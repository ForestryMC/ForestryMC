/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.utils;

import java.util.HashMap;
import java.util.Map;


/**
 * Map for fluids and items. Can add things like ore dictionary strings etc.
 * @author Alex Binnie
 *
 * @param <T> The thing this map maps onto
 * @param <P> The stack class (i.e. ItemStack, FluidStack) used for lookups
 */
public abstract class StackMap<P, T> extends HashMap<Object, T>{
	
	@Override
	public final T put(Object key, T value) {
		if(isValidKey(key) && key != null && value != null)
			return super.put(key, value);
		return null;
	}

	private static final long serialVersionUID = 5383477742290646466L;

	@Override
	public final boolean containsKey(Object key) {
		P stack = getStack(key);
		if(stack == null) return false;
		for (Map.Entry<Object, T> entry : this.entrySet())
			if (areEqual(stack, entry.getKey()))
				return true;
		return super.containsKey(key);
	}

	@Override
	public final T get(Object key) {
		P stack = getStack(key);
		if(stack == null) return null;
		for (Map.Entry<Object, T> entry : this.entrySet())
			if (areEqual(stack, entry.getKey()))
				return entry.getValue();
		return super.get(key);
	}

	/**
	 * Is a, the key used to lookup, equivelant to the key b
	 * @param a
	 * @param b
	 * @return
	 */
	protected abstract boolean areEqual(P a, Object b);
	
	/**
	 * Can this key be added to the map
	 * @param key
	 * @return
	 */
	protected abstract boolean isValidKey(Object key);

	/**
	 * Converts a key used to lookup something into P (ItemStack or FluidStack)
	 * @param key
	 * @return
	 */
	protected abstract P getStack(Object key);

}
