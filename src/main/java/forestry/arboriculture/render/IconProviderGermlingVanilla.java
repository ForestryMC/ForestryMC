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
package forestry.arboriculture.render;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IGermlingIconProvider;
import forestry.core.render.TextureManager;

public class IconProviderGermlingVanilla implements IGermlingIconProvider {
	private final int vanillaMap;

	private IIcon icon;
	private IIcon[] pollenIcons;

	public IconProviderGermlingVanilla(int vanillaMap) {
		this.vanillaMap = vanillaMap;
	}

	@Override
	public void registerIcons(IIconRegister register) {
		icon = Blocks.sapling.getIcon(0, vanillaMap);
		pollenIcons = new IIcon[2];
		pollenIcons[0] = TextureManager.registerTex(register, "germlings/pollen.0");
		pollenIcons[1] = TextureManager.registerTex(register, "germlings/pollen.1");
	}

	@Override
	public IIcon getIcon(EnumGermlingType type, int renderPass) {
		if (type == EnumGermlingType.POLLEN) {
			return pollenIcons[renderPass];
		}

		return icon;
	}
}
