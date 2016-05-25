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
package forestry.arboriculture.blocks.slab;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IStateMapperRegister;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.blocks.property.PropertyForestryWoodType;

public abstract class BlockArbSlab extends BlockForestrySlab<EnumForestryWoodType> implements IWoodTyped, IItemModelRegister, IStateMapperRegister {
	public static List<BlockArbSlab> create(boolean fireproof, final boolean doubleSlab) {
		List<BlockArbSlab> blocks = new ArrayList<>();
		PropertyForestryWoodType[] variants = PropertyForestryWoodType.create("variant", VARIANTS_PER_BLOCK);
		for (int i = 0; i < variants.length; i++) {
			PropertyForestryWoodType variant = variants[i];
			BlockArbSlab block = new BlockArbSlab(fireproof, i) {
				@Nonnull
				@Override
				public PropertyForestryWoodType getVariant() {
					return variant;
				}

				@Override
				public boolean isDouble() {
					return doubleSlab;
				}
			};
			blocks.add(block);
		}
		return blocks;
	}

	private BlockArbSlab(boolean fireproof, int blockNumber) {
		super(fireproof, blockNumber);
	}

	@Nonnull
	@Override
	public EnumForestryWoodType getWoodType(int meta) {
		int variantMeta = (meta & VARIANTS_META_MASK) + getBlockNumber() * VARIANTS_PER_BLOCK;
		return EnumForestryWoodType.byMetadata(variantMeta);
	}
}
