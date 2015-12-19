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
package forestry.apiculture;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.core.INBTTagable;
import forestry.api.genetics.IFlowerProvider;
import forestry.core.utils.BlockUtil;
import forestry.plugins.PluginApiculture;

public class HasFlowersCache implements INBTTagable {
	private static final String nbtKey = "hasFlowerCache";
	private static final Random random = new Random();
	private static final int flowerCheckInterval = 128;

	private final int flowerCheckTime = random.nextInt(flowerCheckInterval);
	private BlockPos flowerCoords = null;
	private int cooldown = 0;

	public boolean hasFlowers(IBee queen, IBeeHousing beeHousing) {
		IFlowerProvider flowerProvider = queen.getGenome().getFlowerProvider();
		String flowerType = flowerProvider.getFlowerType();
		World world = beeHousing.getWorld();

		if (flowerCoords != null) {
			if (world.getTotalWorldTime() % flowerCheckInterval != flowerCheckTime) {
				return true;
			}

			IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(beeHousing);
			BlockPos housingCoords = beeHousing.getCoordinates();

			int[] genomeTerritory = queen.getGenome().getTerritory();
			float housingModifier = beeModifier.getTerritoryModifier(queen.getGenome(), 1f);
			BlockPos area = BlockUtil.multiply(new BlockPos(genomeTerritory[0], genomeTerritory[1], genomeTerritory[2]), housingModifier * 3.0f);
			BlockPos min = BlockUtil.multiply(area, -0.5f).add(housingCoords);
			BlockPos max = BlockUtil.multiply(area, 0.5f).add(housingCoords);

			if (isFlowerValid(world, flowerType, min, max)) {
				return true;
			} else {
				flowerCoords = null;
				cooldown = 0;
			}
		}

		if (cooldown <= 0) {
			flowerCoords = FlowerManager.flowerRegistry.getAcceptedFlowerCoordinates(beeHousing, queen, flowerType);
			cooldown = PluginApiculture.ticksPerBeeWorkCycle;
		} else {
			cooldown--;
		}

		return flowerCoords != null;
	}

	private boolean isFlowerValid(World world, String flowerType, BlockPos min, BlockPos max) {
		if (!isFlowerCoordInRange(flowerCoords, min, max)) {
			return false;
		}
		return FlowerManager.flowerRegistry.isAcceptedFlower(flowerType, world, flowerCoords.getX(), flowerCoords.getY(), flowerCoords.getZ());
	}

	private static boolean isFlowerCoordInRange(BlockPos flowerCoords, BlockPos min, BlockPos max) {
		return flowerCoords.getX() >= min.getX() && flowerCoords.getX() <= max.getX() && flowerCoords.getY() >= min.getY() && flowerCoords.getY() <= max.getY() && flowerCoords.getZ() >= min.getZ() && flowerCoords.getZ() <= max.getZ();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (!nbttagcompound.hasKey(nbtKey)) {
			return;
		}

		NBTTagCompound hasFlowerCacheNBT = nbttagcompound.getCompoundTag(nbtKey);
		if (hasFlowerCacheNBT.hasKey("flowerX")) {
			int x = hasFlowerCacheNBT.getInteger("flowerX");
			int y = hasFlowerCacheNBT.getInteger("flowerY");
			int z = hasFlowerCacheNBT.getInteger("flowerZ");
			flowerCoords = new BlockPos(x, y, z);
		}

		cooldown = hasFlowerCacheNBT.getInteger("cooldown");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		NBTTagCompound hasFlowerCacheNBT = new NBTTagCompound();
		if (flowerCoords != null) {
			hasFlowerCacheNBT.setInteger("flowerX", flowerCoords.getX());
			hasFlowerCacheNBT.setInteger("flowerY", flowerCoords.getY());
			hasFlowerCacheNBT.setInteger("flowerZ", flowerCoords.getZ());
		}
		hasFlowerCacheNBT.setInteger("cooldown", cooldown);

		nbttagcompound.setTag(nbtKey, hasFlowerCacheNBT);
	}
}
