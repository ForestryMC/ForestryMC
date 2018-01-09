package forestry.cultivation.blocks;

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;

public class BlockRegistryCultivation extends BlockRegistry {
	public final BlockPlanter arboretum;
	public final BlockPlanter farmCrops;
	public final BlockPlanter farmMushroom;
	public final BlockPlanter farmGourd;
	public final BlockPlanter farmNether;
	public final BlockPlanter peatBog;
	/*public final BlockPlanter plantation;*/

	public BlockRegistryCultivation() {
		arboretum = new BlockPlanter(BlockTypePlanter.ARBORETUM);
		registerBlock(arboretum, new ItemBlockForestry<>(arboretum), "arboretum");
		farmCrops = new BlockPlanter(BlockTypePlanter.FARM_CROPS);
		registerBlock(farmCrops, new ItemBlockForestry<>(farmCrops), "farm_crops");
		farmMushroom = new BlockPlanter(BlockTypePlanter.FARM_MUSHROOM);
		registerBlock(farmMushroom, new ItemBlockForestry<>(farmMushroom), "farm_mushroom");
		farmGourd = new BlockPlanter(BlockTypePlanter.FARM_GOURD);
		registerBlock(farmGourd, new ItemBlockForestry<>(farmGourd), "farm_gourd");
		farmNether = new BlockPlanter(BlockTypePlanter.FARM_NETHER);
		registerBlock(farmNether, new ItemBlockForestry<>(farmNether), "farm_nether");
		peatBog = new BlockPlanter(BlockTypePlanter.PEAT_POG);
		registerBlock(peatBog, new ItemBlockForestry<>(peatBog), "peat_bog");
	/*	plantation = new BlockPlanter(BlockTypePlanter.PLANTATION);
		registerBlock(plantation, new ItemBlockForestry<>(plantation), "plantation");*/
	}
}
