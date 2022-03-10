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

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.climate.IClimatised;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.apiculture.gui.IGuiBeeHousingDelegate;
import forestry.apiculture.network.packets.PacketBeeLogicEntityRequest;
import forestry.apiculture.tiles.TileBeeHousingBase;
import forestry.core.entities.MinecartEntityContainerForestry;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.owner.GameProfileDataSerializer;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.TickHelper;

public abstract class MinecartEntityBeeHousingBase extends MinecartEntityContainerForestry implements IBeeHousing, IOwnedTile, IGuiBeeHousingDelegate, IClimatised, IStreamableGui {
	private static final EntityDataAccessor<Optional<GameProfile>> OWNER = SynchedEntityData.defineId(MinecartEntityBeeHousingBase.class, GameProfileDataSerializer.INSTANCE);

	private static final int beeFXInterval = 4;
	private static final int pollenFXInterval = 50;

	private final TickHelper tickHelper = new TickHelper();

	private final IBeekeepingLogic beeLogic = BeeManager.beeRoot.createBeekeepingLogic(this);
	private final IErrorLogic errorLogic = ForestryAPI.errorStateRegistry.createErrorLogic();
	private final OwnerHandler ownerHandler = new OwnerHandler() {
		@Override
		public void setOwner(GameProfile owner) {
			super.setOwner(owner);
			entityData.set(OWNER, Optional.of(owner));
		}

		@Override
		public GameProfile getOwner() {
			Optional<GameProfile> gameProfileOptional = entityData.get(OWNER);
			return gameProfileOptional.orElse(null);
		}
	};

	// CLIENT
	private int breedingProgressPercent = 0;
	private boolean needsActiveUpdate = true;

	public MinecartEntityBeeHousingBase(EntityType<? extends MinecartEntityBeeHousingBase> type, Level world) {
		super(type, world);
	}

	public MinecartEntityBeeHousingBase(EntityType<?> type, Level world, double posX, double posY, double posZ) {
		super(type, world, posX, posY, posZ);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(OWNER, Optional.empty());
	}

	/* IOwnedTile */
	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	/* IBeeHousing */
	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return beeLogic;
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromBiome(getBiome(), blockPosition());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getBiome().getDownfall());
	}

	@Override
	public float getExactTemperature() {
		return 0; // getBiome().getTemperature(blockPosition());
	}

	@Override
	public float getExactHumidity() {
		return getBiome().getDownfall();
	}

	@Override
	public int getBlockLightValue() {
		return level.getMaxLocalRawBrightness(blockPosition().above());
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return level.canSeeSkyFromBelowWater(blockPosition().above());
	}

	@Override
	public boolean isRaining() {
		return level.isRainingAt(blockPosition().above());
	}

	@Override
	public Level getWorldObj() {
		return level;
	}

	@Override
	public Biome getBiome() {
		return level.getBiome(blockPosition()).value();
	}

	@Override
	public GameProfile getOwner() {
		return getOwnerHandler().getOwner();
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return errorLogic;
	}

	@Override
	public BlockPos getCoordinates() {
		return blockPosition();
	}

	@Override
	public Vec3 getBeeFXCoordinates() {
		BlockPos pos = blockPosition();
		return new Vec3(pos.getX(), pos.getY() + 0.25, pos.getZ());
	}

	@Override
	public void writeGuiData(PacketBufferForestry data) {
		data.writeVarInt(beeLogic.getBeeProgressPercent());
	}

	@Override
	public void readGuiData(PacketBufferForestry data) {
		breedingProgressPercent = data.readVarInt();
	}

	@Override
	public int getHealthScaled(int i) {
		return breedingProgressPercent * i / 100;
	}

	@Override
	public void tick() {
		super.tick();
		tickHelper.onTick();
		if (!level.isClientSide) {
			if (beeLogic.canWork()) {
				beeLogic.doWork();
			}
		} else {
			if (needsActiveUpdate) {
				IForestryPacketServer packet = new PacketBeeLogicEntityRequest(this);
				NetworkUtil.sendToServer(packet);
				needsActiveUpdate = false;
			}

			if (beeLogic.canDoBeeFX()) {
				if (tickHelper.updateOnInterval(beeFXInterval)) {
					beeLogic.doBeeFX();
				}

				if (tickHelper.updateOnInterval(pollenFXInterval)) {
					BlockPos pos = blockPosition();
					TileBeeHousingBase.doPollenFX(level, pos.getX() - 0.5, pos.getY() - 0.1, pos.getZ() - 0.5);
				}
			}
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compoundNBT) {
		super.readAdditionalSaveData(compoundNBT);
		beeLogic.read(compoundNBT);
		ownerHandler.read(compoundNBT);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compoundNBT) {
		super.addAdditionalSaveData(compoundNBT);
		beeLogic.write(compoundNBT);
		ownerHandler.write(compoundNBT);
	}
}
