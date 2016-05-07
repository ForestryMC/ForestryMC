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

import net.minecraft.init.Blocks;

import net.minecraftforge.oredict.OreDictionary;

import forestry.core.items.ItemBlockForestry;

public class BlockRegistryCore extends BlockRegistry {
	public final BlockCore core;
	public final BlockSoil soil;
	public final BlockResourceOre resources;
	public final BlockResourceStorage resourceStorage;

	public BlockRegistryCore() {
		core = new BlockCore();
		registerBlock(core, new ItemBlockForestry(core), "core");
		
		soil = new BlockSoil();
		registerBlock(soil, new ItemBlockForestry(soil), "soil");
		soil.setHarvestLevel("shovel", 0, soil.getStateFromMeta(0));
		soil.setHarvestLevel("shovel", 0, soil.getStateFromMeta(1));
		
		resources = new BlockResourceOre();
		registerBlock(resources, new ItemBlockForestry(resources), "resources");
		resources.setHarvestLevel("pickaxe", 1);
		OreDictionary.registerOre("oreApatite", resources.get(EnumResourceType.APATITE, 1));
		OreDictionary.registerOre("oreCopper", resources.get(EnumResourceType.COPPER, 1));
		OreDictionary.registerOre("oreTin", resources.get(EnumResourceType.TIN, 1));
		
		resourceStorage = new BlockResourceStorage();
		registerBlock(resourceStorage, new ItemBlockForestry(resourceStorage), "resourceStorage");
		resourceStorage.setHarvestLevel("pickaxe", 0);
		OreDictionary.registerOre("blockApatite", resourceStorage.get(EnumResourceType.APATITE));
		OreDictionary.registerOre("blockCopper", resourceStorage.get(EnumResourceType.COPPER));
		OreDictionary.registerOre("blockTin", resourceStorage.get(EnumResourceType.TIN));
		OreDictionary.registerOre("blockBronze", resourceStorage.get(EnumResourceType.BRONZE));

		// register some common oreDict names for our recipes
		OreDictionary.registerOre("craftingTableWood", Blocks.CRAFTING_TABLE);
	}
}
