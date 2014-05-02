/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class ItemMisc extends ItemForestry {

	String[] definition = new String[] { "pulsatingDust", "pulsatingMesh", "silkWisp", "wovenSilk", "dissipationCharge", "iceShard", "scentedPaneling" };

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
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (itemstack.getItemDamage() >= definition.length || itemstack.getItemDamage() < 0)
			return StringUtil.localize("item.unknown");
		else
			return StringUtil.localize("item." + definition[itemstack.getItemDamage()]);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < 7; i++)
			itemList.add(new ItemStack(this, 1, i));
	}

}
