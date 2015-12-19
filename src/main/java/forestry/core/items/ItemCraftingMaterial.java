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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;

public class ItemCraftingMaterial extends ItemForestry {

	private final String[] definition = new String[]{"pulsatingDust", "pulsatingMesh", "silkWisp", "wovenSilk", "dissipationCharge", "iceShard", "scentedPaneling"};

	public ItemCraftingMaterial() {
		super();
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (stack.getItemDamage() >= definition.length || stack.getItemDamage() < 0) {
			return "item.forestry.unknown";
		} else {
			return super.getUnlocalizedName(stack) + "." + definition[stack.getItemDamage()];
		}
	}

	/* Models */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < definition.length; i++) {
			manager.registerItemModel(item, i, "/" + definition[i]);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < 7; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}

	public ItemStack getPulsatingDust() {
		return new ItemStack(this, 1, 0);
	}

	public ItemStack getPulsatingMesh() {
		return new ItemStack(this, 1, 1);
	}

	public ItemStack getSilkWisp() {
		return new ItemStack(this, 1, 2);
	}

	public ItemStack getWovenSilk() {
		return new ItemStack(this, 1, 3);
	}

	public ItemStack getDissipationCharge() {
		return new ItemStack(this, 1, 4);
	}

	public ItemStack getIceShard(int amount) {
		return new ItemStack(this, amount, 5);
	}

	public ItemStack getScentedPaneling() {
		return new ItemStack(this, 1, 6);
	}
}
