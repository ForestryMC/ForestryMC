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
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.core.IItemModelRegister;
import forestry.core.models.ModelManager;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginManager;

public abstract class ItemRegistry {
	protected static <T extends Item> T registerItem(T item, String name) {
		if (PluginManager.getStage() != PluginManager.Stage.REGISTER) {
			throw new RuntimeException("Tried to register Item outside of REGISTER");
		}
		item.setUnlocalizedName("for." + name);
		GameRegistry.registerItem(item, StringUtil.cleanItemName(item));

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			if (item instanceof IItemModelRegister) {
				((IItemModelRegister) item).registerModel(item, ModelManager.getInstance());
			}
		}

		return item;
	}

	protected static void registerOreDict(String oreDictName, ItemStack itemStack) {
		OreDictionary.registerOre(oreDictName, itemStack);
	}
}
