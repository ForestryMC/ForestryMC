/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.pipes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.core.config.ForestryItem;
import forestry.core.render.TextureManager;
import java.util.Locale;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public enum EnumFilterType {

	CLOSED, ANYTHING, ITEM, BEE, DRONE, PRINCESS, QUEEN, PURE_BREED, NOCTURNAL, PURE_NOCTURNAL, FLYER, PURE_FLYER, CAVE, PURE_CAVE, NATURAL;

	public static EnumFilterType getType(ItemStack stack) {
		if (ForestryItem.beeDroneGE.isItemEqual(stack))
			return DRONE;
		if (ForestryItem.beePrincessGE.isItemEqual(stack))
			return PRINCESS;
		if (ForestryItem.beeQueenGE.isItemEqual(stack))
			return QUEEN;

		return ITEM;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return TextureManager.getInstance().getDefault("analyzer/" + this.toString().toLowerCase(Locale.ENGLISH));
	}
}
