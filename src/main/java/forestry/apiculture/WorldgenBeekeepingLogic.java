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

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.network.packets.PacketBeeLogicActive;
import forestry.apiculture.tiles.TileHive;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.TickHelper;

public class WorldgenBeekeepingLogic implements IBeekeepingLogic {
	private final TileHive housing;
	private IEffectData[] effectData = new IEffectData[2];
	private final HasFlowersCache hasFlowersCache = new HasFlowersCache(2);
	private final TickHelper tickHelper = new TickHelper();

	// Client
	private boolean active;

	public WorldgenBeekeepingLogic(TileHive housing) {
		this.housing = housing;
	}

	// / SAVING & LOADING
	@Override
	public void read(CompoundNBT CompoundNBT) {
		setActive(CompoundNBT.getBoolean("Active"));
		hasFlowersCache.read(CompoundNBT);
	}

	@Override
	public CompoundNBT write(CompoundNBT CompoundNBT) {
		CompoundNBT.putBoolean("Active", active);
		hasFlowersCache.write(CompoundNBT);

		return CompoundNBT;
	}

	@Override
	public void writeData(PacketBuffer data) {
		data.writeBoolean(active);
		if (active) {
			hasFlowersCache.writeData(data);
		}
	}

	@Override
	public void readData(PacketBuffer data) {
		boolean active = data.readBoolean();
		setActive(active);
		if (active) {
			hasFlowersCache.readData(data);
		}
	}

	/* Activatable */
	private void setActive(boolean active) {
		if (this.active == active) {
			return;
		}
		this.active = active;

		syncToClient();
	}

	/* UPDATING */

	@Override
	public boolean canWork() {
		tickHelper.onTick();

		if (tickHelper.updateOnInterval(200)) {
			IBee queen = housing.getContainedBee();
			hasFlowersCache.update(queen, housing);
			World world = housing.getWorldObj();
			boolean canWork = (world.isDaytime() || queen.getGenome().getActiveValue(BeeChromosomes.NEVER_SLEEPS)) &&
				(!housing.isRaining() || queen.getGenome().getActiveValue(BeeChromosomes.TOLERATES_RAIN));
			boolean flowerCacheNeedsSync = hasFlowersCache.needsSync();

			if (active != canWork) {
				setActive(canWork);
			} else if (flowerCacheNeedsSync) {
				syncToClient();
			}
		}

		return active;
	}

	@Override
	public void doWork() {

	}

	@Override
	public void clearCachedValues() {

	}

	/* CLIENT */

	@Override
	public void syncToClient() {
		World world = housing.getWorldObj();
		if (world != null && !world.isRemote) {
			NetworkUtil.sendNetworkPacket(new PacketBeeLogicActive(housing), housing.getCoordinates(), world);
		}
	}

	@Override
	public void syncToClient(ServerPlayerEntity player) {
		World world = housing.getWorldObj();
		if (world != null && !world.isRemote) {
			NetworkUtil.sendToPlayer(new PacketBeeLogicActive(housing), player);
		}
	}

	@Override
	public int getBeeProgressPercent() {
		return 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean canDoBeeFX() {
		return !Minecraft.getInstance().isGamePaused() && active;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void doBeeFX() {
		IBee queen = housing.getContainedBee();
		queen.doFX(effectData, housing);
	}

	@Override
	public List<BlockPos> getFlowerPositions() {
		return hasFlowersCache.getFlowerCoords();
	}

}
