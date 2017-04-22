/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.utils.datastructures;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

/**
 * Map for fluids and items. Can add things like ore dictionary strings etc.
 *
 * @param <T> The thing this map maps onto
 * @param <P> The stack class (i.e. ItemStack, FluidStack) used for lookups
 * @author Alex Binnie
 */
public abstract class StackMap<P, T> extends HashMap<P, T> {

	@Override
	public final T put(P key, T value) {
		Preconditions.checkArgument(isValidKey(key), "Key is invalid");
		Preconditions.checkNotNull(value);
		return super.put(key, value);
	}

	private static final long serialVersionUID = 5383477742290646466L;

	@Override
	public final boolean containsKey(Object key) {
		P stack = getStack(key);
		if (stack == null) {
			return false;
		}
		for (Map.Entry<P, T> entry : this.entrySet()) {
			if (areEqual(stack, entry.getKey())) {
				return true;
			}
		}
		return super.containsKey(key);
	}

	@Override
	@Nullable
	public final T get(Object key) {
		P stack = getStack(key);
		if (stack == null) {
			return null;
		}
		for (Map.Entry<P, T> entry : this.entrySet()) {
			if (areEqual(stack, entry.getKey())) {
				return entry.getValue();
			}
		}
		return super.get(key);
	}

	/**
	 * Is a, the key used to lookup, equivelant to the key b
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	protected abstract boolean areEqual(P a, Object b);

	/**
	 * Can this key be added to the map
	 *
	 * @param key
	 * @return
	 */
	protected abstract boolean isValidKey(Object key);

	/**
	 * Converts a key used to lookup something into P (ItemStack or FluidStack)
	 *
	 * @param key
	 * @return
	 */
	@Nullable
	protected abstract P getStack(Object key);

}
