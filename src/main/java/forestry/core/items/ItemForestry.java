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

import java.util.List;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.CreativeTabForestry;
import forestry.core.utils.ItemTooltipUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemForestry extends Item implements IItemModelRegister {
	public ItemForestry() {
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	public ItemForestry(CreativeTabs creativeTab) {
		setCreativeTab(creativeTab);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0);
	}

	public ItemStack getItemStack() {
		return new ItemStack(this);
	}

	public ItemStack getItemStack(int amount) {
		return new ItemStack(this, amount);
	}

	public ItemStack getWildcard() {
		return new ItemStack(this, 1, OreDictionary.WILDCARD_VALUE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		ItemTooltipUtil.addInformation(stack, playerIn, tooltip, advanced);
	}
}
