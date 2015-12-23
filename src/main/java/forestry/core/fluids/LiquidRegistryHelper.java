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

import java.util.EnumMap;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;

import forestry.core.config.Constants;
import forestry.core.items.EnumContainerType;
import forestry.core.items.ItemLiquidContainer;
import forestry.core.utils.Log;
import forestry.plugins.PluginFluids;

public class LiquidRegistryHelper {

	private static final EnumMap<EnumContainerType, ItemLiquidContainer> emptyContainers = new EnumMap<>(EnumContainerType.class);

	static {
		emptyContainers.put(EnumContainerType.CAN, PluginFluids.items.canEmpty);
		emptyContainers.put(EnumContainerType.CAPSULE, PluginFluids.items.waxCapsuleEmpty);
		emptyContainers.put(EnumContainerType.REFRACTORY, PluginFluids.items.refractoryEmpty);
	}

	public static void registerLiquidContainer(Fluids fluid, ItemStack filled) {
		Item item = filled.getItem();
		ItemStack empty = null;

		if (item.getContainerItem() instanceof ItemBucket) {
			empty = new ItemStack(Items.bucket);
		} else if (item instanceof ItemLiquidContainer) {
			ItemLiquidContainer liquidContainer = (ItemLiquidContainer) item;
			EnumContainerType containerType = liquidContainer.getType();
			ItemLiquidContainer emptyContainer = emptyContainers.get(containerType);
			empty = emptyContainer.getItemStack();
		}

		if (empty == null) {
			Log.warning("Unable to inject liquid container: " + filled);
		} else {
			registerLiquidContainer(fluid, Constants.BUCKET_VOLUME, filled, empty);
		}
	}

	public static void registerLiquidContainer(Fluids fluid, int volume, ItemStack filled, ItemStack empty) {
		FluidStack contained = fluid.getFluid(volume);
		if (contained == null) {
			throw new IllegalArgumentException(String.format("Attempted to inject a liquid container for the non-existent liquid '%s'.", fluid));
		}

		FluidContainerData container = new FluidContainerData(contained, filled, empty);
		FluidContainerRegistry.registerFluidContainer(container);
	}

}
