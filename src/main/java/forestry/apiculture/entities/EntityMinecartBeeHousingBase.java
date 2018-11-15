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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.apiculture.gui.IGuiBeeHousingDelegate;
import forestry.apiculture.network.packets.PacketBeeLogicEntityRequest;
import forestry.apiculture.tiles.TileBeeHousingBase;
import forestry.core.entities.EntityMinecartContainerForestry;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.owner.GameProfileDataSerializer;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import forestry.core.tiles.IClimatised;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.TickHelper;

public abstract class EntityMinecartBeeHousingBase extends EntityMinecartContainerForestry implements IBeeHousing, IOwnedTile, IGuiBeeHousingDelegate, IClimatised, IStreamableGui {
	private static final DataParameter<Optional<GameProfile>> OWNER = EntityDataManager.createKey(EntityMinecartBeeHousingBase.class, GameProfileDataSerializer.INSTANCE);

	private static final int beeFXInterval = 4;
	private static final int pollenFXInterval = 50;

	private final TickHelper tickHelper = new TickHelper();

	private final IBeekeepingLogic beeLogic = BeeManager.beeRoot.createBeekeepingLogic(this);
	private final IErrorLogic errorLogic = ForestryAPI.errorStateRegistry.createErrorLogic();
	private final OwnerHandler ownerHandler = new OwnerHandler() {
		@Override
		public void setOwner(GameProfile owner) {
			super.setOwner(owner);
			dataManager.set(OWNER, Optional.of(owner));
		}

		@Override
		public GameProfile getOwner() {
			Optional<GameProfile> gameProfileOptional = dataManager.get(OWNER);
			return gameProfileOptional.orElse(null);
		}
	};

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

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(OWNER, Optional.empty());
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
		return EnumTemperature.getFromBiome(getBiome(), getPosition());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getBiome().getRainfall());
	}

	@Override
	public float getExactTemperature() {
		return getBiome().getTemperature(getPosition());
	}

	@Override
	public float getExactHumidity() {
		return getBiome().getRainfall();
	}

	@Override
	public int getBlockLightValue() {
		return world.getLightFromNeighbors(getPosition().up());
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return world.canBlockSeeSky(getPosition().up());
	}

	@Override
	public boolean isRaining() {
		return world.isRainingAt(getPosition().up());
	}

	@Override
	public World getWorldObj() {
		return world;
	}

	@Override
	public Biome getBiome() {
		return world.getBiome(getPosition());
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
		return getPosition();
	}

	@Override
	public Vec3d getBeeFXCoordinates() {
		return new Vec3d(posX, posY + 0.25, posZ);
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
	public void onUpdate() {
		super.onUpdate();
		tickHelper.onTick();
		if (!world.isRemote) {
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
					TileBeeHousingBase.doPollenFX(world, posX - 0.5, posY - 0.1, posZ - 0.5);
				}
			}
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
		super.readEntityFromNBT(nbtTagCompound);
		beeLogic.readFromNBT(nbtTagCompound);
		ownerHandler.readFromNBT(nbtTagCompound);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
		super.writeEntityToNBT(nbtTagCompound);
		beeLogic.writeToNBT(nbtTagCompound);
		ownerHandler.writeToNBT(nbtTagCompound);
	}
}
