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
package forestry.arboriculture.blocks;

import java.util.ArrayList;
import java.util.List;

import forestry.api.arboriculture.EnumVanillaWoodType;

public abstract class BlockFireproofVanillaLog extends BlockForestryLog<EnumVanillaWoodType> {
	public static List<BlockFireproofVanillaLog> create() {
		List<BlockFireproofVanillaLog> blocks = new ArrayList<>();
		PropertyVanillaWoodType[] variants = PropertyVanillaWoodType.create("variant", VARIANTS_PER_BLOCK);
		for (int i = 0; i < variants.length; i++) {
			PropertyVanillaWoodType variant = variants[i];
			BlockFireproofVanillaLog block = new BlockFireproofVanillaLog(i) {
				@Override
				public PropertyVanillaWoodType getVariant() {
					return variant;
				}
			};
			blocks.add(block);
		}
		return blocks;
	}

	private BlockFireproofVanillaLog(int blockNumber) {
		super(true, blockNumber);
	}

	@Override
	public EnumVanillaWoodType getWoodType(int meta) {
		int variantCount = getVariant().getAllowedValues().size();
		int variantMeta = (meta % variantCount) + getBlockNumber() * VARIANTS_PER_BLOCK;
		return EnumVanillaWoodType.byMetadata(variantMeta);
	}
}
