/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.item.ItemStack;

public class Farmables {
	/**
	 * Can be used to add IFarmables to some of the vanilla farm logics.
	 * <p>
	 * Identifiers: farmArboreal farmWheat farmGourd farmInfernal farmPoales farmSucculentes farmVegetables farmShroom
	 * @deprecated Please use {@link IFarmRegistry#registerFarmables(String, IFarmable...)} and {@link IFarmRegistry#getFarmables(String)}.
	 */
	@Deprecated
	public static final Multimap<String, IFarmable> farmables = HashMultimap.create();

	/**
	  * @deprecated Please use {@link IFarmRegistry#registerFertilizer(ItemStack, int)} and {@link IFarmRegistry#getFertilizeValue(ItemStack)}.
	 */
	@Deprecated
	public static final Map<ItemStack, Integer> fertilizers = new LinkedHashMap<>();

}
