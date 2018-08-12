package forestry.core.utils.datastructures;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A map that doesn't actually store anything
 */
public class DummyMap<K, V> implements Map<K, V> {
	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean containsKey(Object o) {
		return false;
	}

	@Override
	public boolean containsValue(Object o) {
		return false;
	}

	@Override
	@Nullable
	public V get(Object o) {
		return null;
	}

	@Override
	@Nullable
	public V put(K k, V v) {
		return null;
	}

	@Override
	@Nullable
	public V remove(Object o) {
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {

	}

	@Override
	public void clear() {

	}

	@Override
	public Set<K> keySet() {
		return Collections.emptySet();
	}

	@Override
	public Collection<V> values() {
		return Collections.emptySet();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return Collections.emptySet();
	}
}
