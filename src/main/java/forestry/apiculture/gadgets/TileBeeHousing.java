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
package forestry.apiculture.gadgets;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.apiculture.BeeHousingInventory;
import forestry.core.config.Config;
import forestry.core.gadgets.TileBase;
import forestry.core.interfaces.IClimatised;
import forestry.core.proxy.Proxies;

public abstract class TileBeeHousing extends TileBase implements IBeeHousing, IClimatised {
	private final IBeekeepingLogic beeLogic;
	private BiomeGenBase cachedBiome;

	// CLIENT
	private int breedingProgressPercent = 0;

	protected TileBeeHousing() {
		setHints(Config.hints.get("apiary"));
		this.beeLogic = BeeManager.beeRoot.createBeekeepingLogic(this);
	}

	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return beeLogic;
	}

	/* LOADING & SAVING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		beeLogic.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		beeLogic.readFromNBT(nbttagcompound);
	}

	@Override
	public Packet getDescriptionPacket() {
		beeLogic.syncToClient();
		return super.getDescriptionPacket();
	}

	/* ICLIMATISED */
	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromBiome(getBiome(), pos);
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Override
	public float getExactTemperature() {
		return getBiome().getFloatTemperature(pos);
	}

	@Override
	public float getExactHumidity() {
		return getBiome().rainfall;
	}

	/* UPDATING */
	@Override
	public void updateClientSide() {
		if (beeLogic.canDoBeeFX() && updateOnInterval(4)) {
			beeLogic.doBeeFX();

			if (updateOnInterval(50)) {
				float fxX = pos.getX() + 0.5F;
				float fxY = pos.getY() + 0.25F;
				float fxZ = pos.getZ() + 0.5F;
				float distanceFromCenter = 0.6F;
				float leftRightSpreadFromCenter = distanceFromCenter * (worldObj.rand.nextFloat() - 0.5F);
				float upSpread = (worldObj.rand.nextFloat() * 6F) / 16F;
				fxY += upSpread;

				Proxies.common.addEntitySwarmFX(worldObj, (fxX - distanceFromCenter), fxY, (fxZ + leftRightSpreadFromCenter));
				Proxies.common.addEntitySwarmFX(worldObj, (fxX + distanceFromCenter), fxY, (fxZ + leftRightSpreadFromCenter));
				Proxies.common.addEntitySwarmFX(worldObj, (fxX + leftRightSpreadFromCenter), fxY, (fxZ - distanceFromCenter));
				Proxies.common.addEntitySwarmFX(worldObj, (fxX + leftRightSpreadFromCenter), fxY, (fxZ + distanceFromCenter));
			}
		}
	}

	@Override
	public void updateServerSide() {
		if (beeLogic.canWork()) {
			beeLogic.doWork();
		}
	}

	/**
	 * Returns scaled queen health or breeding progress
	 */
	public int getHealthScaled(int i) {
		return (breedingProgressPercent * i) / 100;
	}

	/* SMP */
	public void getGUINetworkData(int i, int j) {
		switch (i) {
			case 0:
				breedingProgressPercent = j;
				break;
		}
	}

	public void sendGUINetworkData(Container container, ICrafting iCrafting) {
		iCrafting.sendProgressBarUpdate(container, 0, beeLogic.getBeeProgressPercent());
	}

	// / IBEEHOUSING
	@Override
	public BiomeGenBase getBiome() {
		if (cachedBiome == null) {
			cachedBiome = worldObj.getBiomeGenForCoordsBody(pos);
		}
		return cachedBiome;
	}

	@Override
	public int getBlockLightValue() {
		return worldObj.getLightFromNeighbors(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()));
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return worldObj.canBlockSeeSky(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()));
	}

	@Override
	public GameProfile getOwner() {
		return getAccessHandler().getOwner();
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

	public static class TileBeeHousingInventory extends BeeHousingInventory {
		private final TileBeeHousing tile;

		public TileBeeHousingInventory(TileBeeHousing tile, int size, String name) {
			super(size, name, tile.getAccessHandler());
			this.tile = tile;
		}

		@Override
		public void markDirty() {
			super.markDirty();
			tile.markDirty();
		}
	}

}
