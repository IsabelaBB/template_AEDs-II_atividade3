public interface IMedicao {

	/** @return Quantidade de comparações de chave realizadas na última operação. */
	public long getComparacoes();

	/** @return Tempo gasto na última operação, em milissegundos. */
	public double getTempo();
}
