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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.core.render.TextureManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class ItemMisc extends ItemForestry {

	private final String[] definition = new String[]{"pulsatingDust", "pulsatingMesh", "silkWisp", "wovenSilk", "dissipationCharge", "iceShard", "scentedPaneling"};

	public ItemMisc() {
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
		if (stack.getItemDamage() >= definition.length || stack.getItemDamage() < 0)
			return "item.forestry.unknown";
		else
			return super.getUnlocalizedName(stack) + "." + definition[stack.getItemDamage()];
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {

		icons = new IIcon[definition.length];
		for (int i = 0; i < definition.length; i++)
			icons[i] = TextureManager.getInstance().registerTex(register, definition[i]);

	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		if (damage >= definition.length || damage < 0)
			return icons[0];
		else
			return icons[damage];
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < 7; i++)
			itemList.add(new ItemStack(this, 1, i));
	}

}
