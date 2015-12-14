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
package forestry.apiculture.tiles;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.apiculture.gui.IGuiBeeHousingInventory;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.IClimatised;
import forestry.core.tiles.TileBase;

public abstract class TileBeeHousingBase extends TileBase implements IBeeHousing, IClimatised, IGuiBeeHousingInventory, IStreamableGui {
	private final IBeekeepingLogic beeLogic;
	private BiomeGenBase cachedBiome;

	// CLIENT
	private int breedingProgressPercent = 0;

	protected TileBeeHousingBase(String hintKey) {
		super(hintKey);
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
		return EnumTemperature.getFromBiome(getBiome(), xCoord, yCoord, zCoord);
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Override
	public float getExactTemperature() {
		return getBiome().getFloatTemperature(xCoord, yCoord, zCoord);
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
				doPollenFX(worldObj, xCoord, yCoord, zCoord);
			}
		}
	}

	public static void doPollenFX(World world, double xCoord, double yCoord, double zCoord) {
		double fxX = xCoord + 0.5F;
		double fxY = yCoord + 0.25F;
		double fxZ = zCoord + 0.5F;
		float distanceFromCenter = 0.6F;
		float leftRightSpreadFromCenter = distanceFromCenter * (world.rand.nextFloat() - 0.5F);
		float upSpread = (world.rand.nextFloat() * 6F) / 16F;
		fxY += upSpread;

		Proxies.render.addEntitySwarmFX(world, (fxX - distanceFromCenter), fxY, (fxZ + leftRightSpreadFromCenter));
		Proxies.render.addEntitySwarmFX(world, (fxX + distanceFromCenter), fxY, (fxZ + leftRightSpreadFromCenter));
		Proxies.render.addEntitySwarmFX(world, (fxX + leftRightSpreadFromCenter), fxY, (fxZ - distanceFromCenter));
		Proxies.render.addEntitySwarmFX(world, (fxX + leftRightSpreadFromCenter), fxY, (fxZ + distanceFromCenter));
	}

	@Override
	public void updateServerSide() {
		if (beeLogic.canWork()) {
			beeLogic.doWork();
		}
	}

	@Override
	public int getHealthScaled(int i) {
		return (breedingProgressPercent * i) / 100;
	}

	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(beeLogic.getBeeProgressPercent());
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		breedingProgressPercent = data.readVarInt();
	}

	// / IBEEHOUSING
	@Override
	public BiomeGenBase getBiome() {
		if (cachedBiome == null) {
			cachedBiome = worldObj.getBiomeGenForCoordsBody(xCoord, zCoord);
		}
		return cachedBiome;
	}

	@Override
	public int getBlockLightValue() {
		return worldObj.getBlockLightValue(xCoord, yCoord + 1, zCoord);
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord);
	}

	@Override
	public GameProfile getOwner() {
		return getAccessHandler().getOwner();
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public Vec3 getBeeFXCoordinates() {
		return Vec3.createVectorHelper(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
	}

}
