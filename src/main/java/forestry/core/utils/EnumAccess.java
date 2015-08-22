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
package forestry.core.utils;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum EnumAccess {
	SHARED("gui.rule.shared"), VIEWABLE("gui.rule.restricted"), PRIVATE("gui.rule.private");

	private final String name;

	@SideOnly(Side.CLIENT)
	private TextureAtlasSprite icon;

	private EnumAccess(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

}
