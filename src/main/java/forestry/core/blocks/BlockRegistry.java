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

import javax.annotation.Nullable;

import forestry.core.proxy.Proxies;
import forestry.plugins.PluginManager;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public abstract class BlockRegistry {
	protected <T extends Block> void registerBlock(T block, @Nullable ItemBlock itemBlock, String name) {
		if (PluginManager.getStage() != PluginManager.Stage.REGISTER) {
			throw new RuntimeException("Tried to register Block outside of REGISTER");
		}
		block.setUnlocalizedName("for." + name);
		block.setRegistryName(name);
		GameRegistry.register(block);
		Proxies.common.registerBlock(block);

		if (itemBlock != null) {
			itemBlock.setRegistryName(name);
			GameRegistry.register(itemBlock);
			Proxies.common.registerItem(itemBlock);
		}
	}

	protected <T extends Block> void registerBlock(T block, String name) {
		registerBlock(block, null, name);
	}

	protected static void registerOreDictWildcard(String oreDictName, Block block) {
		OreDictionary.registerOre(oreDictName, new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE));
	}

}
