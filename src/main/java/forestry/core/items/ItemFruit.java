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
import java.util.Locale;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.render.TextureManager;
import forestry.plugins.PluginCore;

public class ItemFruit extends ItemForestryFood {

	public enum EnumFruit {
		CHERRY("cropCherry"), WALNUT("cropWalnut"), CHESTNUT("cropChestnut"), LEMON("cropLemon"), PLUM("cropPlum"), DATES("cropDate"), PAPAYA("cropPapaya");//, COCONUT("cropCoconut");
		public static final EnumFruit[] VALUES = values();

		private final String oreDict;

		EnumFruit(String oreDict) {
			this.oreDict = oreDict;
		}

		private static IIcon[] icons;

		public static void registerIcons(IIconRegister register) {
			icons = new IIcon[VALUES.length];
			for (int i = 0; i < VALUES.length; i++) {
				icons[i] = TextureManager.registerTex(register, "fruits/" + VALUES[i].toString().toLowerCase(Locale.ENGLISH));
			}
		}

		public IIcon getIcon() {
			return icons[ordinal()];
		}

		public ItemStack getStack() {
			return getStack(1);
		}

		public ItemStack getStack(int qty) {
			return new ItemStack(PluginCore.items.fruits, qty, ordinal());
		}

		public String getOreDict() {
			return oreDict;
		}
	}

	public ItemFruit() {
		super(1, 0.2f);
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

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		EnumFruit.registerIcons(register);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int meta) {
		return EnumFruit.values()[meta].getIcon();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < EnumFruit.values().length; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (stack.getItemDamage() < 0 || stack.getItemDamage() >= EnumFruit.VALUES.length) {
			return null;
		}

		return super.getUnlocalizedName(stack) + "." + EnumFruit.VALUES[stack.getItemDamage()].name().toLowerCase(Locale.ENGLISH);
	}

}
