package forestry.lepidopterology.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileRegistry;
import forestry.lepidopterology.ModuleLepidopterology;
import forestry.lepidopterology.blocks.BlockRegistryLepidopterology;

public class TileRegistryLepidopterology extends TileRegistry {

	public final TileEntityType<TileCocoon> SOLID_COCOON;
	public final TileEntityType<TileCocoon> COCOON;
	public final TileEntityType<TileLepidopteristChest> LEPIDOPTERIST_CHEST;

	public TileRegistryLepidopterology() {
		BlockRegistryLepidopterology blocks = ModuleLepidopterology.getBlocks();

		SOLID_COCOON = registerTileEntityType(() -> new TileCocoon(true), "solid_cocoon", blocks.solidCocoon);
		COCOON = registerTileEntityType(() -> new TileCocoon(false), "cocoon", blocks.cocoon);
		LEPIDOPTERIST_CHEST = registerTileEntityType(TileLepidopteristChest::new, "lepidopterologist_chest", blocks.butterflyChest);
	}
}
