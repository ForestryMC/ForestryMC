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

import forestry.core.items.ItemBlockForestry;
import forestry.core.utils.OreDictUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class BlockRegistryCore extends BlockRegistry {
	public final BlockCore analyzer;
	public final BlockCore escritoire;
	public final BlockBogEarth bogEarth;
	public final BlockHumus humus;
	public final BlockResourceOre resources;
	public final ItemStack resourceStorageApatite;
	public final ItemStack resourceStorageTin;
	public final ItemStack resourceStorageCopper;
	public final ItemStack resourceStorageBronze;

	public BlockRegistryCore() {
		analyzer = new BlockCore(BlockTypeCoreTesr.ANALYZER);
		registerBlock(analyzer, new ItemBlockForestry<>(analyzer), "analyzer");

		escritoire = new BlockCore(BlockTypeCoreTesr.ESCRITOIRE);
		registerBlock(escritoire, new ItemBlockForestry<>(escritoire), "escritoire");

		bogEarth = new BlockBogEarth();
		registerBlock(bogEarth, new ItemBlockForestry<>(bogEarth), "bog_earth");
		bogEarth.setHarvestLevel("shovel", 0);

		humus = new BlockHumus();
		registerBlock(humus, new ItemBlockForestry<>(humus), "humus");
		humus.setHarvestLevel("shovel", 0);

		resources = new BlockResourceOre();
		registerBlock(resources, new ItemBlockForestry<>(resources), "resources");
		resources.setHarvestLevel("pickaxe", 1);
		OreDictionary.registerOre(OreDictUtil.ORE_APATITE, resources.get(EnumResourceType.APATITE, 1));
		OreDictionary.registerOre(OreDictUtil.ORE_COPPER, resources.get(EnumResourceType.COPPER, 1));
		OreDictionary.registerOre(OreDictUtil.ORE_TIN, resources.get(EnumResourceType.TIN, 1));

		BlockResourceStorage resourceStorage = new BlockResourceStorage();
		registerBlock(resourceStorage, new ItemBlockForestry<>(resourceStorage), "resource_storage");
		resourceStorage.setHarvestLevel("pickaxe", 0);

		resourceStorageApatite = resourceStorage.get(EnumResourceType.APATITE);
		OreDictionary.registerOre(OreDictUtil.BLOCK_APATITE, resourceStorageApatite);

		resourceStorageCopper = resourceStorage.get(EnumResourceType.COPPER);
		OreDictionary.registerOre(OreDictUtil.BLOCK_COPPER, resourceStorageCopper);

		resourceStorageTin = resourceStorage.get(EnumResourceType.TIN);
		OreDictionary.registerOre(OreDictUtil.BLOCK_TIN, resourceStorageTin);

		resourceStorageBronze = resourceStorage.get(EnumResourceType.BRONZE);
		OreDictionary.registerOre(OreDictUtil.BLOCK_BRONZE, resourceStorageBronze);

		// register some common oreDict names for our recipes
		OreDictionary.registerOre(OreDictUtil.CRAFTING_TABLE_WOOD, Blocks.CRAFTING_TABLE);
		OreDictionary.registerOre(OreDictUtil.TRAPDOOR_WOOD, Blocks.TRAPDOOR);
	}
}
