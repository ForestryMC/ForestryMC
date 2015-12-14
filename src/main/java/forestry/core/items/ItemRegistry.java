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
package forestry.core.items;

import net.minecraft.item.Item;

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.core.utils.StringUtil;
import forestry.plugins.PluginManager;

public abstract class ItemRegistry {
	protected static <T extends Item> T registerItem(T item, String name) {
		if (PluginManager.getStage() != PluginManager.Stage.SETUP) {
			throw new RuntimeException("Tried to register Item outside of Setup");
		}
		item.setUnlocalizedName("for." + name);
		GameRegistry.registerItem(item, StringUtil.cleanItemName(item));
		return item;
	}
}
