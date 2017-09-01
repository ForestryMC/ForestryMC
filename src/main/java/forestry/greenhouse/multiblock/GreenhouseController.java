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
package forestry.greenhouse.multiblock;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.ClimateStateType;
import forestry.api.climate.IClimateState;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ICamouflagedTile;
import forestry.api.core.IErrorLogic;
import forestry.api.greenhouse.IGreenhouseListener;
import forestry.api.greenhouse.IGreenhouseLogic;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IGreenhouseComponent.Active;
import forestry.api.multiblock.IGreenhouseComponent.Listener;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.PluginCore;
import forestry.core.climate.ClimateContainer;
import forestry.core.climate.ClimateState;
import forestry.core.config.Config;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.multiblock.MultiblockValidationException;
import forestry.core.multiblock.RectangularMultiblockControllerBase;
import forestry.core.network.PacketBufferForestry;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;
import forestry.energy.EnergyManager;
import forestry.greenhouse.api.climate.GreenhouseState;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.climate.IClimateContainerListener;
import forestry.greenhouse.api.greenhouse.GreenhouseManager;
import forestry.greenhouse.api.greenhouse.IGreenhouseBlock;
import forestry.greenhouse.api.greenhouse.IGreenhouseLimits;
import forestry.greenhouse.api.greenhouse.IGreenhouseProvider;
import forestry.greenhouse.api.greenhouse.IGreenhouseProviderListener;
import forestry.greenhouse.api.greenhouse.Position2D;
import forestry.greenhouse.camouflage.CamouflageHandlerType;
import forestry.greenhouse.multiblock.blocks.storage.GreenhouseProvider;
import forestry.greenhouse.multiblock.blocks.storage.GreenhouseProviderClient;
import forestry.greenhouse.multiblock.blocks.storage.GreenhouseProviderServer;
import forestry.greenhouse.multiblock.blocks.world.GreenhouseBlockManager;
import forestry.greenhouse.network.packets.PacketCamouflageSelectionServer;
import forestry.greenhouse.network.packets.PacketGreenhouseDataRequest;
import forestry.greenhouse.tiles.TileGreenhousePlain;

public class GreenhouseController extends RectangularMultiblockControllerBase implements IGreenhouseControllerInternal, IClimateContainerListener, IGreenhouseProviderListener {

	public static final String TYPE = "for.multiblock.greenhouse.type";
	public static final int CENTER_HEIGHT = 3;
	public static final int REGION_DEPTH = 6;
	private static final String CAMOUFLAGE_NBT_KEY = "Camouflage";
	private static final String CONTAINER_NBT_KEY = "Container";
	private static final String CENTER_POSITION_NBT_KEY = "Center";
	private final Set<Listener> listenerComponents = new HashSet<>();
	private final Set<Active> activeComponents = new HashSet<>();
	private final Collection<IGreenhouseLogic> logics;
	private final EnergyManager energyManager;
	//Provider
	private final GreenhouseProvider provider;
	@Nullable
	private IGreenhouseLimits limits;
	//Camouflage blocks
	private ItemStack camouflage;
	//Climate
	private ClimateContainer climateContainer;
	private IClimateState defaultState = ClimateState.MIN;
	private BlockPos centerPos;
	private int assembleTickCount;
	@SideOnly(Side.CLIENT)
	private boolean requestedData;

	public GreenhouseController(World world) {
		super(world, GreenhouseMultiblockSizeLimits.instance);

		this.energyManager = new EnergyManager(200, 100000);

		this.camouflage = getDefaultCamouflageBlock();
		this.climateContainer = new ClimateContainer(this);
		this.climateContainer.addListener(this);
		this.centerPos = BlockPos.ORIGIN;
		if (world.isRemote) {
			provider = new GreenhouseProviderClient(world, climateContainer);
		} else {
			provider = new GreenhouseProviderServer(world, climateContainer);
		}
		provider.addListener(this);
		this.logics = GreenhouseManager.helper.createLogics(this);
		for (IGreenhouseLogic logic : logics) {
			if (logic instanceof IGreenhouseProviderListener) {
				this.provider.addListener((IGreenhouseProviderListener) logic);
			}
		}
	}

	/**
	 * The default camouflage block {@link ItemStack} for every greenhouse block.
	 */
	public static ItemStack createDefaultCamouflageBlock() {
		return new ItemStack(PluginCore.getBlocks().ashBrick);
	}

	/* IClimateHousing */
	@Override
	public BlockPos getCoordinates() {
		return getReferenceCoord();
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromValue(getExactTemperature());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Override
	public float getExactTemperature() {
		return climateContainer.getState().getTemperature();
	}

	@Override
	public float getExactHumidity() {
		return climateContainer.getState().getHumidity();
	}

	@Override
	public IClimateContainer getClimateContainer() {
		return climateContainer;
	}

	/* IGreenhouseHousing */
	@Override
	public int getSize() {
		return provider.getSize();
	}

	@Override
	public void onUpdateClimate() {
		if (provider instanceof GreenhouseProviderClient) {
			GreenhouseProviderClient providerClient = (GreenhouseProviderClient) provider;
			providerClient.markBlockForRenderUpdate();
		}
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return provider.getErrorLogic();
	}

	@Override
	public IClimateState getDefaultClimate() {
		return defaultState;
	}

	@Override
	public IGreenhouseProvider getProvider() {
		return provider;
	}

	@Override
	public IGreenhouseLimits getLimits() {
		return limits;
	}

	@Override
	public boolean isClosed(IClimateContainer container) {
		return provider.isClosed();
	}

	/* IMultiblockControllerInternal */
	@Override
	public void formatDescriptionPacket(NBTTagCompound data) {
		writeToNBT(data);
	}

	@Override
	public void decodeDescriptionPacket(NBTTagCompound data) {
		readFromNBT(data);
	}

	@Override
	public String getUnlocalizedType() {
		return TYPE;
	}

	/* MultiblockControllerBase */
	@Override
	protected void onAttachedPartWithMultiblockData(IMultiblockComponent part, NBTTagCompound data) {
		readFromNBT(data);
	}

	@Override
	protected void onBlockAdded(IMultiblockComponent newPart) {
		if (newPart instanceof Active) {
			activeComponents.add((Active) newPart);
		}
		if (newPart instanceof Listener) {
			listenerComponents.add((Listener) newPart);
		}
	}

	@Override
	protected void onBlockRemoved(IMultiblockComponent oldPart) {
		if (oldPart instanceof Active) {
			activeComponents.remove(oldPart);
		}
		if (oldPart instanceof Listener) {
			listenerComponents.remove(oldPart);
		}
	}

	@Override
	protected void onMachineRestored() {
		super.onMachineRestored();
	}

	@Override
	protected void onMachinePaused() {
		provider.clear(true);
		provider.getStorage().removeProviderFromChunks();
		this.centerPos = BlockPos.ORIGIN;
	}

	@Override
	protected void onMachineDisassembled() {
		super.onMachineDisassembled();

		provider.clear(false);
		provider.getStorage().removeProviderFromChunks();

		this.centerPos = BlockPos.ORIGIN;

		for (IMultiblockComponent comp : connectedParts) {
			if (comp instanceof ICamouflagedTile) {
				world.markBlockRangeForRenderUpdate(comp.getCoordinates(), comp.getCoordinates());
			}
		}
		limits = null;
	}

	@Override
	protected void onAssimilate(IMultiblockControllerInternal assimilated) {
	}

	@Override
	public void onAssimilated(IMultiblockControllerInternal assimilator) {
	}

	/* UPDATES */
	@Override
	protected boolean updateServer(int tickCount) {
		if (provider.getState() == GreenhouseState.UNLOADED) {
			if (assembleTickCount + 20 <= tickCount) {
				provider.create();
			}
		} else {
			if (canWork()) {
				climateContainer.updateClimate(tickCount);
			}

			for (Active activeComponent : activeComponents) {
				activeComponent.updateServer(tickCount);
			}

			for (IGreenhouseLogic logic : logics) {
				logic.work(tickCount);
			}
		}

		return false;
	}

	@Override
	protected void updateClient(int tickCount) {
		if (!requestedData && provider.getState() == GreenhouseState.UNLOADED) {
			NetworkUtil.sendToServer(new PacketGreenhouseDataRequest(getReferenceCoord()));
			requestedData = true;
		}
	}

	/* IStreamableGui */
	@Override
	public void writeGuiData(PacketBufferForestry data) {
		energyManager.writeData(data);
		data.writeItemStack(camouflage);
		climateContainer.writeData(data);
	}

	@Override
	public void readGuiData(PacketBufferForestry data) throws IOException {
		energyManager.readData(data);
		camouflage = data.readItemStack();
		climateContainer.readData(data);
	}

	/* ICamouflageHandler */
	@Override
	public ItemStack getCamouflageBlock() {
		return camouflage;
	}

	@Override
	public ItemStack getDefaultCamouflageBlock() {
		return createDefaultCamouflageBlock();
	}

	@Override
	public boolean setCamouflageBlock(ItemStack camouflageBlock, boolean sendClientUpdate) {
		if (!ItemStackUtil.isIdenticalItem(camouflageBlock, camouflage)) {
			camouflage = camouflageBlock;
			if (sendClientUpdate && world.isRemote) {
				for (IMultiblockComponent comp : connectedParts) {
					if (comp instanceof ICamouflagedTile) {
						ICamouflagedTile camBlock = (ICamouflagedTile) comp;

						BlockPos coordinates = camBlock.getCoordinates();
						world.markBlockRangeForRenderUpdate(coordinates, coordinates);
					}
				}
				NetworkUtil.sendToServer(new PacketCamouflageSelectionServer(this, CamouflageHandlerType.STRUCTURE));
			}
			return true;
		}
		return false;
	}

	/* IGreenhouseControllerInternal */
	@Override
	public EnergyManager getEnergyManager() {
		return energyManager;
	}

	@Override
	public boolean canWork() {
		boolean canWork = true;
		for (IGreenhouseComponent.Listener listenerComponent : listenerComponents) {
			IGreenhouseListener listener = listenerComponent.getGreenhouseListener();
			canWork = listener.canWork(this, canWork);
		}
		return canWork;
	}

	@Override
	public Set<Listener> getListenerComponents() {
		return listenerComponents;
	}

	/* IGreenhouseController */
	@Override
	public BlockPos getCenterCoordinates() {
		return getTopCenterCoord();
	}

	@Override
	public void setCenterCoordinates(BlockPos coordinates) {
		if (!coordinates.equals(centerPos) && isAssembled()) {
			centerPos = coordinates;
			provider.init(centerPos, limits);
			if (!world.isRemote) {
				assembleTickCount = getTickCount();
			}
		}
	}

	/* MultiblockControllerForestry */
	@Override
	protected void onMachineAssembled() {
		super.onMachineAssembled();

		BlockPos centerTop = getTopCenterCoord();
		centerPos = centerTop.up(CENTER_HEIGHT);

		createDefaultState();
		limits = createLimits();
		if(!climateContainer.getTargetedState().isPresent()) {
			IClimateState defaultClimate = getDefaultClimate();
			climateContainer.setState(defaultClimate.toState(ClimateStateType.MUTABLE));
			climateContainer.setTargetedState(defaultClimate);
		}

		provider.init(centerPos, limits);
		assembleTickCount = getTickCount();
	}

	/* SAVING & LOADING */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);

		energyManager.writeToNBT(data);

		data.setTag(CAMOUFLAGE_NBT_KEY, camouflage.serializeNBT());

		data.setTag(CONTAINER_NBT_KEY, climateContainer.writeToNBT(new NBTTagCompound()));

		data.setTag(CENTER_POSITION_NBT_KEY, NBTUtil.createPosTag(centerPos));

		for (IGreenhouseLogic logic : logics) {
			data.setTag(logic.getUID(), logic.writeToNBT(new NBTTagCompound()));
		}

		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		energyManager.readFromNBT(data);

		if (data.hasKey(CAMOUFLAGE_NBT_KEY)) {
			NBTTagCompound nbtTag = data.getCompoundTag(CAMOUFLAGE_NBT_KEY);
			camouflage = new ItemStack(nbtTag);
		}

		if (data.hasKey(CONTAINER_NBT_KEY)) {
			NBTTagCompound nbtTag = data.getCompoundTag(CONTAINER_NBT_KEY);
			climateContainer.readFromNBT(nbtTag);
		}

		if (data.hasKey(CENTER_POSITION_NBT_KEY)) {
			centerPos = NBTUtil.getPosFromTag(data.getCompoundTag(CENTER_POSITION_NBT_KEY));
		}

		for (IGreenhouseLogic logic : logics) {
			logic.readFromNBT(data.getCompoundTag(logic.getUID()));
		}
	}

	private void createDefaultState() {
		BlockPos maximumCoord = getMaximumCoord();
		BlockPos minimumCoord = getMinimumCoord();

		double biomes = 0;
		double temperature = 0;
		double humidity = 0;
		for (int x = minimumCoord.getX(); x <= maximumCoord.getX(); x++) {
			for (int z = minimumCoord.getZ(); z <= maximumCoord.getZ(); z++) {
				Biome biome = world.getBiome(new BlockPos(x, 0, z));
				temperature += biome.getTemperature();
				humidity += biome.getRainfall();
				biomes++;
			}
		}
		defaultState = new ClimateState((float) (temperature / biomes), (float) (humidity / biomes), ClimateStateType.IMMUTABLE);
	}

	/* RectangularMultiblockControllerBase */
	@Override
	public void isGoodForExteriorLevel(IMultiblockComponent part, int level) throws MultiblockValidationException {
		int maxLevel = getMaximumCoord().getY() - getMinimumCoord().getY();
		if (level == maxLevel && !(part instanceof TileGreenhousePlain)) {
			throw new MultiblockValidationException(Translator.translateToLocal("for.multiblock.greenhouse.error.needPlainOnTop"));
		}
		IGreenhouseBlock logicBlock = GreenhouseBlockManager.getInstance().getBlock(world, part.getCoordinates());
		if (logicBlock != null && logicBlock.getProvider() != getProvider()) {
			throw new MultiblockValidationException(Translator.translateToLocal("for.multiblock.greenhouse.error.needSpace"));
		}
	}

	@Override
	public void isGoodForInterior(IMultiblockComponent part) throws MultiblockValidationException {
		if (!(part instanceof TileGreenhousePlain)) {
			throw new MultiblockValidationException(Translator.translateToLocal("for.multiblock.greenhouse.error.needPlainInterior"));
		}
		IGreenhouseBlock logicBlock = GreenhouseBlockManager.getInstance().getBlock(world, ((TileGreenhousePlain) part).getPos());
		if (logicBlock != null && logicBlock.getProvider() != getProvider()) {
			throw new MultiblockValidationException(Translator.translateToLocal("for.multiblock.greenhouse.error.needSpace"));
		}
	}

	/* IGreenhouseProviderListener */
	@Override
	public void onCheckPosition(BlockPos pos) {
	}

	private IGreenhouseLimits createLimits() {
		BlockPos centerTop = getTopCenterCoord();
		centerPos = centerTop.up(CENTER_HEIGHT);
		BlockPos max = getMaximumCoord();
		BlockPos min = getMinimumCoord();

		int sizeNorthSouth = Math.abs(max.getZ() - min.getZ()) + 1;
		int sizeEastWest = Math.abs(max.getX() - min.getX()) + 1;

		int height = (Math.abs(max.getY() - min.getY()) + 2) * Config.greenhouseSize;
		int lengthEastWest = sizeEastWest * Config.greenhouseSize;
		int lengthNorthSouth = sizeNorthSouth * Config.greenhouseSize;
		return new GreenhouseLimits(new Position2D(lengthEastWest, lengthNorthSouth), new Position2D(-lengthEastWest, -lengthNorthSouth), height, REGION_DEPTH);
	}
}
