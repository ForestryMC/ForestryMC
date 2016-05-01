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

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;

import forestry.api.core.EnumCamouflageType;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ICamouflagedBlock;
import forestry.api.greenhouse.EnumGreenhouseEventType;
import forestry.api.greenhouse.GreenhouseEvents.CamouflageChangeEvent;
import forestry.api.greenhouse.GreenhouseEvents.CheckInternalBlockFaceEvent;
import forestry.api.greenhouse.GreenhouseEvents.CreateInternalBlockEvent;
import forestry.api.greenhouse.GreenhouseManager;
import forestry.api.greenhouse.IGreenhouseLogic;
import forestry.api.greenhouse.IGreenhouseState;
import forestry.api.greenhouse.IInternalBlock;
import forestry.api.greenhouse.IInternalBlockFace;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IGreenhouseComponent.ButterflyHatch;
import forestry.api.multiblock.IGreenhouseController;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.access.EnumAccess;
import forestry.core.config.Constants;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.multiblock.MultiblockValidationException;
import forestry.core.multiblock.RectangularMultiblockControllerBase;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.utils.CamouflageUtil;
import forestry.core.utils.Log;
import forestry.core.utils.Translator;
import forestry.energy.EnergyManager;
import forestry.greenhouse.GreenhouseState;
import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.inventory.InventoryGreenhouse;
import forestry.greenhouse.network.packets.PacketCamouflageUpdate;
import forestry.greenhouse.tiles.TileGreenhouseSprinkler;

public class GreenhouseController extends RectangularMultiblockControllerBase implements IGreenhouseControllerInternal, ILiquidTankTile {

	private final List<IInternalBlock> internalBlocks = new ArrayList<>();
	private final Set<IGreenhouseComponent.Listener> listenerComponents = new HashSet<>();
	private final Set<IGreenhouseComponent.Climatiser> climatiserComponents = new HashSet<>();
	private final Set<IGreenhouseComponent.Active> activeComponents = new HashSet<>();
	private final List<IGreenhouseLogic> logics = new ArrayList<>();
	
	private float tempChange;
	private float humidChange;
	private BiomeGenBase cachedBiome;
	private final TankManager tankManager;
	private final StandardTank resourceTank;
	private final EnergyManager energyManager;
	private final InventoryGreenhouse inventory;
	
	//Camouflage blocks
	private ItemStack camouflagePlainBlock;
	private ItemStack camouflageGlassBlock;
	private ItemStack camouflageDoorBlock;
	
	public GreenhouseController(World world) {
		super(world, GreenhouseMultiblockSizeLimits.instance);
		this.tempChange = 0;
		this.humidChange = 0;
		
		this.resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, FluidRegistry.WATER);
		this.tankManager = new TankManager(this, resourceTank);
		this.energyManager = new EnergyManager(2000, 100000);
		this.inventory = new InventoryGreenhouse(this);
		
		camouflagePlainBlock = getDefaultCamouflageBlock(EnumCamouflageType.DEFAULT);
		camouflageGlassBlock = getDefaultCamouflageBlock(EnumCamouflageType.GLASS);
		camouflageDoorBlock = getDefaultCamouflageBlock(EnumCamouflageType.DOOR);
	}
	
	/* CLIMATE */
	@Override
	public EnumTemperature getTemperature() {
		BlockPos coords = getReferenceCoord();
		return EnumTemperature.getFromValue(getExactTemperature());
	}
	
	private BiomeGenBase getBiome() {
		if (cachedBiome == null) {
			BlockPos coords = getReferenceCoord();
			cachedBiome = worldObj.getBiomeGenForCoords(coords);
		}
		return cachedBiome;
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}
	
	@Override
	public float getExactTemperature() {
		BlockPos coords = getReferenceCoord();
		return getBiome().getFloatTemperature(coords) + tempChange;
	}

	@Override
	public float getExactHumidity() {
		return getBiome().rainfall + tempChange;
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
	
	/* LOCATION */
	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public String getUnlocalizedType() {
		return "for.multiblock.greenhouse.type";
	}

	@Override
	public BlockPos getCoordinates() {
		BlockPos coord = getReferenceCoord();
		return new BlockPos(coord);
	}
	
	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		
		data.setFloat("Temperature", tempChange);
		data.setFloat("Humidity", humidChange);
		tankManager.writeToNBT(data);
		energyManager.writeToNBT(data);
		inventory.writeToNBT(data);
		
		CamouflageUtil.writeCamouflageBlockToNBT(data, this, EnumCamouflageType.DEFAULT);
		CamouflageUtil.writeCamouflageBlockToNBT(data, this, EnumCamouflageType.GLASS);
		CamouflageUtil.writeCamouflageBlockToNBT(data, this, EnumCamouflageType.DOOR);
		
		for (IGreenhouseLogic logic : getLogics()) {
			NBTTagCompound nbtTag = new NBTTagCompound();
			logic.writeToNBT(nbtTag);
			data.setTag("logic" + logic.getName(), nbtTag);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		
		tempChange = data.getFloat("Temperature");
		humidChange = data.getFloat("Humidity");
		tankManager.readFromNBT(data);
		energyManager.readFromNBT(data);
		inventory.readFromNBT(data);
		
		CamouflageUtil.readCamouflageBlockFromNBT(data, this, EnumCamouflageType.DEFAULT);
		CamouflageUtil.readCamouflageBlockFromNBT(data, this, EnumCamouflageType.GLASS);
		CamouflageUtil.readCamouflageBlockFromNBT(data, this, EnumCamouflageType.DOOR);
		
		if (logics.isEmpty()) {
			createLogics();
		}
		
		for (IGreenhouseLogic logic : getLogics()) {
			logic.readFromNBT(data.getCompoundTag("logic" + logic.getName()));
		}
	}
	
	@Override
	public void formatDescriptionPacket(NBTTagCompound data) {
		writeToNBT(data);
	}

	@Override
	public void decodeDescriptionPacket(NBTTagCompound data) {
		readFromNBT(data);
	}
	
	@Override
	protected void onAttachedPartWithMultiblockData(IMultiblockComponent part, NBTTagCompound data) {
		readFromNBT(data);
	}

	@Override
	public void onSwitchAccess(EnumAccess oldAccess, EnumAccess newAccess) {
		if (oldAccess == EnumAccess.SHARED || newAccess == EnumAccess.SHARED) {
			// pipes connected to this need to update
			for (IMultiblockComponent part : connectedParts) {
				if (part instanceof TileEntity) {
					TileEntity tile = (TileEntity) part;
					worldObj.notifyBlockOfStateChange(tile.getPos(), tile.getBlockType());
				}
			}
			markDirty();
		}
	}
	
	/* GUI DATA */
	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(Math.round(tempChange * 100));
		data.writeVarInt(Math.round(humidChange * 100));
		tankManager.writeData(data);
		energyManager.writeData(data);
		inventory.writeData(data);
		CamouflageUtil.writeCamouflageBlockToData(data, this, EnumCamouflageType.DEFAULT);
		CamouflageUtil.writeCamouflageBlockToData(data, this, EnumCamouflageType.GLASS);
		CamouflageUtil.writeCamouflageBlockToData(data, this, EnumCamouflageType.DOOR);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		tempChange = data.readVarInt() / 100.0F;
		humidChange = data.readVarInt() / 100.0F;
		tankManager.readData(data);
		energyManager.readData(data);
		inventory.readData(data);
		CamouflageUtil.readCamouflageBlockFromData(data, this);
		CamouflageUtil.readCamouflageBlockFromData(data, this);
		CamouflageUtil.readCamouflageBlockFromData(data, this);
	}
	
	/* CAMOUFLAGE */
	@Override
	public void setCamouflageBlock(EnumCamouflageType type, ItemStack camouflageBlock) {
		switch (type) {
			case DEFAULT:
				camouflagePlainBlock = camouflageBlock;
				break;
			case GLASS:
				camouflageGlassBlock = camouflageBlock;
				break;
			case DOOR:
				camouflageDoorBlock = camouflageBlock;
				break;
			default:
				return;
		}
		
		if (worldObj != null) {
			if (worldObj.isRemote) {
				for (IMultiblockComponent comp : connectedParts) {
					if (comp instanceof ICamouflagedBlock) {
						ICamouflagedBlock camBlock = (ICamouflagedBlock) comp;
						if (camBlock.getCamouflageType() == type) {
							worldObj.markBlockForUpdate(camBlock.getCoordinates());
						}
					}
				}
				Proxies.net.sendToServer(new PacketCamouflageUpdate(this, type, true));
			}
		}
		
		MinecraftForge.EVENT_BUS.post(new CamouflageChangeEvent(createState(), null, this, type));
	}
	
	@Override
	public ItemStack getCamouflageBlock(EnumCamouflageType type) {
		switch (type) {
			case DEFAULT:
				return camouflagePlainBlock;
			case GLASS:
				return camouflageGlassBlock;
			case DOOR:
				return camouflageDoorBlock;
			default:
				return null;
		}
	}
	
	@Override
	public ItemStack getDefaultCamouflageBlock(EnumCamouflageType type) {
		switch (type) {
			case DEFAULT:
				return new ItemStack(Blocks.brick_block);
			case GLASS:
				return new ItemStack(Blocks.stained_glass, 1, 13);
			case DOOR:
				return null;
			default:
				return null;
		}
	}
	
	/* GREENHOUSE */
	@Nonnull
	@Override
	public IGreenhouseState createState() {
		return new GreenhouseState(this);
	}
	
	@Override
	public void onChange(EnumGreenhouseEventType type, Object event) {
		for (IGreenhouseLogic logic : logics) {
			logic.onEvent(type, event);
		}
	}
	
	/* GREENHOUSE LOGICS */
	private void createLogics() {
		logics.clear();
		for (Class<? extends IGreenhouseLogic> logicClass : GreenhouseManager.greenhouseLogics) {
			IGreenhouseLogic logic = createLogic(logicClass);
			if (logic != null) {
				logics.add(logic);
			}
		}
	}
	
	private IGreenhouseLogic createLogic(Class<? extends IGreenhouseLogic> logicClass) {
		try {
			return logicClass.getConstructor(IGreenhouseController.class).newInstance(this);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Log.error("Fail to create a greenhouse logic with the class: {}", logicClass, e);
			return null;
		}
	}
	
	@Override
	public List<IGreenhouseLogic> getLogics() {
		return logics;
	}
	
	/* MANAGERS */
	@Nonnull
	@Override
	public TankManager getTankManager() {
		return tankManager;
	}
	
	@Override
	public EnergyManager getEnergyManager() {
		return energyManager;
	}
	
	@Nonnull
	@Override
	public IInventoryAdapter getInternalInventory() {
		if (isAssembled()) {
			return inventory;
		} else {
			return FakeInventoryAdapter.instance();
		}
	}
	
	/* CONTROLLER */
	@Override
	protected void onMachineAssembled() {
		super.onMachineAssembled();
		
		createLogics();
	}
	
	@Override
	protected void onMachineDisassembled() {
		super.onMachineDisassembled();
		
		internalBlocks.clear();
		logics.clear();
	}

	@Override
	protected void onBlockAdded(IMultiblockComponent newPart) {
		if (newPart instanceof IGreenhouseComponent.Listener) {
			listenerComponents.add((IGreenhouseComponent.Listener) newPart);
		} else if (newPart instanceof IGreenhouseComponent.Climatiser) {
			climatiserComponents.add((IGreenhouseComponent.Climatiser) newPart);
		} else if (newPart instanceof IGreenhouseComponent.Active) {
			activeComponents.add((IGreenhouseComponent.Active) newPart);
		}
	}

	@Override
	protected void onBlockRemoved(IMultiblockComponent oldPart) {
		if (oldPart instanceof IGreenhouseComponent.Listener) {
			listenerComponents.remove(oldPart);
		} else if (oldPart instanceof IGreenhouseComponent.Climatiser) {
			climatiserComponents.remove(oldPart);
		} else if (oldPart instanceof IGreenhouseComponent.Active) {
			activeComponents.remove(oldPart);
		}
	}

	@Override
	protected void onAssimilate(IMultiblockControllerInternal assimilated) {
	}

	@Override
	public void onAssimilated(IMultiblockControllerInternal assimilator) {
	}

	@Override
	protected boolean updateServer(int tickCount) {
		if (!isAssembled()) {
			boolean hasChangeClima = false;
			if (updateOnInterval(45)) {
				if (humidChange > 0F) {
					if (humidChange > 0.25F) {
						humidChange = -0.25F;
					} else {
						humidChange = 0F;
					}
					hasChangeClima = true;
				}
				if (tempChange > 0F) {
					if (tempChange > 0.25F) {
						tempChange = -0.25F;
					} else {
						tempChange = 0F;
					}
					hasChangeClima = true;
				}
			}
			return hasChangeClima;
		} else {
			if (updateOnInterval(20)) {
				inventory.drainCan(tankManager);
			}
			
			for (IGreenhouseLogic logic : logics) {
				logic.work();
			}
			
			for (IGreenhouseComponent.Active activeComponent : activeComponents) {
				activeComponent.updateServer(tickCount);
			}
			
			boolean canWork = true;
			for (IGreenhouseComponent.Listener listenerComponent : listenerComponents) {
				canWork = listenerComponent.getGreenhouseListener().canWork(this, canWork);
			}
			
			if (canWork) {

				for (IGreenhouseComponent.Climatiser climatiser : climatiserComponents) {
					climatiser.changeClimate(tickCount, this);
				}

				tempChange = equalizeChange(tempChange);
				humidChange = equalizeChange(humidChange);
			}

			return canWork;
		}
	}
	
	@Override
	protected void isMachineWhole() throws MultiblockValidationException {
		int minX = getSizeLimits().getMinimumXSize();
		int minY = getSizeLimits().getMinimumYSize();
		int minZ = getSizeLimits().getMinimumZSize();

		if (connectedParts.size() < getSizeLimits().getMinimumNumberOfBlocksForAssembledMachine()) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.small", minX, minY, minZ));
		}
		
		BlockPos maximumCoord = getMaximumCoord();
		BlockPos minimumCoord = getMinimumCoord();
		
		// Quickly check for exceeded dimensions
		int deltaX = maximumCoord.getX() - minimumCoord.getX() + 1;
		int deltaY = maximumCoord.getY() - minimumCoord.getY() + 1;
		int deltaZ = maximumCoord.getZ() - minimumCoord.getZ() + 1;
		
		int maxX = getSizeLimits().getMaximumXSize();
		int maxY = getSizeLimits().getMaximumYSize();
		int maxZ = getSizeLimits().getMaximumZSize();

		if (maxX > 0 && deltaX > maxX) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.large.x", maxX));
		}
		if (maxY > 0 && deltaY > maxY) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.large.y", maxY));
		}
		if (maxZ > 0 && deltaZ > maxZ) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.large.z", maxZ));
		}
		if (deltaX < minX) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.small.x", minX));
		}
		if (deltaY < minY) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.small.y", minY));
		}
		if (deltaZ < minZ) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.small.z", minZ));
		}

		// Now we run a simple check on each block within that volume.
		// Any block deviating = NO DEAL SIR
		TileEntity te;
		IMultiblockComponent part;
		boolean isNextRoof = false;
		Class<? extends RectangularMultiblockControllerBase> myClass = this.getClass();
		
		height:
		for (int y = minimumCoord.getY(); y <= maximumCoord.getY(); y++) {
			for (int x = minimumCoord.getX(); x <= maximumCoord.getX(); x++) {
				for (int z = minimumCoord.getZ(); z <= maximumCoord.getZ(); z++) {
					// Okay, figure out what sort of block this should be.
					BlockPos pos = new BlockPos(x, y, z);
					te = this.worldObj.getTileEntity(pos);
					if (te instanceof IMultiblockComponent) {
						part = (IMultiblockComponent) te;
					} else {
						// This is permitted so that we can incorporate certain non-multiblock parts inside interiors
						part = null;
					}
					
					// Validate block type against both part-level and material-level validators.
					int extremes = 0;
					int sides = 0;

					if (x == minimumCoord.getX()) {
						extremes++;
						sides++;
					}
					if (y == minimumCoord.getY()) {
						extremes++;
					}
					if (z == minimumCoord.getZ()) {
						extremes++;
						sides++;
					}
					
					if (x == maximumCoord.getX()) {
						extremes++;
						sides++;
					}
					if (z == maximumCoord.getZ()) {
						extremes++;
						sides++;
					}
					
					
					if (extremes >= 1) {
						// Side
						BlockPos posUp = pos.add(0, 1, 0);
						TileEntity tileUp = worldObj.getTileEntity(posUp);
						if (sides >= 1 && !(tileUp instanceof IGreenhouseComponent)) {
							int delta = y - minimumCoord.getY();
							if (y - minimumCoord.getY() >= minY) {
								isNextRoof = true;
							}
							break height;
						}
						
						int exteriorLevel = y - minimumCoord.getY();
						if (part != null) {
							// Ensure this part should actually be allowed within a cube of this controller's type
							if (!myClass.equals(part.getMultiblockLogic().getController().getClass())) {
								throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.invalid.part", Translator.translateToLocal(getUnlocalizedType())));
							}
							isGoodForExteriorLevel(part, exteriorLevel);
						} else {
							isBlockGoodForExteriorLevel(exteriorLevel, this.worldObj, pos);
						}
					} else {
						if (part != null) {
							IBlockState state = worldObj.getBlockState(part.getCoordinates());
							if (state.getBlock() instanceof BlockGreenhouse && ((BlockGreenhouse) state.getBlock()).getGreenhouseType() == BlockGreenhouseType.SPRINKLER || !myClass.equals(part.getMultiblockLogic().getController().getClass())) {
								isGoodForInterior(part);
							} else {
								throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.invalid.part", Translator.translateToLocal(getUnlocalizedType())));
							}
						} else {
							isBlockGoodForInterior(this.worldObj, pos);
						}
					}
				}
			}
		}
		internalBlocks.clear();
		
		if (isNextRoof) {
			checkInternalBlock(createInternalBlock(new InternalBlock(worldObj, getMinimumCoord().add(1, 1, 1))));
		}
		if (internalBlocks.isEmpty()) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.space.closed"));
		}
		this.internalBlocks.addAll(internalBlocks);
		
		int hatches = 0;
		for (IMultiblockComponent comp : connectedParts) {
			if (comp instanceof IGreenhouseComponent.ButterflyHatch) {
				hatches++;
			}
		}
		if (hatches > 1) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.butterflyhatch.toomany"));
		}
	}

	@Override
	protected void updateClient(int tickCount) {
	}
	
	private void checkInternalBlock(IInternalBlock blockToCheck) throws MultiblockValidationException {
		internalBlocks.add(blockToCheck);
		BlockPos posRoot = blockToCheck.getPos();
		
		isBlockGoodForInterior(worldObj, posRoot);
		for (IInternalBlockFace faceToCheck : blockToCheck.getFaces()) {
			CheckInternalBlockFaceEvent checkEvent = new CheckInternalBlockFaceEvent(createState(), blockToCheck, faceToCheck);
			MinecraftForge.EVENT_BUS.post(checkEvent);
			
			if (!faceToCheck.isTested()) {
				EnumFacing face = faceToCheck.getFace();
				BlockPos posFacing = posRoot.offset(face);
				
				BlockPos minPos = getMinimumCoord();
				BlockPos maxPos = getMaximumCoord();
				
				if (minPos.getX() > posFacing.getX() || minPos.getY() > posFacing.getY() || minPos.getZ() > posFacing.getZ() || maxPos.getX() < posFacing.getX() || maxPos.getY() < posFacing.getY() || maxPos.getZ() < posFacing.getZ()) {
					throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.space.closed"));
				}
				
				TileEntity tileFace = worldObj.getTileEntity(posFacing);
				
				if (tileFace instanceof IGreenhouseComponent) {
					if (((IGreenhouseComponent) tileFace).getMultiblockLogic().getController() != this) {
						throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.not.connected.part"));
					} else if (!(tileFace instanceof TileGreenhouseSprinkler)) {
						faceToCheck.setTested(true);
					}
				} else {
					IInternalBlock internalBlock = createInternalBlock(new InternalBlock(worldObj, posFacing, face.getOpposite(), blockToCheck));
					
					// Check is the internal block in the list
					if (internalBlocks.contains(internalBlock)) {
						faceToCheck.setTested(true);
					} else {
						checkInternalBlock(internalBlock);
					}
				}
			}
		}
	}
	
	private IInternalBlock createInternalBlock(IInternalBlock internalBlock) {
		CreateInternalBlockEvent createEvent = new CreateInternalBlockEvent(createState(), internalBlock);
		
		MinecraftForge.EVENT_BUS.post(createEvent);
		
		return createEvent.internalBlock;
		
	}
	
	@Override
	public List<IInternalBlock> getInternalBlocks() {
		return internalBlocks;
	}
	
	public static ButterflyHatch getGreenhouseButterflyHatch(World world, BlockPos pos) {
		if (GreenhouseManager.greenhouseHelper.getGreenhouseState(world, pos) == null) {
			return null;
		}
		IGreenhouseState state = GreenhouseManager.greenhouseHelper.getGreenhouseState(world, pos);
		for (IMultiblockComponent greenhouse : state.getGreenhouseComponents()) {
			if (greenhouse instanceof ButterflyHatch) {
				return (ButterflyHatch) greenhouse;
			}
		}
		return null;
	}

	@Override
	protected void isGoodForExteriorLevel(IMultiblockComponent part, int level) throws MultiblockValidationException {
		
	}

	@Override
	protected void isGoodForInterior(IMultiblockComponent part) throws MultiblockValidationException {
		
	}
	
	@Override
	protected void isBlockGoodForInterior(World world, BlockPos pos) throws MultiblockValidationException {
	}

}
