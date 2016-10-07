package forestry.arboriculture.blocks.planks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.arboriculture.blocks.property.PropertyVanillaWoodType;

public abstract class BlockFireproofVanillaPlanks extends BlockForestryPlanks<EnumVanillaWoodType> {
	public static List<BlockFireproofVanillaPlanks> create() {
		List<BlockFireproofVanillaPlanks> blocks = new ArrayList<>();
		PropertyVanillaWoodType[] variants = PropertyVanillaWoodType.create("variant", VARIANTS_PER_BLOCK);
		for (int i = 0; i < variants.length; i++) {
			PropertyVanillaWoodType variant = variants[i];
			BlockFireproofVanillaPlanks block = new BlockFireproofVanillaPlanks(i) {
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

	private BlockFireproofVanillaPlanks(int blockNumber) {
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
