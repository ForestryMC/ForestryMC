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
package forestry.greenhouse.camouflage;

import com.google.common.collect.Lists;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.Loader;

import forestry.api.core.ICamouflageAccess;
import forestry.api.core.ICamouflageItemHandler;
import forestry.core.utils.Log;

public class CamouflageAccess implements ICamouflageAccess {

	private static final List<ICamouflageItemHandler> camouflageItemHandlers = Lists.newArrayList();
	private static final List<ItemStack> camouflageItemBlacklist = Lists.newArrayList();
	private static final List<String> blacklistedMods = Lists.newArrayList();

	@Override
	public void registerItemHandler(ICamouflageItemHandler itemHandler) {
		if (!camouflageItemHandlers.contains(itemHandler)) {
			camouflageItemHandlers.add(itemHandler);
		} else {
			Log.error("Fail to register a camouflage item handler, because the handler is already registered. The handler is form the mod with the ID: " + Loader.instance().activeModContainer().getModId() + ".");
		}
	}

	@Override
	public List<ICamouflageItemHandler> getItemHandlers() {
		return camouflageItemHandlers;
	}

	@Override
	public void addItemToBlackList(ItemStack camouflageBlock) {
		Block block = Block.getBlockFromItem(camouflageBlock.getItem());
		if (block == Blocks.AIR) {
			Log.error("Fail to add camouflage block item to the black list: because it has no block.");
			return;
		}
		for (ItemStack camouflageBlacklisted : camouflageItemBlacklist) {
			if (camouflageBlacklisted.getItem() == camouflageBlock.getItem() && camouflageBlacklisted.getItemDamage() == camouflageBlock.getItemDamage() && ItemStack.areItemStackTagsEqual(camouflageBlock, camouflageBlacklisted)) {
				Log.error("Fail to add camouflage block item to the black list, because it is already registered: " + camouflageBlock + ".");
				return;
			}
		}

		camouflageItemBlacklist.add(camouflageBlock);
	}

	@Override
	public void addModIdToBlackList(String modID) {
		if (!blacklistedMods.contains(modID)) {
			blacklistedMods.add(modID);
		}
	}

	@Override
	public boolean isItemBlackListed(ItemStack camouflageBlock) {
		if (camouflageBlock.isEmpty() || Block.getBlockFromItem(camouflageBlock.getItem()) == Blocks.AIR) {
			return false;
		}
		String modId = camouflageBlock.getItem().getRegistryName().getNamespace();
		if (blacklistedMods.contains(modId)) {
			return true;
		}
		for (ItemStack camouflageBlacklisted : camouflageItemBlacklist) {
			if (camouflageBlacklisted.getItem() == camouflageBlock.getItem() && camouflageBlacklisted.getItemDamage() == camouflageBlock.getItemDamage() && ItemStack.areItemStackTagsEqual(camouflageBlock, camouflageBlacklisted)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public ICamouflageItemHandler getHandler(ItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}
		for (ICamouflageItemHandler handler : camouflageItemHandlers) {
			if (handler.canHandle(stack)) {
				return handler;
			}
		}
		return null;
	}

}
