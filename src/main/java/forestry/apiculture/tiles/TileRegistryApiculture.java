package forestry.apiculture.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.apiculture.blocks.BlockTypeApiculture;
import forestry.apiculture.features.ApicultureBlocks;
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
		hive = registerTileEntityType(TileHive::new, "hive", ApicultureBlocks.BEEHIVE.getBlocks());
		apiary = registerTileEntityType(TileApiary::new, "apiary", ApicultureBlocks.BASE.get(BlockTypeApiculture.APIARY).block());
		beeHouse = registerTileEntityType(TileBeeHouse::new, "bee_house", ApicultureBlocks.BASE.get(BlockTypeApiculture.BEE_HOUSE).block());
		candle = registerTileEntityType(TileCandle::new, "candle", ApicultureBlocks.CANDLE.block());
		apiaristChest = registerTileEntityType(TileApiaristChest::new, "apiarist_chest", ApicultureBlocks.BEE_CHEST.block());

		alveary = registerTileEntityType(TileAlveary::new, "alveary", ApicultureBlocks.ALVEARY.getBlocks());
	}

}
