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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IStateMapperRegister;
import forestry.core.models.ModelManager;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginManager;

public abstract class BlockRegistry {
	private final List<Block> allBlocks = new ArrayList<>();

	protected <T extends Block> T registerBlock(T block, ItemBlock itemBlock, String name) {
		if (PluginManager.getStage() != PluginManager.Stage.REGISTER) {
			throw new RuntimeException("Tried to register Block outside of REGISTER");
		}
		block.setUnlocalizedName("for." + name);
		GameRegistry.registerBlock(block, null, name);
		GameRegistry.registerItem(itemBlock, name);
		allBlocks.add(block);

		registerRendering(block);

		return block;
	}

	protected <T extends Block> T registerBlock(T block, Class<? extends ItemBlock> itemClass, String name, Object... itemCtorArgs) {
		if (PluginManager.getStage() != PluginManager.Stage.REGISTER) {
			throw new RuntimeException("Tried to register Block outside of REGISTER");
		}
		block.setUnlocalizedName("for." + name);
		GameRegistry.registerBlock(block, itemClass, StringUtil.cleanBlockName(block), itemCtorArgs);
		allBlocks.add(block);

		registerRendering(block);

		return block;
	}

	private static void registerRendering(Block block) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			if (block instanceof IItemModelRegister) {
				((IItemModelRegister) block).registerModel(Item.getItemFromBlock(block), ModelManager.getInstance());
			}
			if (block instanceof IStateMapperRegister) {
				((IStateMapperRegister) block).registerStateMapper();
			}
		}
	}

	protected static void registerOreDictWildcard(String oreDictName, Block block) {
		OreDictionary.registerOre(oreDictName, new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE));
	}
	
	public List<Block> getAllBlocks() {
		return allBlocks;
	}
}
