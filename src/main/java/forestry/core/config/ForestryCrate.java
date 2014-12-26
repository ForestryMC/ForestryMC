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
package forestry.core.config;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.core.items.ItemCrated;
import forestry.core.proxy.Proxies;
import forestry.plugins.PluginManager;
import forestry.plugins.PluginStorage;

public class ForestryCrate {

	public static ItemCrated registerCrate(Item item, String name) {
		return registerCrate(new ItemCrated(new ItemStack(item)), name);
	}

	public static ItemCrated registerCrate(Block block, String name) {
		return registerCrate(new ItemCrated(new ItemStack(block)), name);
	}

	public static ItemCrated registerCrate(ItemStack stack, String name) {
		return registerCrate(new ItemCrated(stack), name);
	}

	public static ItemCrated registerCrate(ItemCrated crate, String name) {
		if (!EnumSet.of(PluginManager.Stage.PRE_INIT, PluginManager.Stage.INIT).contains(PluginManager.getStage()))
			throw new RuntimeException("Tried to register Item outside of Pre-Init or Init");
		crate.setUnlocalizedName("for." + name);
		Proxies.common.registerItem(crate);
		PluginStorage.registerCrate(crate);
		return crate;
	}
}
