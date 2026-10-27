import java.util.NoSuchElementException;
import java.util.function.Function;

public class BuscaBinaria<T, K extends Comparable<K>> implements IBuscador<T, K> {

	private T[] dados;
	private int quantidade;
	private Function<T, K> extratorDeChave;
	private long comparacoes;
	private long inicio;
	private long termino;

	/**
	 * @param dados Vetor pesquisado. Pré-condição: ordenado crescentemente pela MESMA chave
	 *              que extratorDeChave extrai.
	 * @param quantidade Quantidade de posições válidas no vetor.
	 * @param extratorDeChave Função que extrai, de cada elemento, a chave usada na busca.
	 */
	public BuscaBinaria(T[] dados, int quantidade, Function<T, K> extratorDeChave) {
		this.dados = dados;
		this.quantidade = quantidade;
		this.extratorDeChave = extratorDeChave;
	}

	@Override
	public T buscar(K chave) {
		// TODO (Tarefa 2): busca binária; registre comparações e tempo.
		return null;
	}

	/**
	 * Busca binária pela primeira posição cuja chave é maior ou igual à chave informada.
	 * Pré-condição: vetor ordenado crescentemente pela mesma chave.
	 * @return A primeira posição i (0 <= i <= quantidade) tal que a chave de dados[i] >= chave;
	 *         quantidade, se todas as chaves forem menores.
	 */
	public int primeiraPosicaoMaiorOuIgual(K chave) {
		// TODO (Tarefa 3)
		return quantidade;
	}

	@Override
	public long getComparacoes() {
		return comparacoes;
	}

	@Override
	public double getTempo() {
		return (termino - inicio) / 1_000_000.0;
	}
}
