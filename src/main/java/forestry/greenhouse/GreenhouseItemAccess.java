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
package forestry.greenhouse;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import forestry.api.core.EnumCamouflageType;
import forestry.api.greenhouse.IGreenhouseItemAccess;
import forestry.core.utils.Log;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class GreenhouseItemAccess implements IGreenhouseItemAccess {

	private final List<ItemStack> greenhouseGlass = Lists.newArrayList();
	private final Map<EnumCamouflageType, List<ItemStack>> camouflageBlockBlacklist = new EnumMap(EnumCamouflageType.class);
	
	public GreenhouseItemAccess() {
		for(EnumCamouflageType type : EnumCamouflageType.VALUES){
			camouflageBlockBlacklist.put(type, new ArrayList());
		}
	}
	
	@Override
	public void registerGreenhouseGlass(@Nonnull ItemStack glass) {
		if(glass == null || glass.getItem() == null){
			Log.error("Fail to register greenhouse glass, because it is null");
			return;
		}
		Block block = Block.getBlockFromItem(glass.getItem());
		if(block == null || block.isOpaqueCube()){
			Log.error("Fail to register greenhouse glass: " + block + ".");
			return;
		}
		for(ItemStack greenhouseGlass : greenhouseGlass){
			if(greenhouseGlass.getItem() == glass.getItem() && greenhouseGlass.getItemDamage() == glass.getItemDamage() && ItemStack.areItemStackTagsEqual(glass, greenhouseGlass)){
				Log.error("Fail to register greenhouse glass, because it is already registered: " + glass + ".");
				return;
			}
		}
		greenhouseGlass.add(glass);
	}

	@Override
	public boolean isGreenhouseGlass(@Nonnull ItemStack glass) {
		if(glass == null || glass.getItem() == null || Block.getBlockFromItem(glass.getItem()) == null) {
			return false;
		}
		for(ItemStack greenhouseGlass : greenhouseGlass){
			if(greenhouseGlass.getItem() == glass.getItem() && greenhouseGlass.getItemDamage() == glass.getItemDamage() && ItemStack.areItemStackTagsEqual(glass, greenhouseGlass)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void addToCamouflageBlockBlackList(@Nonnull EnumCamouflageType type, @Nonnull ItemStack camouflageBlock) {
		if(camouflageBlock == null || camouflageBlock.getItem() == null){
			Log.error("Fail to add camouflage block item to the black list, because it is null");
			return;
		}
		Block block = Block.getBlockFromItem(camouflageBlock.getItem());
		if(block == null){
			Log.error("Fail to add camouflage block item to the black list: because it has no block.");
			return;
		}
		for(ItemStack camouflageBlacklisted : camouflageBlockBlacklist.get(type)){
			if(camouflageBlacklisted.getItem() == camouflageBlock.getItem() && camouflageBlacklisted.getItemDamage() == camouflageBlock.getItemDamage() && ItemStack.areItemStackTagsEqual(camouflageBlock, camouflageBlacklisted)){
				Log.error("Fail to add camouflage block item to the black list, because it is already registered: " + camouflageBlock + ".");
				return;
			}
		}
		camouflageBlockBlacklist.get(type).add(camouflageBlock);
	}

	@Override
	public boolean isOnCamouflageBlockBlackList(@Nonnull EnumCamouflageType type, @Nonnull ItemStack camouflageBlock) {
		if(camouflageBlock == null || camouflageBlock.getItem() == null || Block.getBlockFromItem(camouflageBlock.getItem()) == null) {
			return false;
		}
		for(ItemStack camouflageBlacklisted : camouflageBlockBlacklist.get(type)){
			if(camouflageBlacklisted.getItem() == camouflageBlock.getItem() && camouflageBlacklisted.getItemDamage() == camouflageBlock.getItemDamage() && ItemStack.areItemStackTagsEqual(camouflageBlock, camouflageBlacklisted)){
				return true;
			}
		}
		return false;
	}

}
