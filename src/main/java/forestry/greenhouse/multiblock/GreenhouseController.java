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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import forestry.api.climate.IClimateControl;
import forestry.api.climate.IClimatePosition;
import forestry.api.climate.IClimateRegion;
import forestry.api.climate.IClimateSourceProvider;
import forestry.api.core.CamouflageManager;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.ICamouflagedTile;
import forestry.api.greenhouse.EnumGreenhouseEventType;
import forestry.api.greenhouse.GreenhouseEvents.CamouflageChangeEvent;
import forestry.api.greenhouse.GreenhouseEvents.CheckInternalBlockFaceEvent;
import forestry.api.greenhouse.GreenhouseEvents.CreateInternalBlockEvent;
import forestry.api.greenhouse.GreenhouseManager;
import forestry.api.greenhouse.IGreenhouseLogic;
import forestry.api.greenhouse.IInternalBlock;
import forestry.api.greenhouse.IInternalBlockFace;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IGreenhouseComponent.ButterflyHatch;
import forestry.api.multiblock.IGreenhouseComponent.ClimateControl;
import forestry.api.multiblock.IGreenhouseComponent.Climatiser;
import forestry.api.multiblock.IGreenhouseController;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.climate.ClimatePosition;
import forestry.core.climate.ClimateRegion;
import forestry.core.config.Constants;
import forestry.core.fluids.FilteredTank;
import forestry.core.fluids.StandardTank;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.multiblock.MultiblockValidationException;
import forestry.core.multiblock.RectangularMultiblockControllerBase;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.packets.CamouflageSelectionType;
import forestry.core.network.packets.PacketCamouflageSelectServer;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.utils.CamouflageUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.Translator;
import forestry.energy.EnergyManager;
import forestry.greenhouse.inventory.InventoryGreenhouse;
import forestry.greenhouse.tiles.TileGreenhouseHumidifier;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;

public class GreenhouseController extends RectangularMultiblockControllerBase implements IGreenhouseControllerInternal, ILiquidTankTile {

	private final Set<IInternalBlock> internalBlocks = new HashSet<>();
	private final Set<IGreenhouseComponent.Listener> listenerComponents = new HashSet<>();
	private final Set<IGreenhouseComponent.Active> activeComponents = new HashSet<>();
	private final Set<IGreenhouseComponent.Climatiser> climatiserComponents = new HashSet<>();
	private final List<IGreenhouseLogic> logics = new ArrayList<>();
	@Nullable
	protected ClimateControl climateControl;
	@Nullable
	protected ButterflyHatch butterflyHatch;

	private final TankManager tankManager;
	private final EnergyManager energyManager;
	private final InventoryGreenhouse inventory;
	private final boolean needRenderUpdate = false;

	//Camouflage blocks
	private ItemStack camouflagePlainBlock;
	private ItemStack camouflageGlassBlock;
	private ItemStack camouflageDoorBlock;
	private ClimateRegion region;

	public GreenhouseController(World world) {
		super(world, GreenhouseMultiblockSizeLimits.instance);

		StandardTank resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY).setFilters(FluidRegistry.WATER);
		this.tankManager = new TankManager(this, resourceTank);
		this.energyManager = new EnergyManager(200, 100000);
		this.inventory = new InventoryGreenhouse(this);

		camouflagePlainBlock = getDefaultCamouflageBlock(CamouflageManager.DEFAULT);
		camouflageGlassBlock = getDefaultCamouflageBlock(CamouflageManager.GLASS);
		camouflageDoorBlock = getDefaultCamouflageBlock(CamouflageManager.DOOR);
		this.region = new ClimateRegion(this);
	}

	/* CLIMATE */
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
		return region.getAverageTemperature();
	}

	@Override
	public float getExactHumidity() {
		return region.getAverageHumidity();
	}

	@Override
	public String getUnlocalizedType() {
		return "for.multiblock.greenhouse.type";
	}

	@Override
	public BlockPos getCoordinates() {
		return getReferenceCoord();
	}

	/* SAVING & LOADING */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);

		tankManager.writeToNBT(data);
		energyManager.writeToNBT(data);
		inventory.writeToNBT(data);

		CamouflageUtil.writeCamouflageBlockToNBT(data, this, CamouflageManager.DEFAULT);
		CamouflageUtil.writeCamouflageBlockToNBT(data, this, CamouflageManager.GLASS);
		CamouflageUtil.writeCamouflageBlockToNBT(data, this, CamouflageManager.DOOR);

		for (IGreenhouseLogic logic : getLogics()) {
			NBTTagCompound nbtTag = new NBTTagCompound();
			logic.writeToNBT(nbtTag);
			data.setTag("logic" + logic.getName(), nbtTag);
		}

		data.setTag("Region", region.writeToNBT(new NBTTagCompound()));

		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		tankManager.readFromNBT(data);
		energyManager.readFromNBT(data);
		inventory.readFromNBT(data);

		CamouflageUtil.readCamouflageBlockFromNBT(data, this, CamouflageManager.DEFAULT);
		CamouflageUtil.readCamouflageBlockFromNBT(data, this, CamouflageManager.GLASS);
		CamouflageUtil.readCamouflageBlockFromNBT(data, this, CamouflageManager.DOOR);

		if (logics.isEmpty()) {
			createLogics();
		}

		for (IGreenhouseLogic logic : getLogics()) {
			logic.readFromNBT(data.getCompoundTag("logic" + logic.getName()));
		}

		if (data.hasKey("Region")) {
			NBTTagCompound nbtTag = data.getCompoundTag("Region");
			if (region != null) {
				region.readFromNBT(nbtTag);
			}
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

	/* GUI DATA */
	@Override
	public void writeGuiData(PacketBufferForestry data) {
		tankManager.writeData(data);
		energyManager.writeData(data);
		inventory.writeData(data);
		CamouflageUtil.writeCamouflageBlockToData(data, this, CamouflageManager.DEFAULT);
		CamouflageUtil.writeCamouflageBlockToData(data, this, CamouflageManager.GLASS);
		CamouflageUtil.writeCamouflageBlockToData(data, this, CamouflageManager.DOOR);
		region.writeData(data);
	}

	@Override
	public void readGuiData(PacketBufferForestry data) throws IOException {
		tankManager.readData(data);
		energyManager.readData(data);
		inventory.readData(data);
		CamouflageUtil.readCamouflageBlockFromData(data, this);
		CamouflageUtil.readCamouflageBlockFromData(data, this);
		CamouflageUtil.readCamouflageBlockFromData(data, this);
		region.readData(data);
	}

	@Override
	public boolean canHandleType(String type) {
		return type.equals(CamouflageManager.DEFAULT) || type.equals(CamouflageManager.GLASS) || type.equals(CamouflageManager.DOOR);
	}

	/* CAMOUFLAGE */
	@Override
	public boolean setCamouflageBlock(String type, ItemStack camouflageBlock, boolean sendClientUpdate) {
		ItemStack oldCamouflageBlock;
		switch (type) {
			case CamouflageManager.DEFAULT:
				oldCamouflageBlock = camouflagePlainBlock;
				break;
			case CamouflageManager.GLASS:
				oldCamouflageBlock = camouflageGlassBlock;
				break;
			case CamouflageManager.DOOR:
				oldCamouflageBlock = camouflageDoorBlock;
				break;
			default:
				return false;
		}

		if (!ItemStackUtil.isIdenticalItem(camouflageBlock, oldCamouflageBlock)) {
			switch (type) {
				case CamouflageManager.DEFAULT:
					camouflagePlainBlock = camouflageBlock;
					break;
				case CamouflageManager.GLASS:
					camouflageGlassBlock = camouflageBlock;
					break;
				case CamouflageManager.DOOR:
					camouflageDoorBlock = camouflageBlock;
					break;
				default:
					return false;
			}
			if (sendClientUpdate && world.isRemote) {
				Proxies.net.sendToServer(new PacketCamouflageSelectServer(this, type, CamouflageSelectionType.MULTIBLOCK));
			}
			MinecraftForge.EVENT_BUS.post(new CamouflageChangeEvent(this, null, this, type));
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getCamouflageBlock(String type) {
		switch (type) {
			case CamouflageManager.DEFAULT:
				return camouflagePlainBlock;
			case CamouflageManager.GLASS:
				return camouflageGlassBlock;
			case CamouflageManager.DOOR:
				return camouflageDoorBlock;
			default:
				return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack getDefaultCamouflageBlock(String type) {
		switch (type) {
			case CamouflageManager.DEFAULT:
				return new ItemStack(Blocks.BRICK_BLOCK);
			case CamouflageManager.GLASS:
				return new ItemStack(Blocks.STAINED_GLASS, 1, 13);
			case CamouflageManager.DOOR:
				return ItemStack.EMPTY;
			default:
				return ItemStack.EMPTY;
		}
	}

	/* GREENHOUSE */
	@Override
	public void onChange(EnumGreenhouseEventType type, Object event) {
		for (IGreenhouseLogic logic : logics) {
			logic.onEvent(type, event);
		}
	}

	/* GREENHOUSE LOGICS */
	private void createLogics() {
		logics.clear();
		for (Class<? extends IGreenhouseLogic> logicClass : GreenhouseManager.greenhouseHelper.getGreenhouseLogics()) {
			IGreenhouseLogic logic = createLogic(logicClass);
			if (logic != null) {
				logics.add(logic);
			}
		}
	}

	@Nullable
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

	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	@Override
	public EnergyManager getEnergyManager() {
		return energyManager;
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		if (isAssembled()) {
			return inventory;
		} else {
			return FakeInventoryAdapter.instance();
		}
	}
	
	private void addNewPositions(Collection<IClimatePosition> newPositions){
		for (IClimatePosition position : newPositions) {
			BlockPos pos = position.getPos();
			IClimatePosition oldPosition = region.getPosition(pos);
			float temperature = position.getTemperature();
			float humidity = position.getHumidity();
			if (oldPosition != null) {
				temperature = oldPosition.getTemperature();
				humidity = oldPosition.getHumidity();
			}
			region.setPosition(pos, temperature, humidity);
		}
		region.calculateAverageClimate();
	}

	/* CONTROLLER */
	@Override
	protected void onMachineAssembled() {
		super.onMachineAssembled();

		createLogics();
		Set<IClimatePosition> internalPositions = new HashSet<>();
		for (IInternalBlock block : internalBlocks) {
			if (block != null) {
				internalPositions.add(new ClimatePosition(region, block.getPos()));
			}
		}
		addNewPositions(internalPositions);
		ForestryAPI.climateManager.addRegion(region);

		for (IClimateSourceProvider source : climatiserComponents) {
			region.addSource(source.getClimateSource());
		}

		for (IMultiblockComponent comp : connectedParts) {
			if (comp instanceof ICamouflagedTile) {
				world.markBlockRangeForRenderUpdate(comp.getCoordinates(), comp.getCoordinates());
			}
		}
	}

	@Override
	protected void onMachineDisassembled() {
		super.onMachineDisassembled();

		internalBlocks.clear();
		logics.clear();
		
		ForestryAPI.climateManager.removeRegion(region);

		for (IClimateSourceProvider source : climatiserComponents) {
			region.removeSource(source.getClimateSource());
		}

		for (IMultiblockComponent comp : connectedParts) {
			if (comp instanceof ICamouflagedTile) {
				world.markBlockRangeForRenderUpdate(comp.getCoordinates(), comp.getCoordinates());
			}
		}
	}

	@Override
	protected void onBlockAdded(IMultiblockComponent newPart) {
		if (newPart instanceof IGreenhouseComponent.Listener) {
			listenerComponents.add((IGreenhouseComponent.Listener) newPart);
		} else if (newPart instanceof IGreenhouseComponent.Climatiser) {
			climatiserComponents.add((Climatiser) newPart);
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
			ForestryAPI.climateManager.removeSource((IClimateSourceProvider) oldPart);
		} else if (oldPart instanceof IGreenhouseComponent.Active) {
			activeComponents.remove(oldPart);
		}
	}

	@Override
	protected void onAssimilate(IMultiblockControllerInternal assimilated) {
		IGreenhouseControllerInternal internal = (IGreenhouseControllerInternal) assimilated;
		addNewPositions(internal.getRegion().getPositions());
	}

	@Override
	public void onAssimilated(IMultiblockControllerInternal assimilator) {
		if(isAssembled()){
			ForestryAPI.climateManager.removeRegion(region);
		}
	}

	@Override
	protected boolean updateServer(int tickCount) {
		if (updateOnInterval(20)) {
			inventory.drainCan(tankManager);
		}

		for (IGreenhouseLogic logic : logics) {
			logic.work();
		}

		for (IGreenhouseComponent.Active activeComponent : activeComponents) {
			activeComponent.updateServer(tickCount);
		}

		return false;
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

		for (int y = minimumCoord.getY(); y <= maximumCoord.getY() && !isNextRoof; y++) {
			for (int x = minimumCoord.getX(); x <= maximumCoord.getX(); x++) {
				for (int z = minimumCoord.getZ(); z <= maximumCoord.getZ(); z++) {
					// Okay, figure out what sort of block this should be.
					BlockPos pos = new BlockPos(x, y, z);
					te = this.world.getTileEntity(pos);
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

						int exteriorLevel = y - minimumCoord.getY();
						if (part != null) {
							// Ensure this part should actually be allowed within a cube of this controller's type
							if (!myClass.equals(part.getMultiblockLogic().getController().getClass())) {
								throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.invalid.part", Translator.translateToLocal(getUnlocalizedType())));
							}
							isGoodForExteriorLevel(part, exteriorLevel);
						} else {
							isBlockGoodForExteriorLevel(exteriorLevel, this.world, pos);
						}
					} else {
						if (part != null) {
							IBlockState state = world.getBlockState(part.getCoordinates());
							if (!myClass.equals(part.getMultiblockLogic().getController().getClass())) {
								isGoodForInterior(part);
							} else {
								throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.invalid.part", Translator.translateToLocal(getUnlocalizedType())));
							}
						} else {
							isBlockGoodForInterior(this.world, pos);
						}
					}

					BlockPos posUp = pos.up(2);
					TileEntity tileUp = world.getTileEntity(posUp);
					if (sides >= 1 && !(tileUp instanceof IGreenhouseComponent)) {
						int delta = y - minimumCoord.getY();
						if (delta + 2 >= minY) {
							isNextRoof = true;
						}
					}
				}
			}
		}
		internalBlocks.clear();

		if (isNextRoof) {
			checkInternalBlocks();
		}

		if (internalBlocks.isEmpty()) {
			throw new MultiblockValidationException(Translator.translateToLocal("for.multiblock.greenhouse.error.space.closed"));
		}

		Set<ButterflyHatch> hatches = new HashSet<>();
		Set<ClimateControl> controls = new HashSet<>();
		for (IMultiblockComponent comp : connectedParts) {
			if (comp instanceof ButterflyHatch) {
				hatches.add((ButterflyHatch) comp);
			} else if (comp instanceof ClimateControl) {
				controls.add((ClimateControl) comp);
			}
		}
		if (hatches.size() > 1) {
			throw new MultiblockValidationException(Translator.translateToLocal("for.multiblock.greenhouse.error.butterflyhatch.toomany"));
		}
		if (controls.size() > 1) {
			throw new MultiblockValidationException(Translator.translateToLocal("for.multiblock.greenhouse.error.climatecontrol.toomany"));
		}
		if (hatches.iterator().hasNext()) {
			butterflyHatch = hatches.iterator().next();
		}
		if (controls.iterator().hasNext()) {
			climateControl = controls.iterator().next();
		}
	}

	@Override
	protected void updateClient(int tickCount) {
	}

	/**
	 * Check all internal blocks.
	 */
	private void checkInternalBlocks() throws MultiblockValidationException {
		Stack<IInternalBlock> blocksToCheck = new Stack<>();
		IInternalBlock internalBlock = createInternalBlock(new InternalBlock(getMinimumCoord().add(1, 1, 1)));
		blocksToCheck.add(internalBlock);
		while (!blocksToCheck.isEmpty()) {
			IInternalBlock blockToCheck = blocksToCheck.pop();
			List<IInternalBlock> newBlocksToCheck = checkInternalBlock(blockToCheck);
			blocksToCheck.addAll(newBlocksToCheck);
		}
	}

	/**
	 * Returns a list of more internal blocks to check.
	 */
	private List<IInternalBlock> checkInternalBlock(IInternalBlock blockToCheck) throws MultiblockValidationException {
		List<IInternalBlock> newBlocksToCheck = new ArrayList<>();

		internalBlocks.add(blockToCheck);
		BlockPos posRoot = blockToCheck.getPos();

		isBlockGoodForInterior(world, posRoot);
		for (IInternalBlockFace faceToCheck : blockToCheck.getFaces()) {
			CheckInternalBlockFaceEvent checkEvent = new CheckInternalBlockFaceEvent(this, blockToCheck, faceToCheck);
			MinecraftForge.EVENT_BUS.post(checkEvent);

			if (!faceToCheck.isTested()) {
				EnumFacing face = faceToCheck.getFace();
				BlockPos posFacing = posRoot.offset(face);

				BlockPos minPos = getMinimumCoord();
				BlockPos maxPos = getMaximumCoord();

				if (minPos.getX() > posFacing.getX() || minPos.getY() > posFacing.getY() || minPos.getZ() > posFacing.getZ() || maxPos.getX() < posFacing.getX() || maxPos.getY() < posFacing.getY() || maxPos.getZ() < posFacing.getZ()) {
					throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.greenhouse.error.space.closed"));
				}

				TileEntity tileFace = world.getTileEntity(posFacing);

				if (tileFace instanceof IGreenhouseComponent) {
					if (((IGreenhouseComponent) tileFace).getMultiblockLogic().getController() != this) {
						throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.not.connected.part"));
					} else if (!(tileFace instanceof TileGreenhouseHumidifier)) {
						faceToCheck.setTested(true);
					}
				} else {
					IInternalBlock internalBlock = createInternalBlock(new InternalBlock(posFacing, face.getOpposite(), blockToCheck));

					// Check is the internal block in the list
					if (internalBlocks.contains(internalBlock)) {
						faceToCheck.setTested(true);
					} else {
						newBlocksToCheck.add(internalBlock);
					}
				}
			}
		}
		return newBlocksToCheck;
	}

	private IInternalBlock createInternalBlock(IInternalBlock internalBlock) {
		CreateInternalBlockEvent createEvent = new CreateInternalBlockEvent(this, internalBlock);

		MinecraftForge.EVENT_BUS.post(createEvent);

		return createEvent.internalBlock;

	}

	@Override
	public Set<IInternalBlock> getInternalBlocks() {
		return internalBlocks;
	}

	@Override
	public IClimateControl getClimateControl() {
		if (climateControl == null) {
			return DefaultClimateControl.instance;
		}
		return climateControl.getClimateControl();
	}

	@Override
	@Nullable
	public ButterflyHatch getButterflyHatch() {
		return butterflyHatch;
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

	@Override
	public IClimateRegion getRegion() {
		return region;
	}

	@Override
	public Set<IGreenhouseComponent.Listener> getListenerComponents() {
		return listenerComponents;
	}

	@Override
	public boolean canWork() {
		boolean canWork = true;
		for (IGreenhouseComponent.Listener listenerComponent : listenerComponents) {
			canWork = listenerComponent.getGreenhouseListener().canWork(this, canWork);
		}
		return canWork;
	}

}
