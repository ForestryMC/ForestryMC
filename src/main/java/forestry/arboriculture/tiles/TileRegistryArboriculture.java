package forestry.arboriculture.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.blocks.BlockRegistryArboriculture;
import forestry.core.tiles.TileRegistry;

public class TileRegistryArboriculture extends TileRegistry {

	public final TileEntityType<TileSapling> sapling;
	public final TileEntityType<TileLeaves> leaves;
	public final TileEntityType<TileFruitPod> pods;
	public final TileEntityType<TileArboristChest> arboristChest;

	public TileRegistryArboriculture() {
		BlockRegistryArboriculture blocks = ModuleArboriculture.getBlocks();

		sapling = registerTileEntityType(TileSapling::new, "sapling", blocks.saplingGE);
		leaves = registerTileEntityType(TileLeaves::new, "leaves", blocks.leaves);
		pods = registerTileEntityType(TileFruitPod::new, "pods", blocks.getPods());
		arboristChest = registerTileEntityType(TileArboristChest::new, "arb_chest", blocks.treeChest);
	}
}
