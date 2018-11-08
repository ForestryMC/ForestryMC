package forestry.arboriculture.blocks;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

import forestry.arboriculture.genetics.TreeDefinition;

public class TreeTypePredicate implements Predicate<TreeDefinition> {
	private final int minMeta;
	private final int maxMeta;

	public TreeTypePredicate(int blockNumber, int variantsPerBlock) {
		this.minMeta = blockNumber * variantsPerBlock;
		this.maxMeta = minMeta + variantsPerBlock - 1;
	}

	@Override
	public boolean apply(@Nullable TreeDefinition treeDefinition) {
		return treeDefinition != null && treeDefinition.getMetadata() >= minMeta && treeDefinition.getMetadata() <= maxMeta;
	}
}
