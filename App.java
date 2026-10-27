import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;

    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados */
    static Produto[] produtosCadastrados;

    /** Quantidade de produtos cadastrados atualmente no vetor */
    static int quantosProdutos = 0;

    static IOrdenator<Produto> ordenador;

    /** Critérios de busca e ordenação */
    static final int SEM_ORDEM = 0;
    static final int CODIGO = 1;
    static final int DESCRICAO = 2;

    /** Extratores de chave: definem por qual atributo os produtos são buscados ou ordenados */
    static final Function<Produto, Integer> POR_CODIGO = p -> p.hashCode();
    static final Function<Produto, String> POR_DESCRICAO = p -> p.descricao.toLowerCase();

    /** Critério pelo qual o vetor de produtos está ordenado no momento (SEM_ORDEM, CODIGO ou DESCRICAO) */
    static int criterioOrdenacao = SEM_ORDEM;

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }

    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {

    	T valor;

    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }

    /**
     * Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário (-1, se a opção digitada for inválida).
    */
    static int menu() {
        cabecalho();
        System.out.println("Produtos ordenados por: " + nomeCriterio(criterioOrdenacao));
        System.out.println("1 - Ordenar produtos");
        System.out.println("2 - Embaralhar produtos");
        System.out.println("3 - Busca sequencial por produto");
        System.out.println("4 - Busca binária por produto");
        System.out.println("5 - Listar todos os produtos");
        System.out.println("6 - Procurar produtos pelo início do nome");
        System.out.println("7 - Comparar desempenho das buscas");
        System.out.println("0 - Finalizar");

        Integer opcao = lerOpcao("Digite sua opção: ", Integer.class);
        return (opcao == null) ? -1 : opcao;
    }

    static String nomeCriterio(int criterio) {
    	return switch (criterio) {
    		case CODIGO -> "código";
    		case DESCRICAO -> "descrição";
    		default -> "nenhum critério (vetor embaralhado)";
    	};
    }

    /** Pergunta ao usuário o critério (atributo) de busca ou ordenação. */
    static int lerCriterio() {

    	Integer criterio;
    	do {
    		criterio = lerOpcao("Critério: 1 - código | 2 - descrição", Integer.class);
    	} while (criterio == null || (criterio != CODIGO && criterio != DESCRICAO));
    	return criterio;
    }

    /**
     * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto no formato
     * N  (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {

    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	Produto[] produtosCadastrados;

    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));

    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new Produto[numProdutos];

    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			produtosCadastrados[i] = produto;
    		}
    		quantosProdutos = numProdutos;

    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		if (arquivo != null) {
    			arquivo.close();
    		}
    	}

    	return produtosCadastrados;
    }

    /** Mostra o produto encontrado (ou a mensagem de não encontrado) e as medições da busca. */
    private static void mostrarProduto(Produto produto, IMedicao medicao) {

        StringBuilder mensagem = new StringBuilder("Produto não encontrado.\n");

        if (produto != null) {
            mensagem = new StringBuilder(String.format("%s\n", produto));
        }

        mensagem.append(String.format("\nTotal de comparações: %d\n", medicao.getComparacoes()));
        mensagem.append(String.format("Tempo gasto: %.4f milissegundos\n", medicao.getTempo()));

        System.out.println(mensagem.toString());
    }

    /** Ordena o vetor de produtos pelo critério informado, usando o Quicksort fornecido. */
    static void ordenarProdutos(int criterio) {

    	Comparator<Produto> comparador = (criterio == CODIGO)
    			? Comparator.comparing(POR_CODIGO)
    			: Comparator.comparing(POR_DESCRICAO);

    	ordenador = new Quicksort<>();
        produtosCadastrados = ordenador.ordenar(produtosCadastrados, comparador);
        criterioOrdenacao = criterio;
    }

    static void ordenarProdutos() {

    	cabecalho();
    	int criterio = lerCriterio();
    	ordenarProdutos(criterio);
    	System.out.println(String.format("Produtos ordenados por %s: %d comparações, %d movimentações, %.2f ms.",
    			nomeCriterio(criterio), ordenador.getComparacoes(), ordenador.getMovimentacoes(), ordenador.getTempo()));
    }

    /** Embaralha o vetor de produtos (algoritmo de Fisher-Yates). */
    static void embaralharProdutos() {

    	Random sorteio = new Random();
    	for (int i = quantosProdutos - 1; i > 0; i--) {
    		int j = sorteio.nextInt(i + 1);
    		Produto temp = produtosCadastrados[i];
    		produtosCadastrados[i] = produtosCadastrados[j];
    		produtosCadastrados[j] = temp;
    	}
    	criterioOrdenacao = SEM_ORDEM;
    }

    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static void listarTodosOsProdutos() {

        cabecalho();
        System.out.println("\nProdutos cadastrados: ");
        for (int i = 0; i < quantosProdutos; i++) {
        	System.out.println(String.format("%02d - [%d] %s", (i + 1), produtosCadastrados[i].hashCode(), produtosCadastrados[i].toString()));
        }
    }

    /** Executa a busca com o buscador informado e mostra o resultado e as medições. */
    static <K extends Comparable<K>> void buscar(IBuscador<Produto, K> buscador, K chave) {

    	Produto encontrado = null;
    	try {
    		encontrado = buscador.buscar(chave);
    	} catch (NoSuchElementException excecao) {
    		System.out.println(excecao.getMessage());
    	}
    	mostrarProduto(encontrado, buscador);
    }

    /**
     * Pergunta o critério (código ou descrição) e a chave, e localiza o produto por busca sequencial,
     * mostrando o resultado e as medições.
     */
    static void fazerBuscaSequencial() {
    	// TODO (Tarefa 1)
    }

    /**
     * Pergunta o critério (código ou descrição) e a chave, e localiza o produto por busca binária,
     * mostrando o resultado e as medições. Avisa o usuário se o vetor não estiver ordenado pelo critério escolhido.
     */
    static void fazerBuscaBinaria() {
    	// TODO (Tarefa 2)
    }

    /**
     * Lista os produtos cuja descrição começa com o texto informado (sem diferenciar maiúsculas).
     * Exige o vetor ordenado por descrição: busca binária pela primeira posição candidata e,
     * a partir dela, varredura sequencial que para na primeira descrição que não começa com o texto.
     */
    static void localizarProdutosPorInicioDoNome() {
    	// TODO (Tarefa 3)
    }

    /**
     * Compara busca sequencial e binária, por código e por descrição.
     * Chaves: todos os produtos cadastrados e 1000 chaves inexistentes (códigos 1 a 1000;
     * descrições "inexistente 1" a "inexistente 1000"). Antes de cada bateria de busca binária,
     * o vetor é embaralhado e ordenado pelo critério correspondente, e o custo dessa ordenação é exibido
     * (embaralhar antes evita o pior caso do quicksort com pivô na última posição: vetor já ordenado).
     */
    static void compararDesempenhoBuscas() {
    	// TODO (Tarefa 4)
    }

    public static void main(String[] args) {
		teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);

        embaralharProdutos();

        int opcao = -1;

        do{
        	opcao = menu();
            switch (opcao) {
            	case 1 -> ordenarProdutos();
            	case 2 -> embaralharProdutos();
            	case 3 -> fazerBuscaSequencial();
            	case 4 -> fazerBuscaBinaria();
            	case 5 -> listarTodosOsProdutos();
            	case 6 -> localizarProdutosPorInicioDoNome();
            	case 7 -> compararDesempenhoBuscas();
            	case 0 -> System.out.println("FLW VLW OBG VLT SMP.");
            }
            pausa();
        } while (opcao != 0);

        teclado.close();
    }
}
