package forestry.farming.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileRegistry;
import forestry.farming.ModuleFarming;
import forestry.farming.blocks.BlockRegistryFarming;
import forestry.farming.blocks.EnumFarmBlockType;

public class TileRegistryFarming extends TileRegistry {

	public final TileEntityType<TileFarmControl> control;
	public final TileEntityType<TileFarmGearbox> gearbox;
	public final TileEntityType<TileFarmHatch> hatch;
	public final TileEntityType<TileFarmPlain> plain;
	public final TileEntityType<TileFarmValve> valve;

	public TileRegistryFarming() {
		BlockRegistryFarming blocks = ModuleFarming.getBlocks();

		control = registerTileEntityType(TileFarmControl::new, "control", blocks.farms.row(EnumFarmBlockType.CONTROL).values());
		gearbox = registerTileEntityType(TileFarmGearbox::new, "gearbox", blocks.farms.row(EnumFarmBlockType.GEARBOX).values());
		hatch = registerTileEntityType(TileFarmHatch::new, "hatch", blocks.farms.row(EnumFarmBlockType.HATCH).values());
		plain = registerTileEntityType(TileFarmPlain::new, "plain", blocks.farms.row(EnumFarmBlockType.PLAIN).values());
		valve = registerTileEntityType(TileFarmValve::new, "valve", blocks.farms.row(EnumFarmBlockType.VALVE).values());
	}


}
