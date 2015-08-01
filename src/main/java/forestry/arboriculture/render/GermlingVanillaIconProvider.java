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
import forestry.core.config.ForestryItem;

public class GermlingVanillaIconProvider implements IGermlingIconProvider {
	private final int vanillaMap;

	private IIcon icon;

	public GermlingVanillaIconProvider(int vanillaMap) {
		this.vanillaMap = vanillaMap;
	}

	@Override
	public void registerIcons(IIconRegister register) {
		icon = Blocks.sapling.getIcon(0, vanillaMap);
	}

	@Override
	public IIcon getIcon(EnumGermlingType type, int renderPass) {
		if (type == EnumGermlingType.POLLEN) {
			return ForestryItem.pollenCluster.item().getIconFromDamageForRenderPass(0, renderPass);
		}

		return icon;
	}
}
