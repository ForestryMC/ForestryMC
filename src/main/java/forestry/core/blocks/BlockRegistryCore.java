/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.blocks;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemStack;

import forestry.core.items.ItemBlockBase;
import forestry.core.items.ItemBlockForestry;

//TODO - tags, harvest levels
public class BlockRegistryCore extends BlockRegistry {
	public final BlockCore analyzer;
	public final BlockCore escritoire;
	public final BlockBogEarth bogEarth;
	public final BlockHumus humus;
	public final Block ashBrick;
	public final StairsBlock ashStairs;
	public final Map<EnumResourceType, BlockResourceStorage> resourceStorage = new EnumMap<>(EnumResourceType.class);
	public final Map<EnumResourceType, BlockResourceOre> resourceOre = new EnumMap<>(EnumResourceType.class);

	public BlockRegistryCore() {
		analyzer = new BlockCore(BlockTypeCoreTesr.ANALYZER);
		registerBlock(analyzer, new ItemBlockBase<>(analyzer, BlockTypeCoreTesr.ANALYZER), "analyzer");

		escritoire = new BlockCore(BlockTypeCoreTesr.ESCRITOIRE);
		registerBlock(escritoire, new ItemBlockBase<>(escritoire, BlockTypeCoreTesr.ESCRITOIRE), "escritoire");

		bogEarth = new BlockBogEarth();
		registerBlock(bogEarth, new ItemBlockForestry<>(bogEarth), "bog_earth");

		humus = new BlockHumus();
		registerBlock(humus, new ItemBlockForestry<>(humus), "humus");

		for (EnumResourceType type : EnumResourceType.VALUES) {
			if (type == EnumResourceType.BRONZE) {
				continue;    //there is no bronze ore
			}
			BlockResourceOre block = new BlockResourceOre(type);
			registerBlock(block, new ItemBlockForestry<>(block), "resource_ore_" + type.getName());
			resourceOre.put(type, block);
		}
		//TODO register tag for these

		for (EnumResourceType type : EnumResourceType.VALUES) {
			BlockResourceStorage block = new BlockResourceStorage(type);
			registerBlock(block, new ItemBlockForestry<>(block), "resource_storage_" + type.getName());
			resourceStorage.put(type, block);
		}
		//TODO register tag for these

		//		OreDictionary.registerOre(OreDictUtil.ORE_APATITE, resources.get(EnumResourceType.APATITE, 1));
		//		OreDictionary.registerOre(OreDictUtil.ORE_COPPER, resources.get(EnumResourceType.COPPER, 1));
		//		OreDictionary.registerOre(OreDictUtil.ORE_TIN, resources.get(EnumResourceType.TIN, 1));

		//		BlockResourceStorage resourceStorage = new BlockResourceStorage();
		//		registerBlock(resourceStorage, new ItemBlockForestry<>(resourceStorage), "resource_storage");

		//		resourceStorageApatite = resourceStorage.get(EnumResourceType.APATITE);
		//		OreDictionary.registerOre(OreDictUtil.BLOCK_APATITE, resourceStorageApatite);

		//		resourceStorageCopper = resourceStorage.get(EnumResourceType.COPPER);
		//		OreDictionary.registerOre(OreDictUtil.BLOCK_COPPER, resourceStorageCopper);

		//		resourceStorageTin = resourceStorage.get(EnumResourceType.TIN);
		//		OreDictionary.registerOre(OreDictUtil.BLOCK_TIN, resourceStorageTin);

		//		resourceStorageBronze = resourceStorage.get(EnumResourceType.BRONZE);
		//		OreDictionary.registerOre(OreDictUtil.BLOCK_BRONZE, resourceStorageBronze);

		ashBrick = new Block(Block.Properties.create(Material.ROCK, MaterialColor.STONE)
			.hardnessAndResistance(2.0f, 10.0f)
			.sound(SoundType.STONE));
		registerBlock(ashBrick, new ItemBlockForestry<>(ashBrick), "ash_brick");

		ashStairs = new BlockStairs(ashBrick.getDefaultState());
		registerBlock(ashStairs, new ItemBlockForestry<>(ashStairs), "ash_stairs");

		// register some common oreDict names for our recipes
		//		OreDictionary.registerOre(OreDictUtil.CRAFTING_TABLE_WOOD, Blocks.CRAFTING_TABLE);
		//		OreDictionary.registerOre(OreDictUtil.TRAPDOOR_WOOD, Blocks.TRAPDOOR);
	}


	public ItemStack getStorage(EnumResourceType type) {
		return new ItemStack(resourceStorage.get(type));
	}

	public ItemStack getOre(EnumResourceType type) {
		return getOre(type, 1);
	}

	public ItemStack getOre(EnumResourceType type, int amount) {
		return new ItemStack(resourceOre.get(type), amount);
	}

}
