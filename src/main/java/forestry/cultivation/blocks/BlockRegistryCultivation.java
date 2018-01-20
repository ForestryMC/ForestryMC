package forestry.cultivation.blocks;

import java.util.HashSet;
import java.util.Set;

import forestry.core.blocks.BlockRegistry;
import forestry.cultivation.items.ItemBlockPlanter;

public class BlockRegistryCultivation extends BlockRegistry {
	private Set<BlockPlanter> planters = new HashSet<>();

	public final BlockPlanter arboretum;
	public final BlockPlanter farmCrops;
	public final BlockPlanter farmMushroom;
	public final BlockPlanter farmGourd;
	public final BlockPlanter farmNether;
	public final BlockPlanter farmEnder;
	public final BlockPlanter peatBog;
	/*public final BlockPlanter plantation;*/

	public BlockRegistryCultivation() {
		arboretum = new BlockPlanter(BlockTypePlanter.ARBORETUM);
		registerBlock(arboretum, new ItemBlockPlanter(arboretum), "arboretum");
		planters.add(arboretum);

		farmCrops = new BlockPlanter(BlockTypePlanter.FARM_CROPS);
		registerBlock(farmCrops, new ItemBlockPlanter(farmCrops), "farm_crops");
		planters.add(farmCrops);

		farmMushroom = new BlockPlanter(BlockTypePlanter.FARM_MUSHROOM);
		registerBlock(farmMushroom, new ItemBlockPlanter(farmMushroom), "farm_mushroom");
		planters.add(farmMushroom);

		farmGourd = new BlockPlanter(BlockTypePlanter.FARM_GOURD);
		registerBlock(farmGourd, new ItemBlockPlanter(farmGourd), "farm_gourd");
		planters.add(farmGourd);

		farmNether = new BlockPlanter(BlockTypePlanter.FARM_NETHER);
		registerBlock(farmNether, new ItemBlockPlanter(farmNether), "farm_nether");
		planters.add(farmNether);

		farmEnder = new BlockPlanter(BlockTypePlanter.FARM_ENDER);
		registerBlock(farmEnder, new ItemBlockPlanter(farmEnder), "farm_ender");
		planters.add(farmEnder);

		peatBog = new BlockPlanter(BlockTypePlanter.PEAT_POG);
		registerBlock(peatBog, new ItemBlockPlanter(peatBog), "peat_bog");
		planters.add(peatBog);
	/*	plantation = new BlockPlanter(BlockTypePlanter.PLANTATION);
		registerBlock(plantation, new ItemBlockForestry<>(plantation), "plantation");*/
	}

	public Set<BlockPlanter> getPlanters() {
		return planters;
	}
}
