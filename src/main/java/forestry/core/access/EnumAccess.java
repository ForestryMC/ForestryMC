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
package forestry.core.access;

import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum EnumAccess {
	SHARED("gui.rule.shared"), VIEWABLE("gui.rule.restricted"), PRIVATE("gui.rule.private");

	private final String name;

	@SideOnly(Side.CLIENT)
	private IIcon icon;

	EnumAccess(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

}
