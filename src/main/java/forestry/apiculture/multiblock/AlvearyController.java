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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import com.mojang.authlib.GameProfile;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.DefaultBeeModifier;
import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IClimateControlled;
import forestry.apiculture.BeeHousingInventory;
import forestry.apiculture.BeeHousingModifier;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.CoordTriplet;
import forestry.core.multiblock.IMultiblockPart;
import forestry.core.multiblock.MultiblockControllerBase;
import forestry.core.multiblock.MultiblockTileEntityBase;
import forestry.core.multiblock.MultiblockValidationException;
import forestry.core.multiblock.rectangular.RectangularMultiblockControllerBase;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.PacketGuiUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumAccess;
import forestry.core.utils.Utils;

public class AlvearyController extends RectangularMultiblockControllerBase
		implements IAlvearyController, IClimateControlled {

	private final BeeHousingInventory inventory;
	private final IBeekeepingLogic beekeepingLogic;

	private BiomeGenBase cachedBiome;
	private float tempChange = 0.0f;
	private float humidChange = 0.0f;

	// PARTS
	private final Set<IBeeModifier> beeModifiers = new HashSet<IBeeModifier>();
	private final Set<IBeeListener> beeListeners = new HashSet<IBeeListener>();
	private final Set<IAlvearyComponent.Climatiser> climatisers = new HashSet<IAlvearyComponent.Climatiser>();
	private final Set<IAlvearyComponent.Active> activeComponents = new HashSet<IAlvearyComponent.Active>();
	private final Set<IAlvearyComponent> allComponents = new HashSet<IAlvearyComponent>();

	// CLIENT
	private int breedingProgressPercent = 0;

	public AlvearyController(World world) {
		super(world);
		this.inventory = new BeeHousingInventory(9, "Items", getAccessHandler());
		this.beekeepingLogic = BeeManager.beeRoot.createBeekeepingLogic(this);

		this.beeModifiers.add(new AlvearyBeeModifier());
	}

	@Override
	public IBeeHousingInventory getBeeInventory() {
		return inventory;
	}

	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return beekeepingLogic;
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return inventory;
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return beeListeners;
	}

	@Override
	public Iterable<IBeeModifier> getBeeModifiers() {
		return beeModifiers;
	}

	@Override
	public Iterable<IAlvearyComponent> getComponents() {
		return allComponents;
	}

	@Override
	public void onAttachedPartWithMultiblockData(IMultiblockPart part, NBTTagCompound data) {
		this.readFromNBT(data);
	}

	@Override
	protected void onBlockAdded(IMultiblockPart newPart) {
		if (newPart instanceof IAlvearyComponent) {
			allComponents.add((IAlvearyComponent) newPart);

			if (newPart instanceof IAlvearyComponent.BeeModifier) {
				IAlvearyComponent.BeeModifier alvearyBeeModifier = (IAlvearyComponent.BeeModifier) newPart;
				IBeeModifier beeModifier = alvearyBeeModifier.getBeeModifier();
				beeModifiers.add(beeModifier);
			}

			if (newPart instanceof IAlvearyComponent.BeeListener) {
				IAlvearyComponent.BeeListener beeListenerSource = (IAlvearyComponent.BeeListener) newPart;
				IBeeListener beeListener = beeListenerSource.getBeeListener();
				beeListeners.add(beeListener);
			}

			if (newPart instanceof IAlvearyComponent.Climatiser) {
				climatisers.add((IAlvearyComponent.Climatiser) newPart);
			}

			if (newPart instanceof IAlvearyComponent.Active) {
				activeComponents.add((IAlvearyComponent.Active) newPart);
			}
		}
	}

	@Override
	protected void onBlockRemoved(IMultiblockPart oldPart) {
		if (oldPart instanceof IAlvearyComponent) {
			allComponents.remove(oldPart);

			if (oldPart instanceof IAlvearyComponent.BeeModifier) {
				IAlvearyComponent.BeeModifier alvearyBeeModifier = (IAlvearyComponent.BeeModifier) oldPart;
				IBeeModifier beeModifier = alvearyBeeModifier.getBeeModifier();
				beeModifiers.remove(beeModifier);
			}

			if (oldPart instanceof IAlvearyComponent.BeeListener) {
				IAlvearyComponent.BeeListener beeListenerSource = (IAlvearyComponent.BeeListener) oldPart;
				IBeeListener beeListener = beeListenerSource.getBeeListener();
				beeListeners.remove(beeListener);
			}

			if (oldPart instanceof IAlvearyComponent.Climatiser) {
				climatisers.remove(oldPart);
			}

			if (oldPart instanceof IAlvearyComponent.Active) {
				activeComponents.remove(oldPart);
			}
		}
	}

	@Override
	protected void isMachineWhole() throws MultiblockValidationException {
		super.isMachineWhole();

		final CoordTriplet maximumCoord = getMaximumCoord();
		final CoordTriplet minimumCoord = getMinimumCoord();

		// check that the top is covered in wood slabs

		final int slabY = maximumCoord.pos.getY() + 1;
		for (int slabX = minimumCoord.pos.getX(); slabX <= maximumCoord.pos.getX(); slabX++) {
			for (int slabZ = minimumCoord.pos.getZ(); slabZ <= maximumCoord.pos.getZ(); slabZ++) {
				IBlockState state = worldObj.getBlockState(new BlockPos(slabX, slabY, slabZ));
				Block block = state.getBlock();
				if (!Utils.isWoodSlabBlock(block)) {
					throw new MultiblockValidationException(
							StatCollector.translateToLocal("for.multiblock.alveary.error.needSlabs"));
				}

				int meta = block.getMetaFromState(state);
				if ((meta & 8) != 0) {
					throw new MultiblockValidationException(
							StatCollector.translateToLocal("for.multiblock.alveary.error.needSlabs"));
				}
			}
		}

		// check that there is space all around the alveary entrances

		int airY = maximumCoord.pos.getY();
		for (int airX = minimumCoord.pos.getX() - 1; airX <= maximumCoord.pos.getX() + 1; airX++) {
			for (int airZ = minimumCoord.pos.getZ() - 1; airZ <= maximumCoord.pos.getZ() + 1; airZ++) {
				if (isCoordInMultiblock(airX, airY, airZ)) {
					continue;
				}
				Block block = worldObj.getBlockState(new BlockPos(airX, airY, airZ)).getBlock();
				if (block.isOpaqueCube()) {
					throw new MultiblockValidationException(
							StatCollector.translateToLocal("for.multiblock.alveary.error.needSpace"));
				}
			}
		}
	}

	@Override
	protected int getMinimumNumberOfBlocksForAssembledMachine() {
		return 27;
	}

	@Override
	protected int getMaximumXSize() {
		return 3;
	}

	@Override
	protected int getMaximumZSize() {
		return 3;
	}

	@Override
	protected int getMaximumYSize() {
		return 3;
	}

	@Override
	protected int getMinimumXSize() {
		return 3;
	}

	@Override
	protected int getMinimumYSize() {
		return 3;
	}

	@Override
	protected int getMinimumZSize() {
		return 3;
	}

	@Override
	protected void onAssimilate(MultiblockControllerBase assimilated) {

	}

	@Override
	protected void onAssimilated(MultiblockControllerBase assimilator) {

	}

	@Override
	protected boolean updateServer(int tickCount) {
		for (IAlvearyComponent.Active activeComponent : activeComponents) {
			activeComponent.updateServer(tickCount);
		}

		final boolean canWork = beekeepingLogic.canWork();
		if (canWork) {
			beekeepingLogic.doWork();
		}

		for (IAlvearyComponent.Climatiser climatiser : climatisers) {
			climatiser.changeClimate(tickCount, this);
		}

		tempChange = equalizeChange(tempChange);
		humidChange = equalizeChange(humidChange);

		return canWork;
	}

	private static float equalizeChange(float change) {
		if (change == 0) {
			return 0;
		}

		change *= 0.95f;
		if (change <= 0.001f && change >= -0.001f) {
			change = 0;
		}
		return change;
	}

	@Override
	protected void updateClient(int tickCount) {
		for (IAlvearyComponent.Active activeComponent : activeComponents) {
			activeComponent.updateClient(tickCount);
		}

		if (beekeepingLogic.canDoBeeFX() && updateOnInterval(2)) {
			beekeepingLogic.doBeeFX();

			if (updateOnInterval(50)) {
				CoordTriplet center = getCenterCoord();
				float fxX = center.pos.getX() + 0.5F;
				float fxY = center.pos.getY() + 1.0F;
				float fxZ = center.pos.getZ() + 0.5F;
				float distanceFromCenter = 1.6F;

				float leftRightSpreadFromCenter = distanceFromCenter * (worldObj.rand.nextFloat() - 0.5F);
				float upSpread = worldObj.rand.nextFloat() * 0.8F;
				fxY += upSpread;

				// display fx on all 4 sides
				Proxies.common.addEntitySwarmFX(worldObj, (fxX - distanceFromCenter), fxY,
						(fxZ + leftRightSpreadFromCenter));
				Proxies.common.addEntitySwarmFX(worldObj, (fxX + distanceFromCenter), fxY,
						(fxZ + leftRightSpreadFromCenter));
				Proxies.common.addEntitySwarmFX(worldObj, (fxX + leftRightSpreadFromCenter), fxY,
						(fxZ - distanceFromCenter));
				Proxies.common.addEntitySwarmFX(worldObj, (fxX + leftRightSpreadFromCenter), fxY,
						(fxZ + distanceFromCenter));
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);

		data.setFloat("TempChange", tempChange);
		data.setFloat("HumidChange", humidChange);

		beekeepingLogic.writeToNBT(data);
		inventory.writeToNBT(data);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		tempChange = data.getFloat("TempChange");
		humidChange = data.getFloat("HumidChange");

		beekeepingLogic.readFromNBT(data);
		inventory.readFromNBT(data);
	}

	@Override
	public void formatDescriptionPacket(NBTTagCompound data) {
		writeToNBT(data);
		beekeepingLogic.syncToClient();
	}

	@Override
	public void decodeDescriptionPacket(NBTTagCompound data) {
		readFromNBT(data);
	}

	/* IActivatable */

	@Override
	public BlockPos getCoordinates() {
		CoordTriplet coord = getCenterCoord();
		return new BlockPos(coord.pos.getX(), coord.pos.getY() + 1, coord.pos.getZ());
	}

	@Override
	public void onSwitchAccess(EnumAccess oldAccess, EnumAccess newAccess) {
		PacketGuiUpdate packet = new PacketGuiUpdate(this);
		Proxies.net.sendNetworkPacket(packet, worldObj);

		if (oldAccess == EnumAccess.SHARED || newAccess == EnumAccess.SHARED) {
			// pipes connected to this need to update
			for (IMultiblockPart part : connectedParts) {
				if (part instanceof MultiblockTileEntityBase) {
					MultiblockTileEntityBase tile = (MultiblockTileEntityBase) part;
					tile.notifyNeighborsOfBlockChange();
				}
			}
			markDirty();
		}
	}

	@Override
	public float getExactTemperature() {
		CoordTriplet coords = getReferenceCoord();
		return getBiome().getFloatTemperature(coords.pos) + tempChange;
	}

	@Override
	public float getExactHumidity() {
		return getBiome().rainfall + humidChange;
	}

	@Override
	public EnumTemperature getTemperature() {
		IBeeModifier beeModifier = new BeeHousingModifier(this);
		if (beeModifier.isHellish() && tempChange >= 0) {
			return EnumTemperature.HELLISH;
		}

		return EnumTemperature.getFromValue(getExactTemperature());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
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
	public BiomeGenBase getBiome() {
		if (cachedBiome == null) {
			CoordTriplet coords = getReferenceCoord();
			cachedBiome = worldObj.getBiomeGenForCoords(coords.pos);
		}
		return cachedBiome;
	}

	@Override
	public int getBlockLightValue() {
		CoordTriplet topCenter = getTopCenterCoord();
		return worldObj.getLightFromNeighbors(
				new BlockPos(topCenter.pos.getX(), topCenter.pos.getY() + 1, topCenter.pos.getZ()));
	}

	@Override
	public boolean canBlockSeeTheSky() {
		CoordTriplet topCenter = getTopCenterCoord();
		return worldObj
				.canBlockSeeSky(new BlockPos(topCenter.pos.getX(), topCenter.pos.getY() + 2, topCenter.pos.getZ()));
	}

	@Override
	public void addTemperatureChange(float change, float boundaryDown, float boundaryUp) {
		BlockPos coordinates = getCoordinates();

		float temperature = getBiome().getFloatTemperature(coordinates);

		tempChange += change;
		tempChange = Math.max(boundaryDown - temperature, tempChange);
		tempChange = Math.min(boundaryUp - temperature, tempChange);
	}

	@Override
	public void addHumidityChange(float change, float boundaryDown, float boundaryUp) {
		float humidity = getBiome().rainfall;

		humidChange += change;
		humidChange = Math.max(boundaryDown - humidity, humidChange);
		humidChange = Math.min(boundaryUp - humidity, humidChange);
	}

	/* GUI */
	@Override
	public int getHealthScaled(int i) {
		return (breedingProgressPercent * i) / 100;
	}

	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		super.writeGuiData(data);
		data.writeVarInt(beekeepingLogic.getBeeProgressPercent());
		data.writeVarInt(Math.round(tempChange * 100));
		data.writeVarInt(Math.round(humidChange * 100));
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		super.readGuiData(data);
		breedingProgressPercent = data.readVarInt();
		tempChange = data.readVarInt() / 100.0F;
		humidChange = data.readVarInt() / 100.0F;
	}

	private static class AlvearyBeeModifier extends DefaultBeeModifier {
		@Override
		public float getTerritoryModifier(IBeeGenome genome, float currentModifier) {
			return 2.0f;
		}
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public IChatComponent getDisplayName() {
		return null;
	}

}
