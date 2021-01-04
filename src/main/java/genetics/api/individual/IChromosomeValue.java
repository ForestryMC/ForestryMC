package genetics.api.individual;

public interface IChromosomeValue<V> extends IChromosomeType {
	Class<? extends V> getValueClass();
}
