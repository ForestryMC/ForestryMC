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
package forestry.mail.items;

import forestry.api.mail.EnumPostage;
import forestry.api.mail.IStamps;
import forestry.core.CreativeTabForestry;
import forestry.core.items.ItemOverlay;
import net.minecraft.item.ItemStack;

public class ItemStamps extends ItemOverlay implements IStamps {

	public static class StampInfo extends OverlayInfo {

		private final Object craftingIngredient;
		private final EnumPostage postage;

		public StampInfo(String name, EnumPostage postage, Object crafting, int primaryColor, int secondaryColor) {
			super(name, primaryColor, secondaryColor);
			this.craftingIngredient = crafting;
			this.postage = postage;
		}

		public EnumPostage getPostage() {
			return this.postage;
		}

		public Object getCraftingIngredient() {
			return this.craftingIngredient;
		}

	}

	private final StampInfo[] stampInfo;

	public ItemStamps(StampInfo[] overlays) {
		super(CreativeTabForestry.tabForestry, overlays);
		this.stampInfo = overlays;
	}

	@Override
	public EnumPostage getPostage(ItemStack itemstack) {
		if (itemstack.getItem() != this)
			return EnumPostage.P_0;

		if (itemstack.getItemDamage() < 0 || itemstack.getItemDamage() >= stampInfo.length)
			return EnumPostage.P_0;

		return stampInfo[itemstack.getItemDamage()].getPostage();
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		if (itemstack.getItemDamage() < 0 || itemstack.getItemDamage() >= stampInfo.length)
			return null;

		return super.getUnlocalizedName(itemstack);
	}


}
