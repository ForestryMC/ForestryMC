/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail.items;

import net.minecraft.item.ItemStack;

import forestry.api.mail.EnumPostage;
import forestry.api.mail.IStamps;
import forestry.core.CreativeTabForestry;
import forestry.core.items.ItemOverlay;
import forestry.core.utils.StringUtil;

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
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (itemstack.getItemDamage() < 0 || itemstack.getItemDamage() >= stampInfo.length)
			return null;

		return StringUtil.localize(getUnlocalizedName()) + " " + stampInfo[itemstack.getItemDamage()].name;
	}


}
