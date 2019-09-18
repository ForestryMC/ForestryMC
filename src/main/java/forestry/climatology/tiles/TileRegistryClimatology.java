package forestry.climatology.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.climatology.features.ClimatologyBlocks;
import forestry.core.tiles.TileRegistry;

public class TileRegistryClimatology extends TileRegistry {

	public final TileEntityType<TileHabitatFormer> HABITAT_FORMER;

	public TileRegistryClimatology() {
		HABITAT_FORMER = registerTileEntityType(TileHabitatFormer::new, "habitat_former", ClimatologyBlocks.HABITATFORMER.block());
	}
}
