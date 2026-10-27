import java.util.Comparator;

public interface IOrdenator<T extends Comparable<T>> extends IMedicao {

	public T[] ordenar(T[] dados);
	public T[] ordenar(T[] dados, Comparator<T> comparador);
    public void setComparador(Comparator<T> comparador);
    public long getMovimentacoes();
}