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
package forestry.apiculture.entities;

import java.io.IOException;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.apiculture.gui.IGuiBeeHousingInventory;
import forestry.apiculture.network.packets.PacketBeeLogicEntityRequest;
import forestry.apiculture.tiles.TileBeeHousingBase;
import forestry.core.entities.EntityMinecartContainerForestry;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.IStreamableGui;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.IClimatised;

public abstract class EntityMinecartBeeHousingBase extends EntityMinecartContainerForestry implements IBeeHousing, IGuiBeeHousingInventory, IClimatised, IStreamableGui {
	private static final Random random = new Random();
	private static final int beeFXInterval = 4;
	private static final int pollenFXInterval = 50;

	private final int beeFXTime = random.nextInt(beeFXInterval);
	private final int pollenFXTime = random.nextInt(pollenFXInterval);

	private final IBeekeepingLogic beeLogic = BeeManager.beeRoot.createBeekeepingLogic(this);
	private final IErrorLogic errorLogic = ForestryAPI.errorStateRegistry.createErrorLogic();

	// CLIENT
	private int breedingProgressPercent = 0;
	private boolean needsActiveUpdate = true;

	@SuppressWarnings("unused")
	public EntityMinecartBeeHousingBase(World world) {
		super(world);
	}

	public EntityMinecartBeeHousingBase(World world, double posX, double posY, double posZ) {
		super(world, posX, posY, posZ);
	}

	/* IBeeHousing */
	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return beeLogic;
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromBiome(getBiome(), (int) posX, (int) posY, (int) posZ);
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getBiome().rainfall);
	}

	@Override
	public float getExactTemperature() {
		return getBiome().temperature;
	}

	@Override
	public float getExactHumidity() {
		return getBiome().rainfall;
	}

	@Override
	public int getBlockLightValue() {
		return worldObj.getBlockLightValue((int) posX, (int) posY + 1, (int) posZ);
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return worldObj.canBlockSeeTheSky((int) posX, (int) posY + 1, (int) posZ);
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public BiomeGenBase getBiome() {
		return worldObj.getBiomeGenForCoords((int) posX, (int) posZ);
	}

	@Override
	public GameProfile getOwner() {
		return getAccessHandler().getOwner();
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return errorLogic;
	}

	@Override
	public ChunkCoordinates getCoordinates() {
		return new ChunkCoordinates((int) posX, (int) posY, (int) posZ);
	}

	@Override
	public Vec3 getBeeFXCoordinates() {
		return Vec3.createVectorHelper(posX, posY + 0.25, posZ);
	}

	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(beeLogic.getBeeProgressPercent());
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		breedingProgressPercent = data.readVarInt();
	}

	@Override
	public int getHealthScaled(int i) {
		return (breedingProgressPercent * i) / 100;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!worldObj.isRemote) {
			if (beeLogic.canWork()) {
				beeLogic.doWork();
			}
		} else {
			if (needsActiveUpdate) {
				IForestryPacketServer packet = new PacketBeeLogicEntityRequest(this);
				Proxies.net.sendToServer(packet);
				needsActiveUpdate = false;
			}

			if (beeLogic.canDoBeeFX()) {
				if (worldObj.getTotalWorldTime() % beeFXInterval == beeFXTime) {
					beeLogic.doBeeFX();
				}

				if (worldObj.getTotalWorldTime() % pollenFXInterval == pollenFXTime) {
					TileBeeHousingBase.doPollenFX(worldObj, posX - 0.5, posY - 0.1, posZ - 0.5);
				}
			}
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
		super.readEntityFromNBT(nbtTagCompound);
		beeLogic.readFromNBT(nbtTagCompound);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
		super.writeEntityToNBT(nbtTagCompound);
		beeLogic.writeToNBT(nbtTagCompound);
	}
}
