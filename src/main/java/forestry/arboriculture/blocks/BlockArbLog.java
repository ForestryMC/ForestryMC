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

import forestry.api.arboriculture.EnumForestryWoodType;

public abstract class BlockArbLog extends BlockForestryLog<EnumForestryWoodType> {
	public static List<BlockArbLog> create(boolean fireproof) {
		List<BlockArbLog> blocks = new ArrayList<>();
		PropertyForestryWoodType[] variants = PropertyForestryWoodType.create("variant", VARIANTS_PER_BLOCK);
		for (int i = 0; i < variants.length; i++) {
			PropertyForestryWoodType variant = variants[i];
			BlockArbLog block = new BlockArbLog(fireproof, i) {
				@Override
				public PropertyForestryWoodType getVariant() {
					return variant;
				}
			};
			blocks.add(block);
		}
		return blocks;
	}

	private BlockArbLog(boolean fireproof, int blockNumber) {
		super(fireproof, blockNumber);
	}

	@Override
	public EnumForestryWoodType getWoodType(int meta) {
		int variantCount = getVariant().getAllowedValues().size();
		int variantMeta = (meta % variantCount) + getBlockNumber() * VARIANTS_PER_BLOCK;
		return EnumForestryWoodType.byMetadata(variantMeta);
	}
}
