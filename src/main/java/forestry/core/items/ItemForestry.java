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

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.ItemGroupForestry;
import forestry.core.utils.ItemTooltipUtil;

public class ItemForestry extends Item {

	private final int burnTime;

	public ItemForestry() {
		this(ItemGroupForestry.tabForestry);
	}

	public ItemForestry(CreativeModeTab group) {
		this(new Item.Properties(), group);
	}

	public ItemForestry(Item.Properties properties, CreativeModeTab creativeTab) {
		this(properties.tab(creativeTab));
	}

	public ItemForestry(Item.Properties properties) {
		super(properties);
		if (properties instanceof ItemProperties) {
			this.burnTime = ((ItemProperties) properties).burnTime;
		} else {
			burnTime = 0;
		}
	}

	@Override
	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
		return burnTime;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced) {
		super.appendHoverText(stack, world, tooltip, advanced);
		ItemTooltipUtil.addInformation(stack, world, tooltip, advanced);
	}

}
