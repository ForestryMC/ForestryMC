/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class Farmables {
	/**
	 * Can be used to add IFarmables to some of the vanilla farm logics.
	 *
	 * Identifiers: farmArboreal farmWheat farmGourd farmInfernal farmPoales farmSucculentes farmVegetables farmShroom
	 */
	@Nonnull
	public static final Multimap<String, IFarmable> farmables = HashMultimap.create();
	
	@Nonnull
	private static final LinkedHashMap<ItemStack, Integer> fertilizers = new LinkedHashMap();

	public static void registerFertilizer(ItemStack fertilizerItem, int fertilizerValue){
		if(fertilizerItem == null || fertilizerItem.getItem() == null || fertilizerValue <= 0){
			return;
		}
		fertilizers.put(fertilizerItem, fertilizerValue);
	}
	
	public static void clearFertilizers(){
		fertilizers.clear();
	}
	
	public static Map<ItemStack, Integer> getFertilizers() {
		return Collections.unmodifiableMap(fertilizers);
	}
	
}
