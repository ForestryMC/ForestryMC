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

		control = registerTileEntityType(TileFarmControl::new, "control", blocks.farms.get(EnumFarmBlockType.CONTROL));
		gearbox = registerTileEntityType(TileFarmGearbox::new, "gearbox", blocks.farms.get(EnumFarmBlockType.GEARBOX));
		hatch = registerTileEntityType(TileFarmHatch::new, "hatch", blocks.farms.get(EnumFarmBlockType.HATCH));
		plain = registerTileEntityType(TileFarmPlain::new, "plain", blocks.farms.get(EnumFarmBlockType.PLAIN));
		valve = registerTileEntityType(TileFarmValve::new, "valve", blocks.farms.get(EnumFarmBlockType.VALVE));
	}


}
