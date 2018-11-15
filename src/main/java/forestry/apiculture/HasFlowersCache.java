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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.IBlockPosPredicate;
import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.api.genetics.IFlowerProvider;
import forestry.core.utils.TickHelper;

public class HasFlowersCache implements INbtWritable, INbtReadable {
	private static final String NBT_KEY = "hasFlowerCache";
	private static final String NBT_KEY_FLOWERS = "flowers";
	private int flowerCheckInterval;

	private final TickHelper tickHelper = new TickHelper();

	public HasFlowersCache() {
		this.flowerCheckInterval = 200;
	}

	public HasFlowersCache(int checkInterval) {
		flowerCheckInterval = checkInterval;
	}

	@Nullable
	private FlowerData flowerData;
	private final ArrayList<BlockPos> flowerCoords = new ArrayList<>();
	private final List<IBlockState> flowers = new ArrayList<>();

	private boolean needsSync = false;

	private static class FlowerData {
		public final String flowerType;
		public final Vec3i territory;
		public final IBlockPosPredicate flowerPredicate;
		public Iterator<BlockPos.MutableBlockPos> areaIterator;

		public FlowerData(IBee queen, IBeeHousing beeHousing) {
			IFlowerProvider flowerProvider = queen.getGenome().getFlowerProvider();
			this.flowerType = flowerProvider.getFlowerType();
			this.territory = queen.getGenome().getTerritory();
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
			this.flowers.clear();
		}
		World world = beeHousing.getWorldObj();
		tickHelper.onTick();

		if (!flowerCoords.isEmpty() && tickHelper.updateOnInterval(flowerCheckInterval)) {
			Iterator<BlockPos> iterator = flowerCoords.iterator();
			while (iterator.hasNext()) {
				BlockPos flowerPos = iterator.next();
				if (!flowerData.flowerPredicate.test(world, flowerPos) && world.isBlockLoaded(flowerPos)) {
					iterator.remove();
					flowers.clear();
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
					addFlowerPos(blockPos.toImmutable());
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

	public void onNewQueen(IBee queen, IBeeHousing housing) {
		if (this.flowerData != null) {
			IBeeGenome genome = queen.getGenome();
			String flowerType = genome.getFlowerProvider().getFlowerType();
			if (!this.flowerData.flowerType.equals(flowerType)
				|| !this.flowerData.territory.equals(genome.getTerritory())) {
				flowerData = new FlowerData(queen, housing);
				flowerCoords.clear();
				flowers.clear();
			}
		}
	}

	public List<BlockPos> getFlowerCoords() {
		return Collections.unmodifiableList(flowerCoords);
	}

	public List<IBlockState> getFlowers(World world) {
		if (flowers.isEmpty() && !flowerCoords.isEmpty()) {
			flowers.clear();
			for (BlockPos flowerCoord : flowerCoords) {
				IBlockState blockState = world.getBlockState(flowerCoord);
				flowers.add(blockState);
			}
		}
		return Collections.unmodifiableList(flowers);
	}

	public void addFlowerPos(BlockPos blockPos) {
		flowerCoords.add(blockPos);
		flowers.clear();
		needsSync = true;
	}

	public void forceLookForFlowers(IBee queen, IBeeHousing housing) {
		if (flowerData != null) {
			flowerCoords.clear();
			flowers.clear();
			flowerData.resetIterator(queen, housing);
			World world = housing.getWorldObj();
			while (flowerData.areaIterator.hasNext()) {
				BlockPos.MutableBlockPos blockPos = flowerData.areaIterator.next();
				if (flowerData.flowerPredicate.test(world, blockPos)) {
					addFlowerPos(blockPos.toImmutable());
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (!nbttagcompound.hasKey(NBT_KEY)) {
			return;
		}

		NBTTagCompound hasFlowerCacheNBT = nbttagcompound.getCompoundTag(NBT_KEY);
		flowerCoords.clear();
		if (hasFlowerCacheNBT.hasKey(NBT_KEY_FLOWERS)) {
			int[] flowersList = hasFlowerCacheNBT.getIntArray(NBT_KEY_FLOWERS);
			if (flowersList.length % 3 == 0) {
				int flowerCount = flowersList.length / 3;

				flowerCoords.ensureCapacity(flowerCount);

				for (int i = 0; i < flowerCount; i++) {
					int index = i * 3;
					BlockPos flowerPos = new BlockPos(flowersList[index], flowersList[index + 1], flowersList[index + 2]);
					flowerCoords.add(flowerPos);
				}
				needsSync = true;
			}
		}
		flowers.clear();
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
				i += 3;
			}

			hasFlowerCacheNBT.setIntArray(NBT_KEY_FLOWERS, flowersList);
		}

		nbttagcompound.setTag(NBT_KEY, hasFlowerCacheNBT);
		return nbttagcompound;
	}

	public void writeData(PacketBuffer data) {
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

	public void readData(PacketBuffer data) {
		flowerCoords.clear();
		flowers.clear();

		int size = data.readVarInt();
		while (size > 0) {
			BlockPos pos = new BlockPos(data.readVarInt(), data.readVarInt(), data.readVarInt());
			flowerCoords.add(pos);
			size--;
		}
	}
}
