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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
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

import forestry.api.core.EnumCamouflageType;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.ICamouflagedTile;
import forestry.api.core.climate.IClimatePosition;
import forestry.api.core.climate.IClimateRegion;
import forestry.api.core.climate.IClimateSource;
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
import forestry.api.multiblock.IGreenhouseController;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.access.EnumAccess;
import forestry.core.climate.ClimateRoom;
import forestry.core.climate.ClimatePosition;
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
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.Translator;
import forestry.energy.EnergyManager;
import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.inventory.InventoryGreenhouse;
import forestry.greenhouse.network.packets.PacketCamouflageUpdate;
import forestry.greenhouse.tiles.TileGreenhouseSprinkler;

public class GreenhouseController extends RectangularMultiblockControllerBase implements IGreenhouseControllerInternal, ILiquidTankTile {

	private final Set<IInternalBlock> internalBlocks = new HashSet<>();
	private final Set<IGreenhouseComponent.Listener> listenerComponents = new HashSet<>();
	private final Set<IGreenhouseComponent.Active> activeComponents = new HashSet<>();
	private final List<IGreenhouseLogic> logics = new ArrayList<>();
	
	private final TankManager tankManager;
	private final StandardTank resourceTank;
	private final EnergyManager energyManager;
	private final InventoryGreenhouse inventory;
	
	//Camouflage blocks
	private ItemStack camouflagePlainBlock;
	private ItemStack camouflageGlassBlock;
	private ItemStack camouflageDoorBlock;
	private ClimateRoom region;
	
	public GreenhouseController(World world) {
		super(world, GreenhouseMultiblockSizeLimits.instance);
		
		this.resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY).setFilters(FluidRegistry.WATER);
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

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}
	
	@Override
	public float getExactTemperature() {
		int dimensionID = worldObj.provider.getDimension();
		
		float temperature = 0.0F;
		
		for(IInternalBlock internalBlock : internalBlocks){
			IClimatePosition position = region.getPositions().get(internalBlock.getPos());
			if(position != null){
				temperature+=position.getTemperature();
			}
		}
		return temperature / internalBlocks.size();
	}

	@Override
	public float getExactHumidity() {
		int dimensionID = worldObj.provider.getDimension();
		float humidity = 0.0F;
		
		for(IInternalBlock internalBlock : internalBlocks){
			IClimatePosition position = region.getPositions().get(internalBlock.getPos());
			if(position != null){
				humidity+=position.getHumidity();
			}
		}
		return humidity / internalBlocks.size();
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
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);
		
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
		
		if(region != null){
			data.setTag("Region", region.writeToNBT(new NBTTagCompound()));
		}

		return data;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		
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
		
		if(data.hasKey("Region")){
			NBTTagCompound nbtTag = data.getCompoundTag("Region");
			region = new ClimateRoom(this, nbtTag);
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
		tankManager.writeData(data);
		energyManager.writeData(data);
		inventory.writeData(data);
		CamouflageUtil.writeCamouflageBlockToData(data, this, EnumCamouflageType.DEFAULT);
		CamouflageUtil.writeCamouflageBlockToData(data, this, EnumCamouflageType.GLASS);
		CamouflageUtil.writeCamouflageBlockToData(data, this, EnumCamouflageType.DOOR);
		if(region != null){
			data.writeBoolean(true);
			region.writeData(data);
		}else{
			data.writeBoolean(false);
		}
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		tankManager.readData(data);
		energyManager.readData(data);
		inventory.readData(data);
		CamouflageUtil.readCamouflageBlockFromData(data, this);
		CamouflageUtil.readCamouflageBlockFromData(data, this);
		CamouflageUtil.readCamouflageBlockFromData(data, this);
		if(data.readBoolean()){
			region.readData(data);
		}
	}
	
	/* CAMOUFLAGE */
	@Override
	public void setCamouflageBlock(EnumCamouflageType type, ItemStack camouflageBlock) {
		ItemStack oldCamouflageBlock;
		switch (type) {
		case DEFAULT:
			oldCamouflageBlock = camouflagePlainBlock;
			break;
		case GLASS:
			oldCamouflageBlock = camouflageGlassBlock;
			break;
		case DOOR:
			oldCamouflageBlock = camouflageDoorBlock;
			break;
		default:
			return;
		}
		
		if(!ItemStackUtil.isIdenticalItem(camouflageBlock, oldCamouflageBlock)){
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
						if (comp instanceof ICamouflagedTile) {
							ICamouflagedTile camBlock = (ICamouflagedTile) comp;
							if (camBlock.getCamouflageType() == type) {
								worldObj.markBlockRangeForRenderUpdate(camBlock.getCoordinates(), camBlock.getCoordinates());
							}
						}
					}
					Proxies.net.sendToServer(new PacketCamouflageUpdate(this, type, true));
				}
			}
			
			MinecraftForge.EVENT_BUS.post(new CamouflageChangeEvent(this, null, this, type));
		}
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
				return new ItemStack(Blocks.BRICK_BLOCK);
			case GLASS:
				return new ItemStack(Blocks.STAINED_GLASS, 1, 13);
			case DOOR:
				return null;
			default:
				return null;
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
		if(region != null){
			Map<BlockPos, IClimatePosition> internalPositions = new HashMap<>();
			List<BlockPos> wallPositions = new ArrayList<>();
			for(IMultiblockComponent comp : connectedParts){
				if(comp != null){
					wallPositions.add(comp.getCoordinates());
				}
			}
			for(IInternalBlock block : internalBlocks){
				if(block != null){
					internalPositions.put(block.getPos(), new ClimatePosition(region, block.getPos()));
				}
			}
			ForestryAPI.climateManager.removeRegion(region);
			region = new ClimateRoom(region, internalPositions, wallPositions);
			ForestryAPI.climateManager.addRegion(region);
		}else{
			Map<BlockPos, IClimatePosition> internalPositions = new HashMap<>();
			List<BlockPos> wallPositions = new ArrayList<>();
			for(IMultiblockComponent comp : connectedParts){
				if(comp != null){
					wallPositions.add(comp.getCoordinates());
				}
			}
			region = new ClimateRoom(this, internalPositions, wallPositions);
			for(IInternalBlock block : internalBlocks){
				if(block != null){
					internalPositions.put(block.getPos(), new ClimatePosition(region, block.getPos()));
				}
			}
			ForestryAPI.climateManager.addRegion(region);
		}
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
			ForestryAPI.climateManager.addSource((IClimateSource) newPart);
		} else if (newPart instanceof IGreenhouseComponent.Active) {
			activeComponents.add((IGreenhouseComponent.Active) newPart);
		}
	}

	@Override
	protected void onBlockRemoved(IMultiblockComponent oldPart) {
		if (oldPart instanceof IGreenhouseComponent.Listener) {
			listenerComponents.remove(oldPart);
		} else if (oldPart instanceof IGreenhouseComponent.Climatiser) {
			ForestryAPI.climateManager.removeSource((IClimateSource) oldPart);
		} else if (oldPart instanceof IGreenhouseComponent.Active) {
			activeComponents.remove(oldPart);
		}
	}

	@Override
	protected void onAssimilate(IMultiblockControllerInternal assimilated) {
		if(assimilated != null && ((IGreenhouseControllerInternal)assimilated).getRegion() != null){
			IGreenhouseControllerInternal internal = (IGreenhouseControllerInternal) assimilated;
			if(region != null){
				ForestryAPI.climateManager.removeRegion(region);
				List<BlockPos> wallPositions = new ArrayList<>();
				for(IMultiblockComponent comp : connectedParts){
					if(comp != null){
						wallPositions.add(comp.getCoordinates());
					}
				}
				region = new ClimateRoom(region,internal.getRegion().getPositions(), wallPositions);
				ForestryAPI.climateManager.addRegion(region);
			}
		}
	}

	@Override
	public void onAssimilated(IMultiblockControllerInternal assimilator) {
		if(region != null){
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

		if (connectedParts.size() < getSizeLimits().getMinimumNumberOfBlocksForAssembledMachine()) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.greenhouse.error.space.closed", minX, minY, minZ));
		}
		internalBlocks.clear();
		
		Stack<IInternalBlock> blocksToCheck = new Stack<>();
		IInternalBlock internalBlock = createInternalBlock(new InternalBlock(getMinimumCoord().add(1, 1, 1)));
		blocksToCheck.add(internalBlock);
		while (!blocksToCheck.isEmpty()) {
			IInternalBlock blockToCheck = blocksToCheck.pop();
			List<IInternalBlock> newBlocksToCheck = checkInternalBlock(blockToCheck);
			blocksToCheck.addAll(newBlocksToCheck);
		}

		if (internalBlocks.isEmpty()) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.greenhouse.error.space.closed"));
		}

		int hatches = 0;
		for (IMultiblockComponent comp : connectedParts) {
			if (comp instanceof IGreenhouseComponent.ButterflyHatch) {
				hatches++;
			}
		}
		if (hatches > 1) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.greenhouse.error.butterflyhatch.toomany"));
		}
	}

	@Override
	protected void updateClient(int tickCount) {
	}

	/**
	 * Returns a list of more internal blocks to check.
	 * @throws MultiblockValidationException
	 */
	private List<IInternalBlock> checkInternalBlock(IInternalBlock blockToCheck) throws MultiblockValidationException {
		List<IInternalBlock> newBlocksToCheck = new ArrayList<>();

		internalBlocks.add(blockToCheck);
		BlockPos posRoot = blockToCheck.getPos();
		
		isBlockGoodForInterior(worldObj, posRoot);
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
				
				TileEntity tileFace = worldObj.getTileEntity(posFacing);
				
				if (tileFace instanceof IGreenhouseComponent) {
					if (((IGreenhouseComponent) tileFace).getMultiblockLogic().getController() != this) {
						throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.not.connected.part"));
					} else if (!(tileFace instanceof TileGreenhouseSprinkler)) {
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
	
	public static ButterflyHatch getGreenhouseButterflyHatch(World world, BlockPos pos) {
		if (GreenhouseManager.greenhouseHelper.getGreenhouseController(world, pos) == null) {
			return null;
		}
		IGreenhouseController controller = GreenhouseManager.greenhouseHelper.getGreenhouseController(world, pos);
		for (IMultiblockComponent greenhouse : controller.getComponents()) {
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

	@Override
	public IClimateRegion getRegion() {
		return region;
	}
	
	@Override
	public void clearRegion() {
		region = null;
	}
	
	@Override
	public Set<IGreenhouseComponent.Listener> getListenerComponents() {
		return listenerComponents;
	}

}
