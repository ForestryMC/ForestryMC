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

import com.mojang.authlib.GameProfile;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.apiculture.gui.IGuiBeeHousingDelegate;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import forestry.core.render.ParticleRender;
import forestry.core.tiles.IClimatised;
import forestry.core.tiles.TileBase;
import forestry.core.utils.ClimateUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileBeeHousingBase extends TileBase implements IBeeHousing, IOwnedTile, IClimatised, IGuiBeeHousingDelegate, IStreamableGui {
	private final String hintKey;
	private final OwnerHandler ownerHandler = new OwnerHandler();
	private final IBeekeepingLogic beeLogic;

	// CLIENT
	private int breedingProgressPercent = 0;

	protected TileBeeHousingBase(String hintKey) {
		this.hintKey = hintKey;
		this.beeLogic = BeeManager.beeRoot.createBeekeepingLogic(this);
	}

	@Override
	public String getHintKey() {
		return hintKey;
	}

	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return beeLogic;
	}

	/* LOADING & SAVING */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
		beeLogic.writeToNBT(nbttagcompound);
		ownerHandler.writeToNBT(nbttagcompound);
		return nbttagcompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		beeLogic.readFromNBT(nbttagcompound);
		ownerHandler.readFromNBT(nbttagcompound);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound updateTag = super.getUpdateTag();
		beeLogic.writeToNBT(updateTag);
		ownerHandler.writeToNBT(updateTag);
		return updateTag;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		beeLogic.readFromNBT(tag);
		ownerHandler.readFromNBT(tag);
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	/* ICLIMATISED */
	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromBiome(getBiome(), world, getPos());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Override
	public float getExactTemperature() {
		return ClimateUtil.getTemperature(world, getPos());
	}

	@Override
	public float getExactHumidity() {
		return ClimateUtil.getHumidity(world, getPos());
	}

	/* UPDATING */
	@Override
	@SideOnly(Side.CLIENT)
	public void updateClientSide() {
		if (beeLogic.canDoBeeFX() && updateOnInterval(4)) {
			beeLogic.doBeeFX();

			if (updateOnInterval(50)) {
				doPollenFX(world, getPos().getX(), getPos().getY(), getPos().getZ());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void doPollenFX(World world, double xCoord, double yCoord, double zCoord) {
		double fxX = xCoord + 0.5F;
		double fxY = yCoord + 0.25F;
		double fxZ = zCoord + 0.5F;
		float distanceFromCenter = 0.6F;
		float leftRightSpreadFromCenter = distanceFromCenter * (world.rand.nextFloat() - 0.5F);
		float upSpread = world.rand.nextFloat() * 6F / 16F;
		fxY += upSpread;

		ParticleRender.addEntityHoneyDustFX(world, fxX - distanceFromCenter, fxY, fxZ + leftRightSpreadFromCenter);
		ParticleRender.addEntityHoneyDustFX(world, fxX + distanceFromCenter, fxY, fxZ + leftRightSpreadFromCenter);
		ParticleRender.addEntityHoneyDustFX(world, fxX + leftRightSpreadFromCenter, fxY, fxZ - distanceFromCenter);
		ParticleRender.addEntityHoneyDustFX(world, fxX + leftRightSpreadFromCenter, fxY, fxZ + distanceFromCenter);
	}

	@Override
	public void updateServerSide() {
		if (beeLogic.canWork()) {
			beeLogic.doWork();
		}
	}

	@Override
	public int getHealthScaled(int i) {
		return breedingProgressPercent * i / 100;
	}

	@Override
	public void writeGuiData(PacketBufferForestry data) {
		data.writeVarInt(beeLogic.getBeeProgressPercent());
	}

	@Override
	public void readGuiData(PacketBufferForestry data) throws IOException {
		breedingProgressPercent = data.readVarInt();
	}

	// / IBEEHOUSING
	@Override
	public Biome getBiome() {
		return world.getBiome(getPos());
	}

	@Override
	public int getBlockLightValue() {
		return world.getLightFromNeighbors(getPos().up());
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return world.canBlockSeeSky(getPos().up());
	}

	@Override
	public boolean isRaining() {
		return world.isRainingAt(getPos().up());
	}

	@Override
	public GameProfile getOwner() {
		return getOwnerHandler().getOwner();
	}

	@Override
	public Vec3d getBeeFXCoordinates() {
		return new Vec3d(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);
	}

}
