package forestry.climatology.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.climatology.ModuleClimatology;
import forestry.climatology.blocks.BlockRegistryClimatology;
import forestry.core.tiles.TileRegistry;

public class TileRegistryClimatology extends TileRegistry {

	public final TileEntityType<TileHabitatFormer> HABITAT_FORMER;

	public TileRegistryClimatology() {
		BlockRegistryClimatology blocks = ModuleClimatology.getBlocks();
		HABITAT_FORMER = registerTileEntityType(TileHabitatFormer::new, "habitat_former", blocks.habitatformer);
	}
}
