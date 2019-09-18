package forestry.energy.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileEngine;
import forestry.core.tiles.TileRegistry;
import forestry.energy.blocks.BlockTypeEngine;
import forestry.energy.features.EnergyBlocks;

public class TileRegistryEnergy extends TileRegistry {

	public final TileEntityType<TileEngineBiogas> biogasEngine;
	public final TileEntityType<TileEngine> clockworkEngine;
	public final TileEntityType<TileEngine> peatEngine;

	//TODO these need the compat block registry
	//	public final TileEntityType<TileEngine> electricEngine;
	//	public final TileEntityType<TileEuGenerator> generator;

	public TileRegistryEnergy() {
		biogasEngine = registerTileEntityType(TileEngineBiogas::new, "biogas_engine", EnergyBlocks.ENGINES.get(BlockTypeEngine.BIOGAS).block());
		clockworkEngine = registerTileEntityType(TileEngineClockwork::new, "clockwork_engine", EnergyBlocks.ENGINES.get(BlockTypeEngine.CLOCKWORK).block());
		peatEngine = registerTileEntityType(TileEnginePeat::new, "peat_engine", EnergyBlocks.ENGINES.get(BlockTypeEngine.PEAT).block());
	}
}
