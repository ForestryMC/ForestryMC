package forestry.core.utils;

import com.google.common.base.Preconditions;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import net.minecraft.util.IntIdentityHashBiMap;

public class IntBiMap<V> extends IntIdentityHashBiMap<V> {

	public IntBiMap(int initialCapacity) {
		super(initialCapacity);
	}

	public Set<V> toSet(){
		HashSet set = new HashSet();
		forEach((v) -> set.add( v));
		return set;
	}

	public void forEach(BiConsumer<Integer, ? super V> action) {
		Preconditions.checkNotNull(action);
		for (int i = 0;i < size();i++) {
			V v = get(i);
			if(v == null){
				continue;
			}
			action.accept(i, v);
		}
	}
}
