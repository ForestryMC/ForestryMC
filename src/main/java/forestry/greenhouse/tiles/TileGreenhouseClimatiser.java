package forestry.greenhouse.tiles;

import forestry.api.core.IClimateControlled;
import forestry.api.greenhouse.IGreenhouseHousing;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IGreenhouseController;
import forestry.apiculture.network.packets.PacketActiveUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.IActivatable;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import net.minecraft.nbt.NBTTagCompound;

public class TileGreenhouseClimatiser extends TileGreenhouse implements IActivatable, IGreenhouseComponent.Climatiser {
	
	protected static final int WORK_CYCLES = 1;
	protected static final int ENERGY_PER_OPERATION = 50;
	
	protected enum ClimitiserType{
		TEMPERATURE, HUMIDITY
	}
	
	protected interface IClimitiserDefinition {
		float getChangePerTransfer();

		float getBoundaryUp();

		float getBoundaryDown();
		
		ClimitiserType getType();
	}
	
	private final IClimitiserDefinition definition;
	
	protected int workingTime = 0;
	
	private boolean active;
	
	protected TileGreenhouseClimatiser(IClimitiserDefinition definition) {
		this.definition = definition;
	}
	
	@Override
	public <G extends IGreenhouseController & IGreenhouseHousing & IClimateControlled> void changeClimate(int tick, G greenhouse) {
		IGreenhouseControllerInternal greenhouseInternal = (IGreenhouseControllerInternal) greenhouse;
		if (workingTime < 20 && greenhouseInternal.getEnergyManager().consumeEnergyToDoWork(WORK_CYCLES, ENERGY_PER_OPERATION)) {
			// one tick of work for every 10 RF
			workingTime += ENERGY_PER_OPERATION / 10;
		}

		if (workingTime > 0) {
			workingTime--;
			if(definition.getType() == ClimitiserType.TEMPERATURE){
				greenhouse.addTemperatureChange(definition.getChangePerTransfer(), definition.getBoundaryDown(), definition.getBoundaryUp());
			}else if(definition.getType() == ClimitiserType.HUMIDITY){
				greenhouse.addHumidityChange(definition.getChangePerTransfer(), definition.getBoundaryDown(), definition.getBoundaryUp());
			}
		}

		setActive(workingTime > 0);
	}
	
	/* LOADING & SAVING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		workingTime = nbttagcompound.getInteger("Heating");
		setActive(workingTime > 0);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("Heating", workingTime);
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
