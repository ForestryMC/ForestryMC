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
package forestry.core.fluids;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.config.ForestryItem;
import forestry.core.items.ItemLiquidContainer;
import forestry.core.utils.Log;

public class LiquidRegistryHelper {

	public static void registerLiquidContainer(Fluids fluid, ItemStack filled) {
		Item item = filled.getItem();
		if (item.getContainerItem() instanceof ItemBucket) {
			LiquidRegistryHelper.registerLiquidContainer(fluid, Constants.BUCKET_VOLUME, filled, new ItemStack(Items.bucket));
			return;
		} else if (item instanceof ItemLiquidContainer) {
			ItemLiquidContainer liquidContainer = (ItemLiquidContainer) item;
			switch (liquidContainer.getType()) {
				case CAN:
					LiquidRegistryHelper.registerTinContainer(fluid, Constants.BUCKET_VOLUME, filled, ForestryItem.canEmpty.getItemStack());
					return;
				case CAPSULE:
					LiquidRegistryHelper.registerWaxContainer(fluid, Constants.BUCKET_VOLUME, filled, ForestryItem.waxCapsule.getItemStack());
					return;
				case REFRACTORY:
					LiquidRegistryHelper.registerRefractoryContainer(fluid, Constants.BUCKET_VOLUME, filled, ForestryItem.refractoryEmpty.getItemStack());
					return;
			}
		}
		Log.warning("Unable to inject liquid container: " + filled);
	}

	public static void registerLiquidContainer(Fluids fluid, int volume, ItemStack filled, ItemStack empty) {
		registerLiquidContainer(fluid, volume, filled, empty, null, 0);
	}

	public static void registerWaxContainer(Fluids fluid, int volume, ItemStack filled, ItemStack empty) {
		registerLiquidContainer(fluid, volume, filled, empty, ForestryItem.beeswax.getItemStack(), 10);
	}

	public static void registerRefractoryContainer(Fluids fluid, int volume, ItemStack filled, ItemStack empty) {
		registerLiquidContainer(fluid, volume, filled, empty, ForestryItem.refractoryWax.getItemStack(), 10);
	}

	public static void registerTinContainer(Fluids fluid, int volume, ItemStack filled, ItemStack empty) {
		registerLiquidContainer(fluid, volume, filled, empty, ForestryItem.ingotTin.getItemStack(), 5);
	}

	public static void registerLiquidContainer(Fluids fluid, int volume, ItemStack filled, ItemStack empty, ItemStack remnant, int chance) {
		FluidStack contained = fluid.getFluid(volume);
		if (contained == null) {
			throw new IllegalArgumentException(String.format("Attempted to inject a liquid container for the non-existent liquid '%s'.", fluid));
		}

		FluidContainerData container = new FluidContainerData(contained, filled, empty);
		FluidContainerRegistry.registerFluidContainer(container);

		if (RecipeManagers.squeezerManager != null) {
			if (!container.filledContainer.getItem().hasContainerItem(container.filledContainer)) {
				if (remnant != null) {
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{container.filledContainer}, container.fluid, remnant, chance);
				} else {
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{container.filledContainer}, container.fluid);
				}
			}
		}
	}

}
