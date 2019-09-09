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
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

import net.minecraftforge.registries.ForgeRegistries;

import forestry.core.proxy.Proxies;
import forestry.core.utils.Log;

public abstract class BlockRegistry {
	protected <T extends Block> void registerBlock(T block, @Nullable BlockItem itemBlock, String name) {

		if (!name.equals(name.toLowerCase(Locale.ENGLISH))) {
			Log.error("Name must be lowercase");
		}

		//TODO - are these done by registry name now?
		//https://gist.github.com/williewillus/353c872bcf1a6ace9921189f6100d09a#lang-changes
		//		block.setTranslationKey("for." + name);
		block.setRegistryName(name);
		ForgeRegistries.BLOCKS.register(block);
		Proxies.common.registerBlock(block);


		if (itemBlock != null) {
			itemBlock.setRegistryName(name);
			ForgeRegistries.ITEMS.register(itemBlock);
			Proxies.common.registerItem(itemBlock);
		}
	}

	protected <T extends Block> void registerBlock(T block, String name) {
		registerBlock(block, null, name);
	}

}
