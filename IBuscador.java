/**
 * Buscador de elementos do tipo T por uma chave do tipo K.
 * A chave é extraída de cada elemento por uma função (extrator de chave), de modo que o mesmo
 * buscador pode localizar objetos por qualquer um de seus atributos.
 * As medições (comparações e tempo) referem-se à última chamada de buscar.
 *
 * @param <T> Tipo dos elementos pesquisados.
 * @param <K> Tipo da chave de busca.
 */
public interface IBuscador<T, K extends Comparable<K>> extends IMedicao {

	/**
	 * Localiza um elemento cuja chave é igual à chave informada.
	 * @param chave Chave procurada.
	 * @return O elemento encontrado.
	 * @throws java.util.NoSuchElementException se nenhum elemento tiver a chave procurada.
	 */
	public T buscar(K chave);
}
