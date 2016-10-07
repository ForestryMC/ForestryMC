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

import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IStateMapperRegister;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.blocks.property.PropertyVanillaWoodType;

public abstract class BlockFireproofVanillaSlab extends BlockForestrySlab<EnumVanillaWoodType> implements IWoodTyped, IItemModelRegister, IStateMapperRegister {
	public static List<BlockFireproofVanillaSlab> create(final boolean doubleSlab) {
		List<BlockFireproofVanillaSlab> blocks = new ArrayList<>();
		PropertyVanillaWoodType[] variants = PropertyVanillaWoodType.create("variant", VARIANTS_PER_BLOCK);
		for (int i = 0; i < variants.length; i++) {
			PropertyVanillaWoodType variant = variants[i];
			BlockFireproofVanillaSlab block = new BlockFireproofVanillaSlab(i) {
				@Nonnull
				@Override
				public PropertyVanillaWoodType getVariant() {
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

	private BlockFireproofVanillaSlab(int blockNumber) {
		super(true, blockNumber);
	}

	@Nonnull
	@Override
	public EnumVanillaWoodType getWoodType(int meta) {
		int variantCount = getVariant().getAllowedValues().size();
		int variantMeta = (meta % variantCount) + getBlockNumber() * VARIANTS_PER_BLOCK;
		return EnumVanillaWoodType.byMetadata(variantMeta);
	}
}
