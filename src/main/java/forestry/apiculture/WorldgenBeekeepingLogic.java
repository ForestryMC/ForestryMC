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

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.network.packets.PacketBeeLogicActive;
import forestry.apiculture.tiles.TileHive;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.TickHelper;

public class WorldgenBeekeepingLogic implements IBeekeepingLogic {
	private final TileHive housing;
	private IBee queen;
	private IEffectData effectData[] = new IEffectData[2];
	private final HasFlowersCache hasFlowersCache = new HasFlowersCache(2);
	private final TickHelper tickHelper = new TickHelper();

	// Client
	private boolean active;

	public WorldgenBeekeepingLogic(TileHive housing) {
		this.housing = housing;
	}

	// / SAVING & LOADING
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		setActive(nbttagcompound.getBoolean("Active"));
		hasFlowersCache.readFromNBT(nbttagcompound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setBoolean("Active", active);
		hasFlowersCache.writeToNBT(nbttagcompound);

		return nbttagcompound;
	}

	@Override
	public void writeData(PacketBuffer data) {
		data.writeBoolean(active);
		if (active) {
			hasFlowersCache.writeData(data);
		}
	}

	@Override
	public void readData(PacketBuffer data) throws IOException {
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
			if (queen == null) {                //Trying to set this in constructor causes crash
				queen = housing.getContainedBee();
			}
			hasFlowersCache.update(queen, housing);
			World world = housing.getWorldObj();
			boolean canWork = (world.isDaytime() || queen.getGenome().getNeverSleeps()) &&
					(!housing.isRaining() || queen.getGenome().getToleratesRain());
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
	public void syncToClient(EntityPlayerMP player) {
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
	@SideOnly(Side.CLIENT)
	public boolean canDoBeeFX() {
		return !Minecraft.getMinecraft().isGamePaused() && active;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void doBeeFX() {
		IBee queen = housing.getContainedBee();
		queen.doFX(effectData, housing);
	}

	@Override
	public List<BlockPos> getFlowerPositions() {
		return hasFlowersCache.getFlowerCoords();
	}

}
