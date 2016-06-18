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

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.api.genetics.IFlowerProvider;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.utils.VectUtil;

public class HasFlowersCache implements INbtWritable, INbtReadable, IStreamable {
	private static final String nbtKey = "hasFlowerCache";
	private static final String nbtKeyFlowers = "flowers";
	private static final String nbtKeyCooldown = "cooldown";

	private static final Random random = new Random();
	private static final int flowerCheckInterval = 128;

	private final int flowerCheckTime = random.nextInt(flowerCheckInterval);
	@Nonnull
	private List<BlockPos> flowerCoords = new ArrayList<>();
	private int cooldown = 0;

	private boolean needsSync = false;

	public boolean hasFlowers(IBee queen, IBeeHousing beeHousing) {
		IFlowerProvider flowerProvider = queen.getGenome().getFlowerProvider();
		String flowerType = flowerProvider.getFlowerType();
		World world = beeHousing.getWorldObj();

		if (!flowerCoords.isEmpty()) {
			if (world.getTotalWorldTime() % flowerCheckInterval != flowerCheckTime) {
				return true;
			}

			IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(beeHousing);
			BlockPos housingCoords = beeHousing.getCoordinates();

			Vec3i genomeTerritory = queen.getGenome().getTerritory();
			float housingModifier = beeModifier.getTerritoryModifier(queen.getGenome(), 1f);
			Vec3i area = VectUtil.scale(genomeTerritory, housingModifier * 3.0f);
			BlockPos min = VectUtil.scale(area, -0.5f).add(housingCoords);
			BlockPos max = VectUtil.scale(area, 0.5f).add(housingCoords);

			for (BlockPos flowerPos : flowerCoords) {
				if (!isFlowerValid(world, flowerPos, flowerType, min, max)) {
					cooldown = 0;
					break;
				}
			}
		}

		if (cooldown <= 0) {
			List<BlockPos> newFlowerCoords = FlowerManager.flowerRegistry.getAcceptedFlowerCoordinates(beeHousing, queen, flowerType, 5);
			cooldown = PluginApiculture.ticksPerBeeWorkCycle;
			if (!flowerCoords.equals(newFlowerCoords)) {
				flowerCoords = newFlowerCoords;
				needsSync = true;
			}
		} else {
			cooldown--;
		}

		return !flowerCoords.isEmpty();
	}

	public boolean needsSync() {
		boolean returnVal = needsSync;
		needsSync = false;
		return returnVal;
	}

	public void clear() {
		flowerCoords.clear();
		cooldown = 0;
	}

	@Nonnull
	public List<BlockPos> getFlowerCoords() {
		return Collections.unmodifiableList(flowerCoords);
	}

	private static boolean isFlowerValid(World world, BlockPos flowerCoords, String flowerType, BlockPos min, BlockPos max) {
		if (!isFlowerCoordInRange(flowerCoords, min, max)) {
			return false;
		}
		return FlowerManager.flowerRegistry.isAcceptedFlower(flowerType, world, flowerCoords);
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

		if (hasFlowerCacheNBT.hasKey(nbtKeyFlowers)) {
			int[] flowersList = hasFlowerCacheNBT.getIntArray(nbtKeyFlowers);
			if (flowersList != null && flowersList.length % 3 == 0) {
				int flowerCount = flowersList.length / 3;
				for (int i = 0; i < flowerCount; i++) {
					BlockPos flowerPos = new BlockPos(flowersList[i], flowersList[i + 1], flowersList[i + 2]);
					flowerCoords.add(flowerPos);
				}
				needsSync = true;
			}
		}

		cooldown = hasFlowerCacheNBT.getInteger(nbtKeyCooldown);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		NBTTagCompound hasFlowerCacheNBT = new NBTTagCompound();

		if (!flowerCoords.isEmpty()) {
			int[] flowersList = new int[flowerCoords.size() * 3];
			int i = 0;
			for (BlockPos flowerPos : flowerCoords) {
				flowersList[i] = flowerPos.getX();
				flowersList[i + 1] = flowerPos.getY();
				flowersList[i + 2] = flowerPos.getZ();
				i++;
			}

			hasFlowerCacheNBT.setIntArray(nbtKeyFlowers, flowersList);
		}

		hasFlowerCacheNBT.setInteger(nbtKeyCooldown, cooldown);

		nbttagcompound.setTag(nbtKey, hasFlowerCacheNBT);
		return nbttagcompound;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		int size = flowerCoords.size();
		data.writeVarInt(size);
		if (size > 0) {
			for (BlockPos pos : flowerCoords) {
				data.writeVarInt(pos.getX());
				data.writeVarInt(pos.getY());
				data.writeVarInt(pos.getZ());
			}
		}
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		flowerCoords.clear();

		int size = data.readVarInt();
		while (size > 0) {
			BlockPos pos = new BlockPos(data.readVarInt(), data.readVarInt(), data.readVarInt());
			flowerCoords.add(pos);
			size--;
		}
	}
}
