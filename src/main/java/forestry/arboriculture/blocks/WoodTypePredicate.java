package forestry.arboriculture.blocks;

import com.google.common.base.Predicate;

import forestry.api.arboriculture.EnumWoodType;

public class WoodTypePredicate implements Predicate<EnumWoodType> {
	private final int minWoodTypeMeta;
	private final int maxWoodTypeMeta;

	public WoodTypePredicate(int blockNumber, int variantsPerBlock) {
		this.minWoodTypeMeta = blockNumber * variantsPerBlock;
		this.maxWoodTypeMeta = minWoodTypeMeta + variantsPerBlock - 1;
	}

	@Override
	public boolean apply(EnumWoodType woodType) {
		return woodType.getMetadata() >= minWoodTypeMeta && woodType.getMetadata() <= maxWoodTypeMeta;
	}
}
