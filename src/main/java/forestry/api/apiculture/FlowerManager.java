/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IFlowerRegistry;

public class FlowerManager {
	/** 
	 * <blockquote><pre>e.g. FlowerManager.flowerRegister.registerPlantableFlower(new ItemStack(Blocks.red_flower), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);</pre></blockquote>
	 */
	public static IFlowerRegistry flowerRegistry;

	public static final String FlowerTypeVanilla = "flowersVanilla";
	public static final String FlowerTypeNether = "flowersNether";
	public static final String FlowerTypeCacti = "flowersCacti";
	public static final String FlowerTypeMushrooms = "flowersMushrooms";
	public static final String FlowerTypeEnd = "flowersEnd";
	public static final String FlowerTypeJungle = "flowersJungle";
	public static final String FlowerTypeSnow = "flowersSnow";
	public static final String FlowerTypeWheat = "flowersWheat";
	public static final String FlowerTypeGourd = "flowersGourd";

	/**
	 * @deprecated since Forestry 3.4. Use IFlowerRegistry.registerPlantableFlower instead.
	 * Completely unused since Forestry 4.0, kept for backward compatibility with older mods.
	 */
	@Deprecated
	public static ArrayList<ItemStack> plainFlowers = new ArrayList<ItemStack>();
}
