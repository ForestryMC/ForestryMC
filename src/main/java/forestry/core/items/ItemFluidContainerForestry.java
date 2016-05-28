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
package forestry.core.items;

import java.util.Locale;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.FluidContainerRegistryWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IModelManager;
import forestry.core.CreativeTabForestry;
import forestry.core.fluids.FluidHelper;
import forestry.core.utils.Translator;

public class ItemFluidContainerForestry extends ItemForestry implements IColoredItem {
	private final EnumContainerType type;
	private final int color;

	public ItemFluidContainerForestry(EnumContainerType type, int color) {
		super(CreativeTabForestry.tabForestry);
		this.type = type;
		this.color = color;
	}

	/* Models */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		if (color == 0) {
			manager.registerItemModel(item, 0, "liquids/" + type.toString().toLowerCase(Locale.ENGLISH) + "_empty");
		} else {
			manager.registerItemModel(item, 0, "liquids/" + type.toString().toLowerCase(Locale.ENGLISH));
		}
	}

	@Override
	public int getColorFromItemstack(ItemStack itemstack, int j) {
		if (j > 0) {
			return color;
		} else {
			return 0xffffff;
		}
	}

	public EnumContainerType getType() {
		return type;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemFluidContainerForestry) {
			FluidStack fluid = FluidHelper.getFluidStackInContainer(stack);
			if (fluid != null) {
				String exactTranslationKey = "item.for." + type.getName() + '.' + fluid.getFluid().getName() + ".name";
				if (Translator.canTranslateToLocal(exactTranslationKey)) {
					return Translator.translateToLocal(exactTranslationKey);
				} else {
					String grammarKey = "item.for." + type.getName() + ".grammar";
					return Translator.translateToLocalFormatted(grammarKey, fluid.getLocalizedName());
				}
			}
		}
		return super.getItemStackDisplayName(stack);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new FluidContainerRegistryWrapper(stack);
	}
}
