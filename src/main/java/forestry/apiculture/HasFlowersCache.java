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
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import forestry.api.core.IBlockPosPredicate;
import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.api.genetics.IFlowerProvider;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.utils.TickHelper;
import forestry.core.utils.VectUtil;

import jdk.nashorn.internal.ir.Block;

public class HasFlowersCache implements INbtWritable, INbtReadable, IStreamable {
	private static final String NBT_KEY = "hasFlowerCache";
	private static final String NBT_KEY_FLOWERS = "flowers";
	private static final int FLOWER_CHECK_INTERVAL = 200;

	private final TickHelper tickHelper = new TickHelper();

	@Nullable
	private FlowerData flowerData;
	private final List<BlockPos> flowerCoords = new ArrayList<>();

	private boolean needsSync = false;

	private static class FlowerData {
		public String flowerType;
		public final IBlockPosPredicate flowerPredicate;
		public Iterator<BlockPos.MutableBlockPos> areaIterator;

		public FlowerData(IBee queen, IBeeHousing beeHousing) {
			IFlowerProvider flowerProvider = queen.getGenome().getFlowerProvider();
			this.flowerType = flowerProvider.getFlowerType();
			this.flowerPredicate = FlowerManager.flowerRegistry.createAcceptedFlowerPredicate(flowerType);
			this.areaIterator = FlowerManager.flowerRegistry.getAreaIterator(beeHousing, queen);
		}

		public void resetIterator(IBee queen, IBeeHousing beeHousing) {
			this.areaIterator = FlowerManager.flowerRegistry.getAreaIterator(beeHousing, queen);
		}
	}

	public void update(IBee queen, IBeeHousing beeHousing) {
		if (flowerData == null) {
			this.flowerData = new FlowerData(queen, beeHousing);
			this.flowerCoords.clear();
		}
		World world = beeHousing.getWorldObj();
		tickHelper.onTick();

		if (!flowerCoords.isEmpty() && tickHelper.updateOnInterval(FLOWER_CHECK_INTERVAL)) {
			Iterator<BlockPos> iterator = flowerCoords.iterator();
			while (iterator.hasNext()) {
				BlockPos flowerPos = iterator.next();
				if (!flowerData.flowerPredicate.test(world, flowerPos) && world.isBlockLoaded(flowerPos)) {
					iterator.remove();
					needsSync = true;
				}
			}
		}

		final int flowerCount = flowerCoords.size();
		final int ticksPerCheck = 1 + (flowerCount * flowerCount);

		if (tickHelper.updateOnInterval(ticksPerCheck)) {
			if (flowerData.areaIterator.hasNext()) {
				BlockPos.MutableBlockPos blockPos = flowerData.areaIterator.next();
				if (flowerData.flowerPredicate.test(world, blockPos)) {
					flowerCoords.add(blockPos.toImmutable());
					needsSync = true;
				}
			} else {
				flowerData.resetIterator(queen, beeHousing);
			}
		}
	}

	public boolean hasFlowers() {
		return !flowerCoords.isEmpty();
	}

	public boolean needsSync() {
		boolean returnVal = needsSync;
		needsSync = false;
		return returnVal;
	}

	public void clear() {
		flowerCoords.clear();
		flowerData = null;
	}

	@Nonnull
	public List<BlockPos> getFlowerCoords() {
		return Collections.unmodifiableList(flowerCoords);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (!nbttagcompound.hasKey(NBT_KEY)) {
			return;
		}

		NBTTagCompound hasFlowerCacheNBT = nbttagcompound.getCompoundTag(NBT_KEY);

		if (hasFlowerCacheNBT.hasKey(NBT_KEY_FLOWERS)) {
			int[] flowersList = hasFlowerCacheNBT.getIntArray(NBT_KEY_FLOWERS);
			if (flowersList != null && flowersList.length % 3 == 0) {
				int flowerCount = flowersList.length / 3;
				for (int i = 0; i < flowerCount; i++) {
					BlockPos flowerPos = new BlockPos(flowersList[i], flowersList[i + 1], flowersList[i + 2]);
					flowerCoords.add(flowerPos);
				}
				needsSync = true;
			}
		}
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

			hasFlowerCacheNBT.setIntArray(NBT_KEY_FLOWERS, flowersList);
		}

		nbttagcompound.setTag(NBT_KEY, hasFlowerCacheNBT);
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
