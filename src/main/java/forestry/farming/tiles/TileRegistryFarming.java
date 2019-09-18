package forestry.farming.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileRegistry;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.features.FarmingBlocks;

public class TileRegistryFarming extends TileRegistry {

	public final TileEntityType<TileFarmControl> control;
	public final TileEntityType<TileFarmGearbox> gearbox;
	public final TileEntityType<TileFarmHatch> hatch;
	public final TileEntityType<TileFarmPlain> plain;
	public final TileEntityType<TileFarmValve> valve;

	public TileRegistryFarming() {
		control = registerTileEntityType(TileFarmControl::new, "control", FarmingBlocks.FARM.getRowBlocks(EnumFarmBlockType.CONTROL));
		gearbox = registerTileEntityType(TileFarmGearbox::new, "gearbox", FarmingBlocks.FARM.getRowBlocks(EnumFarmBlockType.GEARBOX));
		hatch = registerTileEntityType(TileFarmHatch::new, "hatch", FarmingBlocks.FARM.getRowBlocks(EnumFarmBlockType.HATCH));
		plain = registerTileEntityType(TileFarmPlain::new, "plain", FarmingBlocks.FARM.getRowBlocks(EnumFarmBlockType.PLAIN));
		valve = registerTileEntityType(TileFarmValve::new, "valve", FarmingBlocks.FARM.getRowBlocks(EnumFarmBlockType.VALVE));
	}


}
