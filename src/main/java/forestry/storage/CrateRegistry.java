/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v3 which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to: SirSengir (original work), CovertJaguar, Player, Binnie,
 * MysteriousAges
 ******************************************************************************/
package forestry.storage;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.storage.ICrateRegistry;
import forestry.core.items.ItemCrated;
import forestry.core.utils.Log;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginStorage;

public class CrateRegistry implements ICrateRegistry {

    private static void registerCrate(ItemStack stack, String uid, boolean useOreDict) {
        if (stack == null || stack.getItem() == null) {
            Log.severe("Tried to make a crate without an item");
            return;
        }

        if (uid == null) {
            Log.severe("Tried to make a crate without a uid");
            return;
        }

        ItemCrated crate = new ItemCrated(stack, useOreDict);
        crate.setUnlocalizedName(uid);
        GameRegistry.registerItem(crate, StringUtil.cleanItemName(crate));
        PluginStorage.registerCrate(crate);
    }

    @Override
    public void registerCrate(Item item, String uid) {
        registerCrate(new ItemStack(item), uid, false);
    }

    @Override
    public void registerCrateUsingOreDict(Item item, String uid) {
        registerCrate(new ItemStack(item), uid, true);
    }

    @Override
    public void registerCrate(Block block, String uid) {
        registerCrate(new ItemStack(block), uid, false);
    }

    @Override
    public void registerCrateUsingOreDict(Block block, String uid) {
        registerCrate(new ItemStack(block), uid, true);
    }

    @Override
    public void registerCrate(ItemStack stack, String uid) {
        registerCrate(stack, uid, false);
    }

    @Override
    public void registerCrateUsingOreDict(ItemStack stack, String uid) {
        registerCrate(stack, uid, true);
    }
}
