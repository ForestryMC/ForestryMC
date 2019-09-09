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

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.climate.ClimateCapabilities;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.apiculture.gui.IGuiBeeHousingDelegate;
import forestry.core.climate.ClimateListener;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import forestry.core.render.ParticleRender;
import forestry.core.tiles.IClimatised;
import forestry.core.tiles.TileBase;

public abstract class TileBeeHousingBase extends TileBase implements IBeeHousing, IOwnedTile, IClimatised, IGuiBeeHousingDelegate, IStreamableGui {
	private final String hintKey;
	private final OwnerHandler ownerHandler = new OwnerHandler();
	private final IBeekeepingLogic beeLogic;
	protected final ClimateListener climateListener;

	// CLIENT
	private int breedingProgressPercent = 0;

	protected TileBeeHousingBase(TileEntityType<?> type, String hintKey) {
		super(type);
		this.hintKey = hintKey;
		this.beeLogic = BeeManager.beeRoot.createBeekeepingLogic(this);

		climateListener = new ClimateListener(this);
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
	public CompoundNBT write(CompoundNBT compoundNBT) {
		compoundNBT = super.write(compoundNBT);
		beeLogic.write(compoundNBT);
		ownerHandler.write(compoundNBT);
		return compoundNBT;
	}

	@Override
	public void read(CompoundNBT compoundNBT) {
		super.read(compoundNBT);
		beeLogic.read(compoundNBT);
		ownerHandler.read(compoundNBT);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT updateTag = super.getUpdateTag();
		beeLogic.write(updateTag);
		ownerHandler.write(updateTag);
		return updateTag;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleUpdateTag(CompoundNBT tag) {
		super.handleUpdateTag(tag);
		beeLogic.read(tag);
		ownerHandler.read(tag);
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	/* ICLIMATISED */
	@Override
	public EnumTemperature getTemperature() {
		return climateListener.getTemperature();
	}

	@Override
	public EnumHumidity getHumidity() {
		return climateListener.getHumidity();
	}

	@Override
	public float getExactTemperature() {
		return climateListener.getExactTemperature();
	}

	@Override
	public float getExactHumidity() {
		return climateListener.getExactHumidity();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == ClimateCapabilities.CLIMATE_LISTENER) {
			return LazyOptional.of(() -> climateListener).cast();
		}
		return super.getCapability(capability, facing);
	}

	/* UPDATING */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void updateClientSide() {
		if (beeLogic.canDoBeeFX() && updateOnInterval(4)) {
			beeLogic.doBeeFX();

			if (updateOnInterval(50)) {
				doPollenFX(world, getPos().getX(), getPos().getY(), getPos().getZ());
			}
		}
		climateListener.updateClientSide(true);
	}

	@OnlyIn(Dist.CLIENT)
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
	public void readGuiData(PacketBufferForestry data) {
		breedingProgressPercent = data.readVarInt();
	}

	// / IBEEHOUSING
	@Override
	public Biome getBiome() {
		return world.getBiome(getPos());
	}

	//TODO check this call
	@Override
	public int getBlockLightValue() {
		return world.getLight(getPos().up());
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
