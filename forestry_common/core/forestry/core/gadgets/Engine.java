/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gadgets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;

import buildcraft.api.core.Position;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;

import forestry.core.TemperatureState;
import forestry.core.config.Defaults;
import forestry.core.interfaces.IPowerHandler;
import forestry.core.network.PacketPayload;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.ForestryTank;
import forestry.plugins.PluginBuildCraft;

public abstract class Engine extends TileBase implements IPowerHandler, IPipeConnection, IPowerEmitter {

	@Override
	public PacketPayload getPacketPayload() {
		PacketPayload payload = new PacketPayload(3, 1, 0);

		if (this.isActive)
			payload.intPayload[0] = 1;
		else
			payload.intPayload[0] = 0;
		payload.intPayload[1] = (int) storedEnergy;
		payload.intPayload[2] = heat;

		payload.floatPayload[0] = pistonSpeedServer;

		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {

		if (payload.intPayload[0] > 0)
			isActive = true;
		else
			isActive = false;
		storedEnergy = payload.intPayload[1];
		heat = payload.intPayload[2];

		pistonSpeedServer = payload.floatPayload[0];
	}
	public boolean isActive = false; // Used for smp.
	/**
	 * Indicates whether the piston is receding from or approaching the
	 * combustion chamber
	 */
	public int stagePiston = 0;
	/**
	 * Piston speed as supplied by the server
	 */
	public float pistonSpeedServer = 0;
	protected int currentOutput = 0;
	public final int maxEnergy;
	public final int maxEnergyExtracted;
	public float storedEnergy;
	public int heat;
	protected final int maxHeat;
	protected boolean forceCooldown = false;
	public float progress;
	private final PowerHandler powerProvider;

	public Engine(int maxHeat, int maxEnergy, int maxEnergyExtracted) {
		this.maxHeat = maxHeat;
		this.maxEnergy = maxEnergy;
		this.maxEnergyExtracted = maxEnergyExtracted;
		powerProvider = new PowerHandler(this, PowerHandler.Type.ENGINE);
		powerProvider.configure(10, 200, 10, 100000);
	}

	protected ItemStack replenishByContainer(ItemStack inventoryStack, FluidContainerData container, ForestryTank tank) {
		if (container == null)
			return inventoryStack;

		if (tank.fill(container.fluid, false) >= container.fluid.amount) {
			tank.fill(container.fluid, true);
			if (container.filledContainer != null && container.filledContainer.getItem().hasContainerItem(container.filledContainer))
				inventoryStack = container.emptyContainer.copy();
			else
				inventoryStack.stackSize--;
		}

		return inventoryStack;
	}

	@Override
	public void rotateAfterPlacement(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemstack) {
		rotateEngine();
	}

	/**
	 * Adds energy
	 *
	 * @param addition
	 */
	public void addEnergy(float addition) {
		storedEnergy += addition;

		if (storedEnergy > maxEnergy)
			storedEnergy = maxEnergy;
	}

	/**
	 *
	 * @param min Minimum energy to extract. Will return 0 if storedEnergy less
	 * than min.
	 * @param max Maximum energy to extract.
	 * @param doExtract Determines whether energy will actually be removed from
	 * the engine.
	 *
	 * @return
	 */
	public double extractEnergy(double min, double max, boolean doExtract) {
		if (storedEnergy < min)
			return 0;

		double ceiling = max > maxEnergyExtracted ? maxEnergyExtracted : max;

		double extracted;

		if (storedEnergy >= ceiling) {
			extracted = ceiling;
			if (doExtract)
				storedEnergy -= ceiling;
		} else {
			extracted = storedEnergy;
			if (doExtract)
				storedEnergy = 0;
		}

		return extracted;
	}

	/**
	 * Adds heat
	 *
	 * @param i
	 */
	protected void addHeat(int i) {
		heat += i;

		if (heat > maxHeat)
			heat = maxHeat;
	}

	public abstract int dissipateHeat();

	public abstract int generateHeat();

	public int maxEnergyReceived() {
		return 200;
	}

	public boolean mayBurn() {
		return !forceCooldown;
	}

	public abstract void burn();

	@Override
	public void updateClientSide() {
		if (stagePiston != 0) {
			progress += pistonSpeedServer;

			if (progress > 1) {
				stagePiston = 0;
				progress = 0;
			}
		} else if (this.isActive)
			stagePiston = 1;
		return;
	}

	@Override
	public void updateServerSide() {
		TemperatureState energyState = getTemperatureState();
		if (energyState == TemperatureState.MELTING && heat > 0)
			forceCooldown = true;
		else if (forceCooldown && heat <= 0)
			forceCooldown = false;

		// Determine targeted tile
		Position posTarget = new Position(xCoord, yCoord, zCoord, this.getOrientation());
		posTarget.moveForwards(1.0);
		TileEntity tile = worldObj.getTileEntity((int) posTarget.x, (int) posTarget.y, (int) posTarget.z);

		float newPistonSpeed = getPistonSpeed();
		if (newPistonSpeed != pistonSpeedServer) {
			pistonSpeedServer = newPistonSpeed;
			sendNetworkUpdate();
		}

		if (stagePiston != 0) {

			progress += pistonSpeedServer;

			if (progress > 0.5 && stagePiston == 1) {
				stagePiston = 2;

				if (BlockUtil.isPoweredTile(getOrientation().getOpposite(), tile)) {
					IPowerReceptor receptor = (IPowerReceptor) tile;
					double extractedEnergy = extractEnergy(receptor.getPowerReceiver(getOrientation().getOpposite()).getMinEnergyReceived(), receptor.getPowerReceiver(getOrientation().getOpposite()).getMaxEnergyReceived(),
							true);
					if (extractedEnergy > 0)
						PluginBuildCraft.instance.invokeReceiveEnergyMethod(PowerHandler.Type.ENGINE, receptor.getPowerReceiver(getOrientation().getOpposite()), extractedEnergy, getOrientation().getOpposite());
					// receptor.getPowerProvider().receiveEnergy(extractedEnergy);
				}

			} else if (progress >= 1) {
				progress = 0;
				stagePiston = 0;
			}

		} else if (canPowerTo(tile)) { // If we are not already running, check if
			IPowerReceptor receptor = (IPowerReceptor) tile;
			if (extractEnergy(receptor.getPowerReceiver(getOrientation().getOpposite()).getMinEnergyReceived(), receptor.getPowerReceiver(getOrientation().getOpposite()).getMaxEnergyReceived(), false) > 0) {
				stagePiston = 1; // If we can transfer energy, start running
				setActive(true);
			} else
				setActive(false);
		} else
			setActive(false);

		dissipateHeat();
		generateHeat();
		// Now let's fire up the engine:
		if (mayBurn())
			burn();
		else
			extractEnergy(0, 2, true);

	}

	private boolean canPowerTo(TileEntity tile) {
		return isActivated() && BlockUtil.isPoweredTile(getOrientation().getOpposite(), tile);
	}

	private void setActive(boolean isActive) {
		if (this.isActive == isActive)
			return;

		this.isActive = isActive;
		sendNetworkUpdate();
	}

	/* INTERACTION */
	public void rotateEngine() {

		for (int i = getOrientation().ordinal() + 1; i <= getOrientation().ordinal() + 6; ++i) {
			ForgeDirection orient = ForgeDirection.values()[i % 6];

			Position pos = new Position(xCoord, yCoord, zCoord, orient);
			pos.moveForwards(1.0F);

			TileEntity tile = worldObj.getTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

			if (BlockUtil.isPoweredTile(getOrientation().getOpposite(), tile)) {
				setOrientation(orient);
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
				worldObj.func_147479_m(xCoord, yCoord, zCoord);
				break;
			}
		}
	}

	// STATE INFORMATION
	protected double getHeatLevel() {
		return (double) heat / (double) maxHeat;
	}

	public abstract boolean isBurning();

	public int getBurnTimeRemainingScaled(int i) {
		return 0;
	}

	public boolean hasFuelMin(float percentage) {
		return false;
	}

	public int getCurrentOutput() {
		if (isBurning() && isActivated())
			return currentOutput;
		else
			return 0;
	}

	public float getEnergyStored() {
		return storedEnergy;
	}

	public int getHeat() {
		return heat;
	}

	/**
	 * Returns the current energy state of the engine
	 *
	 * @return
	 */
	public TemperatureState getTemperatureState() {
		// double scaledStorage = (double)storedEnergy / (double)maxEnergy;
		double scaledHeat = (double) heat / (double) maxHeat;

		if (scaledHeat < 0.20)
			return TemperatureState.COOL;
		else if (scaledHeat < 0.45)
			return TemperatureState.WARMED_UP;
		else if (scaledHeat < 0.65)
			return TemperatureState.OPERATING_TEMPERATURE;
		else if (scaledHeat < 0.85)
			return TemperatureState.RUNNING_HOT;
		else if (scaledHeat < 1.0)
			return TemperatureState.OVERHEATING;
		else
			return TemperatureState.MELTING;
	}

	/**
	 * Piston speed
	 *
	 * @return
	 */
	public float getPistonSpeed() {
		switch (getTemperatureState()) {
		case COOL:
			return 0.03f;
		case WARMED_UP:
			return 0.04f;
		case OPERATING_TEMPERATURE:
			return 0.05f;
		case RUNNING_HOT:
			return 0.06f;
		case OVERHEATING:
			return 0.07f;
		case MELTING:
			return Defaults.ENGINE_PISTON_SPEED_MAX;
		default:
			return 0;
		}
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		heat = nbt.getInteger("EngineHeat");
		if (nbt.hasKey("EngineStoredEnergy"))
			storedEnergy = nbt.getInteger("EngineStoredEnergy");
		else
			storedEnergy = nbt.getFloat("EngineStored");

		progress = nbt.getFloat("EngineProgress");
		forceCooldown = nbt.getBoolean("ForceCooldown");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("EngineHeat", heat);
		nbt.setFloat("EngineStored", storedEnergy);
		nbt.setFloat("EngineProgress", progress);
		nbt.setBoolean("ForceCooldown", forceCooldown);
	}

	/* SMP GUI */
	public abstract void getGUINetworkData(int i, int j);

	public abstract void sendGUINetworkData(Container containerEngine, ICrafting iCrafting);

	/* IPOWERRECEPTOR */
	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		return powerProvider.getPowerReceiver();
	}

	@Override
	public PowerHandler getPowerHandler() {
		return powerProvider;
	}

	@Override
	public void doWork(PowerHandler workProvider) {
		if (!Proxies.common.isSimulating(worldObj))
			return;

		addEnergy((int) (PluginBuildCraft.instance.invokeUseEnergyMethod(workProvider, 1, maxEnergyReceived(), true) * 0.95F));
	}

	/* IPIPECONNECTION */
	@Override
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		if (type == PipeType.POWER)
			return ConnectOverride.DEFAULT;
		if (with == getOrientation())
			return ConnectOverride.DISCONNECT;
		return ConnectOverride.DEFAULT;
	}

	/* IPOWEREMITTER */
	@Override
	public boolean canEmitPowerFrom(ForgeDirection side) {
		return side == getOrientation();
	}
}
