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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import forestry.api.core.ICamouflageAccess;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflageItemHandler;
import forestry.core.utils.Log;

public class CamouflageAccess implements ICamouflageAccess {

	private static final Map<String, List<ICamouflageItemHandler>> camouflageItemHandlers = new HashMap<>();
	private static final Map<String, List<ItemStack>> camouflageItemBlacklist = new HashMap<>();
	
	@Override
	public void registerCamouflageItemHandler(@Nonnull ICamouflageItemHandler itemHandler) {
		if(itemHandler == null){
			Log.error("Fail to register a camouflage item handler, because the handler is null. The handler is form the mod with the ID: " + Loader.instance().activeModContainer().getModId() + ".");
			return;
		}
		String type = itemHandler.getType();
		if(!camouflageItemHandlers.containsKey(type)){
			camouflageItemHandlers.put(type, new ArrayList<>());
		}
		if(!camouflageItemHandlers.containsValue(itemHandler)){
			camouflageItemHandlers.get(type).add(itemHandler);
		}else{
			Log.error("Fail to register a camouflage item handler, because the handler is already registered. The handler is form the mod with the ID: " + Loader.instance().activeModContainer().getModId() + ".");
		}
	}
	
	@Override
	public List<ICamouflageItemHandler> getCamouflageItemHandler(String type) {
		if(!camouflageItemHandlers.containsKey(type)){
			return null;
		}
		return camouflageItemHandlers.get(type);
	}
	
	@Override
	public void addItemToBlackList(String type, ItemStack camouflageBlock) {
		if (camouflageBlock == null || camouflageBlock.getItem() == null) {
			Log.error("Fail to add camouflage block item to the black list, because it is null");
			return;
		}
		Block block = Block.getBlockFromItem(camouflageBlock.getItem());
		if (block == null) {
			Log.error("Fail to add camouflage block item to the black list: because it has no block.");
			return;
		}
		for (ItemStack camouflageBlacklisted : camouflageItemBlacklist.get(type)) {
			if (camouflageBlacklisted.getItem() == camouflageBlock.getItem() && camouflageBlacklisted.getItemDamage() == camouflageBlock.getItemDamage() && ItemStack.areItemStackTagsEqual(camouflageBlock, camouflageBlacklisted)) {
				Log.error("Fail to add camouflage block item to the black list, because it is already registered: " + camouflageBlock + ".");
				return;
			}
		}
		camouflageItemBlacklist.get(type).add(camouflageBlock);
	}
	
	@Override
	public boolean isItemBlackListed(String type, ItemStack camouflageBlock) {
		if (camouflageBlock == null || camouflageBlock.getItem() == null || Block.getBlockFromItem(camouflageBlock.getItem()) == null || !camouflageItemBlacklist.containsKey(type)) {
			return false;
		}
		for (ItemStack camouflageBlacklisted : camouflageItemBlacklist.get(type)) {
			if (camouflageBlacklisted.getItem() == camouflageBlock.getItem() && camouflageBlacklisted.getItemDamage() == camouflageBlock.getItemDamage() && ItemStack.areItemStackTagsEqual(camouflageBlock, camouflageBlacklisted)) {
				return true;
			}
		}
		return false;
	}
	
	public static ICamouflageItemHandler getHandlerFromItem(@Nonnull ItemStack camouflageItem, ICamouflageHandler camouflageHandler){
		for(List<ICamouflageItemHandler> handlers : camouflageItemHandlers.values()){
			for(ICamouflageItemHandler handler : handlers){
				if(handler.canHandle(camouflageItem, camouflageHandler)){
					return handler;
				}
			}
		}
		return null;
	}

}
