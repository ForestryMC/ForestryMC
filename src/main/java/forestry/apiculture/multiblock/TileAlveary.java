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
package forestry.apiculture.multiblock;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogic;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.apiculture.blocks.BlockAlveary;
import forestry.core.access.EnumAccess;
import forestry.core.access.IAccessHandler;
import forestry.core.access.IRestrictedAccess;
import forestry.core.multiblock.MultiblockTileEntityForestry;

public abstract class TileAlveary extends MultiblockTileEntityForestry<MultiblockLogicAlveary> implements IBeeHousing, IRestrictedAccess, IAlvearyComponent {
	public static final int PLAIN_META = 0;
	public static final int ENTRANCE_META = 1;
	public static final int SWARMER_META = 2;
	public static final int FAN_META = 3;
	public static final int HEATER_META = 4;
	public static final int HYGRO_META = 5;
	public static final int STABILIZER_META = 6;
	public static final int SIEVE_META = 7;

	protected TileAlveary() {
		super(new MultiblockLogicAlveary());
	}

	/* TEXTURES */
	public int getIcon(int side) {
		return BlockAlveary.PLAIN;
	}

	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, ChunkCoordinates minCoord, ChunkCoordinates maxCoord) {
		// Re-render this block on the client
		if (worldObj.isRemote) {
			this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		markDirty();
	}

	@Override
	public void onMachineBroken() {
		// Re-render this block on the client
		if (worldObj.isRemote) {
			this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		markDirty();
	}

	/* IHousing */
	@Override
	public BiomeGenBase getBiome() {
		return getMultiblockLogic().getController().getBiome();
	}

	/* IBeeHousing */
	@Override
	public Iterable<IBeeModifier> getBeeModifiers() {
		return getMultiblockLogic().getController().getBeeModifiers();
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return getMultiblockLogic().getController().getBeeListeners();
	}

	@Override
	public IBeeHousingInventory getBeeInventory() {
		return getMultiblockLogic().getController().getBeeInventory();
	}

	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return getMultiblockLogic().getController().getBeekeepingLogic();
	}

	@Override
	public Vec3 getBeeFXCoordinates() {
		return getMultiblockLogic().getController().getBeeFXCoordinates();
	}

	/* IClimatised */
	@Override
	public EnumTemperature getTemperature() {
		return getMultiblockLogic().getController().getTemperature();
	}

	@Override
	public EnumHumidity getHumidity() {
		return getMultiblockLogic().getController().getHumidity();
	}

	@Override
	public int getBlockLightValue() {
		return getMultiblockLogic().getController().getBlockLightValue();
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return getMultiblockLogic().getController().canBlockSeeTheSky();
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return getMultiblockLogic().getController().getErrorLogic();
	}

	@Override
	public IAccessHandler getAccessHandler() {
		return getMultiblockLogic().getController().getAccessHandler();
	}

	@Override
	public void onSwitchAccess(EnumAccess oldAccess, EnumAccess newAccess) {
		getMultiblockLogic().getController().onSwitchAccess(oldAccess, newAccess);
	}
}
