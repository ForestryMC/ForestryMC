package forestry.arboriculture.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.core.tiles.TileRegistry;

public class TileRegistryArboriculture extends TileRegistry {

	public final TileEntityType<TileSapling> sapling;
	public final TileEntityType<TileLeaves> leaves;
	public final TileEntityType<TileFruitPod> pods;
	public final TileEntityType<TileArboristChest> arboristChest;

	public TileRegistryArboriculture() {
		sapling = registerTileEntityType(TileSapling::new, "sapling", ArboricultureBlocks.SAPLING_GE.block());
		leaves = registerTileEntityType(TileLeaves::new, "leaves", ArboricultureBlocks.LEAVES.block());
		pods = registerTileEntityType(TileFruitPod::new, "pods", ArboricultureBlocks.PODS.getBlocks());
		arboristChest = registerTileEntityType(TileArboristChest::new, "arb_chest", ArboricultureBlocks.TREE_CHEST.block());
	}
}
