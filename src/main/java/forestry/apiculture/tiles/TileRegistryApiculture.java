package forestry.apiculture.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.blocks.BlockRegistryApiculture;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.core.tiles.TileRegistry;

public class TileRegistryApiculture extends TileRegistry {


	public final TileEntityType<TileHive> hive;
	public final TileEntityType<TileApiary> apiary;
	public final TileEntityType<TileBeeHouse> beeHouse;
	public final TileEntityType<TileCandle> candle;
	public final TileEntityType<TileApiaristChest> apiaristChest;

	public final TileEntityType<TileAlveary> alveary; //TODO think I need to specify the types separately?

	public TileRegistryApiculture() {
		BlockRegistryApiculture blocks = ModuleApiculture.getBlocks();

		hive = registerTileEntityType(TileHive::new, "hive", blocks.beehives.values());
		apiary = registerTileEntityType(TileApiary::new, "apiary", blocks.apiary);
		beeHouse = registerTileEntityType(TileBeeHouse::new, "bee_house", blocks.beeHouse);
		candle = registerTileEntityType(TileCandle::new, "candle", blocks.candle);
		apiaristChest = registerTileEntityType(TileApiaristChest::new, "apiarist_chest", blocks.beeChest);

		alveary = registerTileEntityType(TileAlveary::new, "alveary", blocks.alvearyBlockMap.values());
	}

}
