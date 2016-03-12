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

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IClimateControlled;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.access.EnumAccess;
import forestry.core.config.Constants;
import forestry.core.fluids.ITankManager;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.multiblock.MultiblockValidationException;
import forestry.core.multiblock.RectangularMultiblockControllerBase;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.ICamouflagedBlock;
import forestry.core.tiles.ILiquidTankTile;
import forestry.energy.EnergyManager;
import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.network.packets.PacketCamouflageUpdate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fluids.FluidRegistry;

public class GreenhouseController extends RectangularMultiblockControllerBase implements IGreenhouseControllerInternal, IClimateControlled, ILiquidTankTile, ICamouflagedBlock {

	private final List<InternalBlock> internalBlocks = Lists.newArrayList();
	private final Set<IGreenhouseComponent.Listener> listenerComponents = new HashSet<>();
	private final Set<IGreenhouseComponent.Climatiser> climatiserComponents = new HashSet<>();
	private final Set<IGreenhouseComponent.Active> activeComponents = new HashSet<>();
	
	private float tempChange;
	private float humidChange;
	private BiomeGenBase cachedBiome;
	private final TankManager tankManager;
	private final StandardTank resourceTank;
	private final EnergyManager energyManager;
	private ItemStack camouflageBlock;
	
	public GreenhouseController(World world) {
		super(world, GreenhouseMultiblockSizeLimits.instance);
		tempChange = 0;
		humidChange = 0;
		
		this.resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, FluidRegistry.WATER);
		this.tankManager = new TankManager(this, resourceTank);
		this.energyManager = new EnergyManager(2000, 100000);
	}

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
	
	@Override
	public World getWorld() {
		return worldObj;
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
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		
		data.setFloat("Temperature", tempChange);
		data.setFloat("Humidity", humidChange);
		tankManager.writeToNBT(data);
		energyManager.writeToNBT(data);
		
		if(camouflageBlock != null){
			NBTTagCompound nbtTag = new NBTTagCompound();
			camouflageBlock.writeToNBT(nbtTag);
			data.setTag("CamouflageBlock", nbtTag);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		
		tempChange = data.getFloat("Temperature");
		humidChange = data.getFloat("Humidity");
		tankManager.readFromNBT(data);
		energyManager.readFromNBT(data);
		
		if(data.hasKey("CamouflageBlock")){
			setCamouflageBlock(ItemStack.loadItemStackFromNBT(data.getCompoundTag("CamouflageBlock")));
		}
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

	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(Math.round(tempChange * 100));
		data.writeVarInt(Math.round(humidChange * 100));
		tankManager.writeData(data);
		energyManager.writeData(data);
		ItemStack camouflageBlock = getCamouflageBlock();
		if(camouflageBlock != null){
			data.writeShort(1);
			data.writeItemStack(camouflageBlock);
		}else{
			data.writeShort(0);
		}
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		tempChange = data.readVarInt() / 100.0F;
		humidChange = data.readVarInt() / 100.0F;
		tankManager.readData(data);
		energyManager.readData(data);
		if(data.readShort() == 1){
			setCamouflageBlock(data.readItemStack());
		}
	}
	
	@Override
	public boolean setCamouflageBlock(ItemStack camouflageBlock) {
		this.camouflageBlock = camouflageBlock;
		
		if (worldObj != null && worldObj.isRemote) {
			for(IMultiblockComponent comp : connectedParts){
				ICamouflagedBlock camBlock = (ICamouflagedBlock) comp;
				worldObj.markBlockForUpdate(camBlock.getCoordinates());
				Proxies.net.sendToServer(new PacketCamouflageUpdate(this, true));
			}
		}
		return true;
	}
	
	@Override
	public ItemStack getCamouflageBlock() {
		return camouflageBlock;
	}

	@Override
	public BlockPos getCoordinates() {
		BlockPos coord = getReferenceCoord();
		return new BlockPos(coord);
	}
	
	@Override
	public ITankManager getTankManager() {
		return tankManager;
	}
	
	@Override
	public EnergyManager getEnergyManager() {
		return energyManager;
	}

	@Override
	protected void onAttachedPartWithMultiblockData(IMultiblockComponent part, NBTTagCompound data) {
		readFromNBT(data);
	}
	
	@Override
	protected void onMachineDisassembled() {
		super.onMachineDisassembled();
		
		internalBlocks.clear();
	}

	@Override
	protected void onBlockAdded(IMultiblockComponent newPart) {
		if(newPart instanceof IGreenhouseComponent.Listener){
			listenerComponents.add((IGreenhouseComponent.Listener) newPart);
		}else if(newPart instanceof IGreenhouseComponent.Climatiser){
			climatiserComponents.add((IGreenhouseComponent.Climatiser) newPart);
		}else if(newPart instanceof IGreenhouseComponent.Active){
			activeComponents.add((IGreenhouseComponent.Active) newPart);
		}
	}

	@Override
	protected void onBlockRemoved(IMultiblockComponent oldPart) {
		if(oldPart instanceof IGreenhouseComponent.Listener){
			listenerComponents.remove(oldPart);
		}else if(oldPart instanceof IGreenhouseComponent.Climatiser){
			climatiserComponents.remove(oldPart);
		}else if(oldPart instanceof IGreenhouseComponent.Active){
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
		for (IGreenhouseComponent.Active activeComponent : activeComponents) {
			activeComponent.updateServer(tickCount);
		}
		
		boolean canWork = true;
		
		for (IGreenhouseComponent.Listener listenerComponent : listenerComponents) {
			canWork = listenerComponent.getGreenhouseListener().canWork(this);
		}
		
		if(canWork){

			for (IGreenhouseComponent.Climatiser climatiser : climatiserComponents) {
				climatiser.changeClimate(tickCount, this);
			}
	
			tempChange = equalizeChange(tempChange);
			humidChange = equalizeChange(humidChange);
		}

		return canWork;
	}

	@Override
	protected void updateClient(int tickCount) {
	}

	@Override
	protected void isGoodForExteriorLevel(IMultiblockComponent part, int level) throws MultiblockValidationException {
		
	}

	@Override
	protected void isGoodForInterior(IMultiblockComponent part) throws MultiblockValidationException {
		
	}
	
	@Override
	protected void isMachineWhole() throws MultiblockValidationException {
		int minX = getSizeLimits().getMinimumXSize();
		int minY = getSizeLimits().getMinimumYSize();
		int minZ = getSizeLimits().getMinimumZSize();

		if (connectedParts.size() < getSizeLimits().getMinimumNumberOfBlocksForAssembledMachine()) {
			throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.small", minX, minY, minZ));
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
			throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.large.x", maxX));
		}
		if (maxY > 0 && deltaY > maxY) {
			throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.large.y", maxY));
		}
		if (maxZ > 0 && deltaZ > maxZ) {
			throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.large.z", maxZ));
		}
		if (deltaX < minX) {
			throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.small.x", minX));
		}
		if (deltaY < minY) {
			throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.small.y", minY));
		}
		if (deltaZ < minZ) {
			throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.small.z", minZ));
		}

		// Now we run a simple check on each block within that volume.
		// Any block deviating = NO DEAL SIR
		TileEntity te;
		IMultiblockComponent part;
		boolean isNextRoof = false;
		Class<? extends RectangularMultiblockControllerBase> myClass = this.getClass();
		
		List<InternalBlock> internalBlocks = Lists.newArrayList();
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

					if (x == minimumCoord.getX()) {
						extremes++;
					}
					if (z == minimumCoord.getZ()) {
						extremes++;
					}
					
					if (x == maximumCoord.getX()) {
						extremes++;
					}
					if (z == maximumCoord.getZ()) {
						extremes++;
					}
					
					
					if (extremes >= 1) {
						// Side
						BlockPos posUp = pos.add(0, 1, 0);
						IBlockState state = worldObj.getBlockState(posUp);
						if(!(state.getBlock() instanceof BlockGreenhouse)){
							isNextRoof = true;
							break;
						}
						
						int exteriorLevel = y - minimumCoord.getY();
						if (part != null) {
							// Ensure this part should actually be allowed within a cube of this controller's type
							if (!myClass.equals(part.getMultiblockLogic().getController().getClass())) {
								throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.invalid.part", x, y, z, myClass.getSimpleName()));
							}
							isGoodForExteriorLevel(part, exteriorLevel);
						} else {
							isBlockGoodForExteriorLevel(exteriorLevel, this.worldObj, pos);
						}
					} else {
						if (part != null) {
							if (!myClass.equals(part.getMultiblockLogic().getController().getClass())) {
								throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.invalid.part", x, y, z, myClass.getSimpleName()));
							}
							isGoodForInterior(part);
						} else {
							isBlockGoodForInterior(this.worldObj, pos);
						}
					}
				}
			}
			if(isNextRoof){
				addInternalBlock(new InternalBlock(getMinimumCoord().add(1, 1, 1)), internalBlocks);
			}else{
				throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.space.closed"));
			}
		}
		this.internalBlocks.addAll(internalBlocks);
	}
	
	private InternalBlock addInternalBlock(InternalBlock internalBlock, List<InternalBlock> internalBlocks) throws MultiblockValidationException{
		isBlockGoodForInterior(worldObj, internalBlock.pos);
		for(Map.Entry<EnumFacing, Boolean> entry : internalBlock.testetFaces.entrySet()){
			if(!entry.getValue()){
				BlockPos posFacing = new BlockPos(internalBlock.pos.getX() + entry.getKey().getFrontOffsetX(), internalBlock.pos.getY() + entry.getKey().getFrontOffsetY(), internalBlock.pos.getZ() + entry.getKey().getFrontOffsetZ());
				BlockPos minPos = getMinimumCoord();
				BlockPos maxPos = getMaxXYCoord();
				if(minPos.getX() > posFacing.getX() || minPos.getZ() > posFacing.getZ() || maxPos.getX() < posFacing.getX() || maxPos.getZ() < posFacing.getZ()){
					throw new MultiblockValidationException(StatCollector.translateToLocalFormatted("for.multiblock.error.space.closed"));
				}
				if(worldObj.getTileEntity(posFacing) instanceof IGreenhouseComponent && worldObj.getBlockState(posFacing).getBlock().isSideSolid(worldObj, posFacing, entry.getKey())){
					entry.setValue(true);
				}
				internalBlocks.add(addInternalBlock(new InternalBlock(posFacing, entry.getKey(), internalBlock), internalBlocks));
			}
		}
		return internalBlock;
	}
	
	@Override
	public CamouflageType getType() {
		return CamouflageType.DEFAULT;
	}

	private class InternalBlock{
		public BlockPos pos;
		public EnumMap<EnumFacing, Boolean> testetFaces = Maps.newEnumMap(EnumFacing.class);
		public InternalBlock root;
		
		public InternalBlock(BlockPos pos) {
			this.pos = pos;
			for(EnumFacing facing : EnumFacing.VALUES){
				testetFaces.put(facing, false);
			}
		}
		
		public InternalBlock(BlockPos pos, EnumFacing facing, InternalBlock root) {
			this.pos = pos;
			for(EnumFacing f : EnumFacing.VALUES){
				testetFaces.put(facing, f == facing);
			}
			this.root = root;
		}
	}

}
