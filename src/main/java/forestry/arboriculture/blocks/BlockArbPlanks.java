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

public abstract class BlockArbPlanks extends BlockForestryPlanks<EnumForestryWoodType> {
	public static List<BlockArbPlanks> create(boolean fireproof) {
		List<BlockArbPlanks> blocks = new ArrayList<>();
		PropertyForestryWoodType[] variants = PropertyForestryWoodType.create("variant", VARIANTS_PER_BLOCK);
		for (int i = 0; i < variants.length; i++) {
			PropertyForestryWoodType variant = variants[i];
			BlockArbPlanks block = new BlockArbPlanks(fireproof, i) {
				@Override
				public PropertyForestryWoodType getVariant() {
					return variant;
				}
			};
			blocks.add(block);
		}
		return blocks;
	}

	private BlockArbPlanks(boolean fireproof, int blockNumber) {
		super(fireproof, blockNumber);
	}

	@Override
	public EnumForestryWoodType getWoodType(int meta) {
		int variantCount = getVariant().getAllowedValues().size();
		int variantMeta = (meta % variantCount) + getBlockNumber() * VARIANTS_PER_BLOCK;
		return EnumForestryWoodType.byMetadata(variantMeta);
	}
}
