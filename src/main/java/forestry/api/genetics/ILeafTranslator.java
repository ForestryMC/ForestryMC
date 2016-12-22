package forestry.api.genetics;

import javax.annotation.Nullable;

import forestry.api.arboriculture.ITree;
import net.minecraft.block.state.IBlockState;

/**
 * Translates blockStates for a single plain leaf block into genetic data.
 * Used by bees and butterflies to convert and pollinate foreign leaf blocks.
 * Kept in a map with Block keys: {@link AlleleManager#leafTranslators}
 */
public interface ILeafTranslator {
	@Nullable
	ITree getTreeFromLeaf(IBlockState leafBlockState);
}
