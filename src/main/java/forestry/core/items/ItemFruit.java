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

import forestry.api.core.IModelManager;
import forestry.core.PluginCore;
import forestry.core.utils.OreDictUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFruit extends ItemForestryFood {

	public enum EnumFruit {
		CHERRY(OreDictUtil.CROP_CHERRY),
		WALNUT(OreDictUtil.CROP_WALNUT),
		CHESTNUT(OreDictUtil.CROP_CHESTNUT),
		LEMON(OreDictUtil.CROP_LEMON),
		PLUM(OreDictUtil.CROP_PLUM),
		DATES(OreDictUtil.CROP_DATE),
		PAPAYA(OreDictUtil.CROP_PAPAYA);
		//, COCONUT("cropCoconut");
		public static final EnumFruit[] VALUES = values();

		private final String oreDict;

		EnumFruit(String oreDict) {
			this.oreDict = oreDict;
		}

		@SideOnly(Side.CLIENT)
		public static void registerModel(Item item, IModelManager manager) {
			for (int i = 0; i < VALUES.length; i++) {
				EnumFruit fruit = VALUES[i];
				manager.registerItemModel(item, i, "fruits/" + fruit.name().toLowerCase(Locale.ENGLISH));
			}
		}

		public ItemStack getStack() {
			return getStack(1);
		}

		public ItemStack getStack(int qty) {
			return new ItemStack(PluginCore.getItems().fruits, qty, ordinal());
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

	/* MODELS*/
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		EnumFruit.registerModel(item, manager);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for (int i = 0; i < EnumFruit.values().length; i++) {
			subItems.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (stack.getItemDamage() < 0 || stack.getItemDamage() >= EnumFruit.VALUES.length) {
			return super.getUnlocalizedName(stack);
		}

		return super.getUnlocalizedName(stack) + "." + EnumFruit.VALUES[stack.getItemDamage()].name().toLowerCase(Locale.ENGLISH);
	}

}
