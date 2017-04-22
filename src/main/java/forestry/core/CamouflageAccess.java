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
package forestry.core;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageAccess;
import forestry.api.core.ICamouflageItemHandler;
import forestry.core.utils.Log;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class CamouflageAccess implements ICamouflageAccess {

	private static final ListMultimap<String, ICamouflageItemHandler> camouflageItemHandlers = ArrayListMultimap.create();
	private static final ListMultimap<String, ItemStack> camouflageItemBlacklist = ArrayListMultimap.create();
	private static final ListMultimap<String, String> blacklistedMods = ArrayListMultimap.create();
	public static final ICamouflageItemHandler NONE = new CamouflageHandlerNone();

	@Override
	public void registerCamouflageItemHandler(ICamouflageItemHandler itemHandler) {
		String type = itemHandler.getType();

		List<ICamouflageItemHandler> handlers = camouflageItemHandlers.get(type);
		if (!handlers.contains(itemHandler)) {
			handlers.add(itemHandler);
		} else {
			Log.error("Fail to register a camouflage item handler, because the handler is already registered. The handler is form the mod with the ID: " + Loader.instance().activeModContainer().getModId() + ".");
		}
	}

	@Override
	public List<ICamouflageItemHandler> getCamouflageItemHandler(String type) {
		if (type.equals(CamouflageManager.NONE)) {
			List<ICamouflageItemHandler> handlers = new ArrayList<>();
			handlers.addAll(camouflageItemHandlers.values());
			return handlers;
		}

		return camouflageItemHandlers.get(type);
	}

	@Override
	public void addModIdToBlackList(String type, String modID) {
		if (!blacklistedMods.get(type).contains(modID)) {
			blacklistedMods.put(type, modID);
		}
	}

	@Override
	public void addItemToBlackList(String type, ItemStack camouflageBlock) {
		Block block = Block.getBlockFromItem(camouflageBlock.getItem());
		if (block == Blocks.AIR) {
			Log.error("Fail to add camouflage block item to the black list: because it has no block.");
			return;
		}
		for (ItemStack camouflageBlacklisted : camouflageItemBlacklist.get(type)) {
			if (camouflageBlacklisted.getItem() == camouflageBlock.getItem() && camouflageBlacklisted.getItemDamage() == camouflageBlock.getItemDamage() && ItemStack.areItemStackTagsEqual(camouflageBlock, camouflageBlacklisted)) {
				Log.error("Fail to add camouflage block item to the black list, because it is already registered: " + camouflageBlock + ".");
				return;
			}
		}

		camouflageItemBlacklist.put(type, camouflageBlock);
	}

	@Override
	public boolean isItemBlackListed(String type, ItemStack camouflageBlock) {
		if (camouflageBlock.isEmpty() || Block.getBlockFromItem(camouflageBlock.getItem()) == Blocks.AIR || !type.equals(CamouflageManager.NONE) && !camouflageItemBlacklist.containsKey(type)) {
			return false;
		}
		String modId = camouflageBlock.getItem().getRegistryName().getResourceDomain();
		if (blacklistedMods.get(type) != null && blacklistedMods.get(type).contains(modId)) {
			return true;
		}
		List<ItemStack> camouflageItemBlacklisted;
		if (type.equals(CamouflageManager.NONE)) {
			camouflageItemBlacklisted = new ArrayList<>();
			camouflageItemBlacklisted.addAll(camouflageItemBlacklist.values());
		} else {
			camouflageItemBlacklisted = camouflageItemBlacklist.get(type);
		}
		for (ItemStack camouflageBlacklisted : camouflageItemBlacklisted) {
			if (camouflageBlacklisted.getItem() == camouflageBlock.getItem() && camouflageBlacklisted.getItemDamage() == camouflageBlock.getItemDamage() && ItemStack.areItemStackTagsEqual(camouflageBlock, camouflageBlacklisted)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public ICamouflageItemHandler getNoneItemHandler() {
		return NONE;
	}

	@Override
	public ICamouflageItemHandler getHandlerFromItem(ItemStack stack) {
		if (stack.isEmpty()) {
			return NONE;
		}
		for (ICamouflageItemHandler handler : camouflageItemHandlers.values()) {
			if (handler.canHandle(stack)) {
				return handler;
			}
		}
		return NONE;
	}

}
