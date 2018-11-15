package forestry.arboriculture.blocks;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

import forestry.api.arboriculture.IWoodType;

public class WoodTypePredicate implements Predicate<IWoodType> {
	private final int minWoodTypeMeta;
	private final int maxWoodTypeMeta;

	public WoodTypePredicate(int blockNumber, int variantsPerBlock) {
		this.minWoodTypeMeta = blockNumber * variantsPerBlock;
		this.maxWoodTypeMeta = minWoodTypeMeta + variantsPerBlock - 1;
	}

	@Override
	public boolean apply(@Nullable IWoodType woodType) {
		return woodType != null && woodType.getMetadata() >= minWoodTypeMeta && woodType.getMetadata() <= maxWoodTypeMeta;
	}
}
