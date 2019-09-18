package forestry.lepidopterology.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileRegistry;
import forestry.lepidopterology.features.LepidopterologyBlocks;

public class TileRegistryLepidopterology extends TileRegistry {

	public final TileEntityType<TileCocoon> SOLID_COCOON;
	public final TileEntityType<TileCocoon> COCOON;
	public final TileEntityType<TileLepidopteristChest> LEPIDOPTERIST_CHEST;

	public TileRegistryLepidopterology() {
		SOLID_COCOON = registerTileEntityType(() -> new TileCocoon(true), "solid_cocoon", LepidopterologyBlocks.COCOON_SOLID.block());
		COCOON = registerTileEntityType(() -> new TileCocoon(false), "cocoon", LepidopterologyBlocks.COCOON.block());
		LEPIDOPTERIST_CHEST = registerTileEntityType(TileLepidopteristChest::new, "lepidopterologist_chest", LepidopterologyBlocks.BUTTERFLY_CHEST.block());
	}
}
