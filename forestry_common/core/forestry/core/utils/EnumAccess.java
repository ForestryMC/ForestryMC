/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.utils;

import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum EnumAccess {
	SHARED("gui.rule.shared"), VIEWABLE("gui.rule.restricted"), PRIVATE("gui.rule.private");

	private final String name;

	@SideOnly(Side.CLIENT)
	private IIcon icon;

	private EnumAccess(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

}
