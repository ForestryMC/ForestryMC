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

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import forestry.api.core.climate.IClimateWorld;
import forestry.api.core.climate.IClimatedPosition;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IGreenhouseController;
import forestry.api.multiblock.IMultiblockController;
import forestry.apiculture.network.packets.PacketActiveUpdate;
import forestry.core.climate.ClimateManager;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.IActivatable;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;

public class TileGreenhouseClimatiser extends TileGreenhouse implements IActivatable, IGreenhouseComponent.Climatiser {
	
	protected static final int WORK_CYCLES = 1;
	protected static final int ENERGY_PER_OPERATION = 50;
	
	protected enum ClimitiserType {
		TEMPERATURE, HUMIDITY
	}
	
	protected interface IClimitiserDefinition {
		float getChangePerTransfer();

		float getBoundaryUp();

		float getBoundaryDown();
		
		float getMaxChange();
		
		int getClimitiseRange();
		
		float getClimitiseBonus();
		
		ClimitiserType getType();
	}
	
	private final IClimitiserDefinition definition;
	
	protected EnumFacing inwards;
	protected EnumFacing leftwards;
	protected BlockPos maxPos;
	protected BlockPos minPos;
	
	protected int workingTime = 0;
	
	private boolean active;
	
	protected TileGreenhouseClimatiser(IClimitiserDefinition definition) {
		this.definition = definition;
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
		
		if(leftwards != null){
			maxPos = getPos().offset(inwards, definition.getClimitiseRange() / 2).offset(leftwards, definition.getClimitiseRange() / 2).offset(EnumFacing.UP, definition.getClimitiseRange() / 2);
			minPos = getPos().offset(inwards).offset(leftwards.getOpposite(), definition.getClimitiseRange() / 2).offset(EnumFacing.DOWN, definition.getClimitiseRange() / 2);
			
		}else{
			maxPos = getPos().offset(inwards, definition.getClimitiseRange() / 2).offset(EnumFacing.EAST, definition.getClimitiseRange() / 2).offset(EnumFacing.NORTH, definition.getClimitiseRange() / 2);
			minPos = getPos().offset(inwards).offset(EnumFacing.WEST, definition.getClimitiseRange() / 2).offset(EnumFacing.SOUTH, definition.getClimitiseRange() / 2);
		}
	}
	
	@Override
	public void changeClimate(int tick, IGreenhouseController greenhouse) {
		if(minPos != null && maxPos != null){
			IGreenhouseControllerInternal greenhouseInternal = (IGreenhouseControllerInternal) greenhouse;
			if (workingTime == 0 && greenhouseInternal.getEnergyManager().consumeEnergyToDoWork(WORK_CYCLES, ENERGY_PER_OPERATION)) {
				int dimensionID = worldObj.provider.getDimension();
				IClimateWorld climateWorld = ClimateManager.getOrCreateWorld(worldObj);
				
				for(BlockPos pos : BlockPos.getAllInBox(maxPos, minPos)){
					IClimatedPosition position = climateWorld.getPosition(pos);
					if(position != null){
						if (definition.getType() == ClimitiserType.TEMPERATURE) {
							if(position.getTemperature() >= 2.0F){
								if(position.getTemperature() > 2.0F){
									position.setTemperature(2.0F);
								}
								continue;
							}else if(position.getTemperature() <= 0.0F){
								if(position.getTemperature() < 0.0F){
									position.setTemperature(0.0F);
								}
								continue;
							}
						}else{
							if(position.getHumidity() >= 2.0F){
								if(position.getHumidity() > 2.0F){
									position.setHumidity(2.0F);
								}
								continue;
							}else if(position.getHumidity() <= 0.0F){
								if(position.getHumidity() < 0.0F){
									position.setHumidity(0.0F);
								}
								continue;
							}
						}
						
						double distance = pos.distanceSq(pos);
						int maxDistance = definition.getClimitiseRange();
						if(distance <= maxDistance){
							if (definition.getType() == ClimitiserType.TEMPERATURE) {
								position.setTemperature(position.getTemperature() + (float) (definition.getMaxChange() / (distance / definition.getClimitiseBonus())));
							}else{
								position.setHumidity(position.getHumidity() + (float) (definition.getMaxChange() / (distance / definition.getClimitiseBonus())));
							}
						}
					}
				}
				
				// one tick of work for every 10 RF
				workingTime += ENERGY_PER_OPERATION / 10;
			}
	
			if (workingTime > 0) {
				workingTime--;
			}
	
			setActive(workingTime > 0);
		}else if(isActive()){
			setActive(false);
		}
	}
	
	/* LOADING & SAVING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		workingTime = nbttagcompound.getInteger("Heating");
		setActive(workingTime > 0);
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("Heating", workingTime);
		return nbttagcompound;
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
		if (maxCoord.getX() == getPos().getX() || minCoord.getX() == getPos().getX()) {
			facesMatching++;
		}
		if (maxCoord.getY() == getPos().getY() || minCoord.getY() == getPos().getY()) {
			facesMatching++;
		}
		if (maxCoord.getZ() == getPos().getZ() || minCoord.getZ() == getPos().getZ()) {
			facesMatching++;
		}
		if (facesMatching == 1) {
			if (maxCoord.getX() == getPos().getX()) {
				inwards = EnumFacing.WEST;
				leftwards = EnumFacing.SOUTH;
			} else if (minCoord.getX() == getPos().getX()) {
				inwards = EnumFacing.EAST;
				leftwards = EnumFacing.NORTH;
			} else if (maxCoord.getZ() == getPos().getZ()) {
				inwards = EnumFacing.NORTH;
				leftwards = EnumFacing.WEST;
			} else if (minCoord.getZ() == getPos().getZ()) {
				inwards = EnumFacing.SOUTH;
				leftwards = EnumFacing.EAST;
			} else if (maxCoord.getY() == getPos().getY()) {
				inwards = EnumFacing.DOWN;
			} else {
				inwards = EnumFacing.UP;
			}
		}
	}
	
	public IClimitiserDefinition getDefinition() {
		return definition;
	}

	@Override
	public void setActive(boolean active) {
		if (this.active == active) {
			return;
		}

		this.active = active;

		if (worldObj != null) {
			if (worldObj.isRemote) {
				worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
			} else {
				Proxies.net.sendNetworkPacket(new PacketActiveUpdate(this), worldObj);
			}
		}
	}

}
