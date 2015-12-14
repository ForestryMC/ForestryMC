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
import forestry.core.items.ItemBlockTyped;

public class BlockRegistryCore extends BlockRegistry {
	public final BlockCore core;
	public final BlockSoil soil;
	public final BlockResourceOre resources;
	public final BlockResourceStorage resourceStorage;

	public BlockRegistryCore() {
		core = registerBlock(new BlockCore(), ItemBlockForestry.class, "core");
		
		soil = registerBlock(new BlockSoil(), ItemBlockTyped.class, "soil");
		soil.setHarvestLevel("shovel", 0, 0);
		soil.setHarvestLevel("shovel", 0, 1);
		
		resources = registerBlock(new BlockResourceOre(), ItemBlockForestry.class, "resources");
		resources.setHarvestLevel("pickaxe", 1);
		OreDictionary.registerOre("oreApatite", resources.get(BlockResourceOre.ResourceType.APATITE, 1));
		OreDictionary.registerOre("oreCopper", resources.get(BlockResourceOre.ResourceType.COPPER, 1));
		OreDictionary.registerOre("oreTin", resources.get(BlockResourceOre.ResourceType.TIN, 1));
		
		resourceStorage = registerBlock(new BlockResourceStorage(), ItemBlockForestry.class, "resourceStorage");
		resourceStorage.setHarvestLevel("pickaxe", 0);
		OreDictionary.registerOre("blockApatite", resourceStorage.get(BlockResourceStorage.ResourceType.APATITE));
		OreDictionary.registerOre("blockCopper", resourceStorage.get(BlockResourceStorage.ResourceType.COPPER));
		OreDictionary.registerOre("blockTin", resourceStorage.get(BlockResourceStorage.ResourceType.TIN));
		OreDictionary.registerOre("blockBronze", resourceStorage.get(BlockResourceStorage.ResourceType.BRONZE));

		// register some common oreDict names for our recipes
		OreDictionary.registerOre("chestWood", Blocks.chest);
		OreDictionary.registerOre("craftingTableWood", Blocks.crafting_table);
	}
}
