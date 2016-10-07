package forestry.arboriculture.blocks.fence;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.arboriculture.blocks.property.PropertyVanillaWoodType;

public abstract class BlockFireproofVanillaFence extends BlockForestryFence<EnumVanillaWoodType> {
	public static List<BlockFireproofVanillaFence> create() {
		List<BlockFireproofVanillaFence> blocks = new ArrayList<>();

		PropertyVanillaWoodType[] variants = PropertyVanillaWoodType.create("variant", VARIANTS_PER_BLOCK);
		for (int i = 0; i < variants.length; i++) {
			PropertyVanillaWoodType variant = variants[i];
			BlockFireproofVanillaFence block = new BlockFireproofVanillaFence(i) {
				@Nonnull
				@Override
				public PropertyVanillaWoodType getVariant() {
					return variant;
				}
			};
			blocks.add(block);
		}
		return blocks;
	}

	private BlockFireproofVanillaFence(int blockNumber) {
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
