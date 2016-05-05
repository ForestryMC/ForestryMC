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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import forestry.api.core.EnumCamouflageType;
import forestry.api.greenhouse.IGreenhouseAccess;
import forestry.core.utils.Log;

public class GreenhouseAccess implements IGreenhouseAccess {

	private final Map<ItemStack, Float> greenhouseGlasses = new HashMap<>();
	private final Map<EnumCamouflageType, List<ItemStack>> camouflageBlockBlacklist = new EnumMap<>(EnumCamouflageType.class);
	
	public GreenhouseAccess() {
		for (EnumCamouflageType type : EnumCamouflageType.VALUES) {
			camouflageBlockBlacklist.put(type, new ArrayList<>());
		}
	}
	
	@Override
	public void registerGreenhouseGlass(@Nonnull ItemStack glass, float lightTransmittance) {
		if (glass == null || glass.getItem() == null) {
			Log.error("Fail to register greenhouse glass, because it is null");
			return;
		}
		Block block = Block.getBlockFromItem(glass.getItem());
		if (block == null) {
			Log.error("Fail to register greenhouse glass, it has no matching block: " + glass + ".");
			return;
		}
		IBlockState defaultBlockState = block.getDefaultState();
		if (block.isOpaqueCube(defaultBlockState)) {
			Log.error("Fail to register greenhouse glass, it is opaque: " + block + ".");
			return;
		}
		for (ItemStack greenhouseGlass : greenhouseGlasses.keySet()) {
			if (greenhouseGlass.getItem() == glass.getItem() && greenhouseGlass.getItemDamage() == glass.getItemDamage() && ItemStack.areItemStackTagsEqual(glass, greenhouseGlass)) {
				Log.error("Fail to register greenhouse glass, because it is already registered: " + glass + ".");
				return;
			}
		}
		greenhouseGlasses.put(glass, lightTransmittance);
	}
	
	@Override
	public float getGreenhouseGlassLightTransmittance(@Nonnull ItemStack glass) {
		if (glass == null || glass.getItem() == null || Block.getBlockFromItem(glass.getItem()) == null) {
			return 0.5F;
		}
		for (Map.Entry<ItemStack, Float> greenhouseGlassEntry : greenhouseGlasses.entrySet()) {
			ItemStack greenhouseGlass = greenhouseGlassEntry.getKey();
			if (greenhouseGlass.getItem() == glass.getItem() && greenhouseGlass.getItemDamage() == glass.getItemDamage() && ItemStack.areItemStackTagsEqual(glass, greenhouseGlass)) {
				return greenhouseGlassEntry.getValue();
			}
		}
		return 0.5F;
	}

	@Override
	public boolean isGreenhouseGlass(@Nonnull ItemStack glass) {
		if (glass == null || glass.getItem() == null || Block.getBlockFromItem(glass.getItem()) == null) {
			return false;
		}
		for (ItemStack greenhouseGlass : greenhouseGlasses.keySet()) {
			if (greenhouseGlass.getItem() == glass.getItem() && greenhouseGlass.getItemDamage() == glass.getItemDamage() && ItemStack.areItemStackTagsEqual(glass, greenhouseGlass)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void addToCamouflageBlockBlackList(@Nonnull EnumCamouflageType type, @Nonnull ItemStack camouflageBlock) {
		if (camouflageBlock == null || camouflageBlock.getItem() == null) {
			Log.error("Fail to add camouflage block item to the black list, because it is null");
			return;
		}
		Block block = Block.getBlockFromItem(camouflageBlock.getItem());
		if (block == null) {
			Log.error("Fail to add camouflage block item to the black list: because it has no block.");
			return;
		}
		for (ItemStack camouflageBlacklisted : camouflageBlockBlacklist.get(type)) {
			if (camouflageBlacklisted.getItem() == camouflageBlock.getItem() && camouflageBlacklisted.getItemDamage() == camouflageBlock.getItemDamage() && ItemStack.areItemStackTagsEqual(camouflageBlock, camouflageBlacklisted)) {
				Log.error("Fail to add camouflage block item to the black list, because it is already registered: " + camouflageBlock + ".");
				return;
			}
		}
		camouflageBlockBlacklist.get(type).add(camouflageBlock);
	}

	@Override
	public boolean isOnCamouflageBlockBlackList(@Nonnull EnumCamouflageType type, @Nonnull ItemStack camouflageBlock) {
		if (camouflageBlock == null || camouflageBlock.getItem() == null || Block.getBlockFromItem(camouflageBlock.getItem()) == null) {
			return false;
		}
		for (ItemStack camouflageBlacklisted : camouflageBlockBlacklist.get(type)) {
			if (camouflageBlacklisted.getItem() == camouflageBlock.getItem() && camouflageBlacklisted.getItemDamage() == camouflageBlock.getItemDamage() && ItemStack.areItemStackTagsEqual(camouflageBlock, camouflageBlacklisted)) {
				return true;
			}
		}
		return false;
	}

}
