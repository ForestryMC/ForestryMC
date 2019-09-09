package forestry.energy.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileEngine;
import forestry.core.tiles.TileRegistry;
import forestry.energy.ModuleEnergy;
import forestry.energy.blocks.BlockRegistryEnergy;

public class TileRegistryEnergy extends TileRegistry {

	public final TileEntityType<TileEngineBiogas> biogasEngine;
	public final TileEntityType<TileEngine> clockworkEngine;
	public final TileEntityType<TileEngine> peatEngine;

	//TODO these need the compat block registry
	//	public final TileEntityType<TileEngine> electricEngine;
	//	public final TileEntityType<TileEuGenerator> generator;

	public TileRegistryEnergy() {
		BlockRegistryEnergy blocks = ModuleEnergy.getBlocks();

		biogasEngine = registerTileEntityType(TileEngineBiogas::new, "biogas_engine", blocks.biogasEngine);
		clockworkEngine = registerTileEntityType(TileEngineClockwork::new, "clockwork_engine", blocks.clockworkEngine);
		peatEngine = registerTileEntityType(TileEnginePeat::new, "peat_engine", blocks.peatEngine);
	}
}
