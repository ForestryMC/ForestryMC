package forestry.core.gui.elements;

public interface IProviderElement<V> {

    V getValue();

    boolean setValue(V value);
}
