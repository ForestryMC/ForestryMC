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
package forestry.greenhouse.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import forestry.api.climate.IClimateSource;
import forestry.api.climate.IClimatiserDefinition;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.api.multiblock.IMultiblockLogic;
import forestry.apiculture.network.packets.PacketActiveUpdate;
import forestry.core.climate.ClimateSource;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.IActivatable;
import forestry.greenhouse.GreenhouseClimateSource;

public class TileGreenhouseClimatiser extends TileGreenhouse implements IActivatable, IGreenhouseComponent.Climatiser {
	
	protected static final int WORK_CYCLES = 1;
	protected static final int ENERGY_PER_OPERATION = 150;
	
	private final IClimatiserDefinition definition;
	private final ClimateSource source;
	
	protected EnumFacing inwards;
	protected EnumFacing leftwards;
	protected BlockPos maxPos;
	protected BlockPos minPos;
	
	private boolean active;
	
	protected TileGreenhouseClimatiser(IClimatiserDefinition definition, int ticksForChange) {
		this(definition, new GreenhouseClimateSource(ticksForChange));
	}
	
	protected TileGreenhouseClimatiser(IClimatiserDefinition definition, ClimateSource source) {
		this.definition = definition;
		this.source = source;
		this.source.setProvider(this);
	}
	
	protected TileGreenhouseClimatiser(IClimatiserDefinition definition) {
		this(definition, 20 + ENERGY_PER_OPERATION / 25);
	}
	
	@Override
	public void onMachineBroken() {
		inwards = null;
		leftwards = null;
		
		minPos = null;
		maxPos = null;
	}
	
	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
		recalculateDirections(minCoord, maxCoord);
		int range = Math.round((float)definition.getRange() / 2);
		
		if(leftwards != null){
			maxPos = getCoordinates().offset(inwards, range).offset(leftwards, range).offset(EnumFacing.UP, range);
			minPos = getCoordinates().offset(inwards).offset(leftwards.getOpposite(), range).offset(EnumFacing.DOWN, range);
			
		}else{
			maxPos = getCoordinates().offset(inwards, range).offset(EnumFacing.EAST, range).offset(EnumFacing.NORTH, range);
			minPos = getCoordinates().offset(inwards).offset(EnumFacing.WEST, range).offset(EnumFacing.SOUTH, range);
		}
	}
	
	public BlockPos getMinPos()	{
		return minPos;
	}
	
	public BlockPos getMaxPos()	{
		return maxPos;
	}
	
	/* Network */
	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		super.encodeDescriptionPacket(packetData);
		packetData.setBoolean("Active", active);
	}

	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		setActive(packetData.getBoolean("Active"));
	}

	/* IActivatable */
	@Override
	public boolean isActive() {
		return active;
	}
	
	public void recalculateDirections(BlockPos minCoord, BlockPos maxCoord) {
		inwards = null;
		leftwards = null;

		int facesMatching = 0;
		if (maxCoord.getX() == getCoordinates().getX() || minCoord.getX() == getCoordinates().getX()) {
			facesMatching++;
		}
		if (maxCoord.getY() == getCoordinates().getY() || minCoord.getY() == getCoordinates().getY()) {
			facesMatching++;
		}
		if (maxCoord.getZ() == getCoordinates().getZ() || minCoord.getZ() == getCoordinates().getZ()) {
			facesMatching++;
		}
		if (facesMatching == 1) {
			if (maxCoord.getX() == getCoordinates().getX()) {
				inwards = EnumFacing.WEST;
				leftwards = EnumFacing.SOUTH;
			} else if (minCoord.getX() == getCoordinates().getX()) {
				inwards = EnumFacing.EAST;
				leftwards = EnumFacing.NORTH;
			} else if (maxCoord.getZ() == getCoordinates().getZ()) {
				inwards = EnumFacing.NORTH;
				leftwards = EnumFacing.WEST;
			} else if (minCoord.getZ() == getCoordinates().getZ()) {
				inwards = EnumFacing.SOUTH;
				leftwards = EnumFacing.EAST;
			} else if (maxCoord.getY() == getCoordinates().getY()) {
				inwards = EnumFacing.DOWN;
			} else {
				inwards = EnumFacing.UP;
			}
		}else{
			inwards = EnumFacing.DOWN;
		}
	}
	
	@Override
	public IClimatiserDefinition getDefinition() {
		return definition;
	}
	
	@Override
	public IClimateSource getClimateSource() {
		return source;
	}
	
	public boolean canWork(){
		IMultiblockLogic logic = getMultiblockLogic();
		if(logic == null || !logic.isConnected() || getMultiblockLogic().getController().getEnergyManager() == null){
			return false;
		}
		return getMultiblockLogic().getController().getEnergyManager().consumeEnergyToDoWork(WORK_CYCLES, ENERGY_PER_OPERATION);
	}
	
	public Iterable<BlockPos> getPositionsInRange(){
		if(maxPos == null || minPos == null){
			return null;
		}
		return BlockPos.getAllInBox(maxPos, minPos);
	}

	@Override
	public void setActive(boolean active) {
		if (this.active == active) {
			return;
		}

		this.active = active;

		if (worldObj != null) {
			if (worldObj.isRemote) {
				worldObj.markBlockRangeForRenderUpdate(getCoordinates(), getCoordinates());
			} else {
				Proxies.net.sendNetworkPacket(new PacketActiveUpdate(this), worldObj);
			}
		}
	}

}
