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
package forestry.apiculture.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IModelManager;
import forestry.api.core.Tabs;
import forestry.core.config.Config;
import forestry.core.items.ItemForestry;

public class ItemHoneyComb extends ItemForestry {
	public ItemHoneyComb() {
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < EnumHoneyComb.VALUES.length; i++) {
			manager.registerItemModel(item, i, "beecombs/" + EnumHoneyComb.VALUES[i].name);
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		EnumHoneyComb honeyComb = EnumHoneyComb.VALUES[stack.getItemDamage()];
		return super.getUnlocalizedName(stack) + "." + honeyComb.name;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {
		EnumHoneyComb honeyComb = EnumHoneyComb.VALUES[itemstack.getItemDamage()];
		if (j == 1) {
			return honeyComb.primaryColor;
		} else {
			return honeyComb.secondaryColor;
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
		for (int i = 0; i < EnumHoneyComb.VALUES.length; i++) {
			EnumHoneyComb honeyComb = EnumHoneyComb.VALUES[i];
			if (!honeyComb.isSecret() || Config.isDebug) {
				itemList.add(new ItemStack(this, 1, i));
			}
		}
	}

	private static EnumHoneyComb getRandomCombType(Random random, boolean includeSecret) {
		List<EnumHoneyComb> validCombs = new ArrayList<>(EnumHoneyComb.VALUES.length);
		for (int i = 0; i < EnumHoneyComb.VALUES.length; i++) {
			EnumHoneyComb honeyComb = EnumHoneyComb.VALUES[i];
			if (!honeyComb.isSecret() || includeSecret) {
				validCombs.add(honeyComb);
			}
		}

		if (validCombs.isEmpty()) {
			return null;
		} else {
			return validCombs.get(random.nextInt(validCombs.size()));
		}
	}

	public ItemStack getRandomComb(int amount, Random random, boolean includeSecret) {
		EnumHoneyComb honeyComb = getRandomCombType(random, includeSecret);
		return get(honeyComb, amount);
	}

	public ItemStack get(EnumHoneyComb honeyComb, int amount) {
		return new ItemStack(this, amount, honeyComb.ordinal());
	}
}
