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
package forestry.farming.multiblock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitSocketType;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogic;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmInventory;
import forestry.api.farming.IFarmListener;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmable;
import forestry.api.multiblock.IFarmComponent;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.core.access.EnumAccess;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.Fluids;
import forestry.core.fluids.TankManager;
import forestry.core.fluids.tanks.FilteredTank;
import forestry.core.fluids.tanks.StandardTank;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.multiblock.MultiblockValidationException;
import forestry.core.multiblock.RectangularMultiblockControllerBase;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.utils.Log;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.vect.Vect;
import forestry.core.utils.vect.VectUtil;
import forestry.farming.FarmHelper;
import forestry.farming.FarmTarget;
import forestry.farming.gui.IFarmLedgerDelegate;
import forestry.farming.logic.FarmLogicArboreal;
import forestry.farming.tiles.TileFarmGearbox;
import forestry.farming.tiles.TileFarmPlain;

public class FarmController extends RectangularMultiblockControllerBase implements IFarmControllerInternal, ILiquidTankTile {

	private enum Stage {
		CULTIVATE, HARVEST;

		public Stage next() {
			if (this == CULTIVATE) {
				return HARVEST;
			} else {
				return CULTIVATE;
			}
		}
	}

	private static FarmDirection getLayoutDirection(FarmDirection farmSide) {
		switch (farmSide) {
			case NORTH:
				return FarmDirection.WEST;
			case WEST:
				return FarmDirection.SOUTH;
			case SOUTH:
				return FarmDirection.EAST;
			case EAST:
				return FarmDirection.NORTH;
		}
		return null;
	}

	private final Map<FarmDirection, List<FarmTarget>> targets = new EnumMap<>(FarmDirection.class);
	private int allowedExtent = 0;

	private IFarmLogic harvestProvider; // The farm logic which supplied the pending crops.
	private final Stack<ICrop> pendingCrops = new Stack<>();
	private final Stack<ItemStack> pendingProduce = new Stack<>();

	private Stage stage = Stage.CULTIVATE;

	// active components are stored with a tick offset so they do not all tick together
	private final Map<IFarmComponent.Active, Integer> farmActiveComponents = new HashMap<>();
	private final Set<IFarmListener> farmListeners = new HashSet<>();

	private final Map<FarmDirection, IFarmLogic> farmLogics = new EnumMap<>(FarmDirection.class);

	private final InventoryAdapter sockets;
	private final InventoryFarm inventory;
	private final TankManager tankManager;
	private final StandardTank resourceTank;
	private final FarmHydrationManager hydrationManager;
	private final FarmFertilizerManager fertilizerManager;

	// the number of work ticks that this farm has had no power
	private int noPowerTime = 0;

	// tick updates can come from multiple gearboxes so keep track of them here
	private int farmWorkTicks = 0;

	private BiomeGenBase cachedBiome;

	public FarmController(World world) {
		super(world, FarmMultiblockSizeLimits.instance);

		this.resourceTank = new FilteredTank(Constants.PROCESSOR_TANK_CAPACITY, FluidRegistry.WATER);
		this.tankManager = new TankManager(this, resourceTank);

		this.inventory = new InventoryFarm(this);
		this.sockets = new InventoryAdapter(1, "sockets");
		this.hydrationManager = new FarmHydrationManager(this);
		this.fertilizerManager = new FarmFertilizerManager();

		refreshFarmLogics();
	}

	@Override
	public IFarmLedgerDelegate getFarmLedgerDelegate() {
		return hydrationManager;
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		if (isAssembled()) {
			return inventory;
		} else {
			return FakeInventoryAdapter.instance();
		}
	}

	@Override
	public TankManager getTankManager() {
		return tankManager;
	}

	private BiomeGenBase getBiome() {
		if (cachedBiome == null) {
			ChunkCoordinates coords = getReferenceCoord();
			cachedBiome = worldObj.getBiomeGenForCoords(coords.posX, coords.posZ);
		}
		return cachedBiome;
	}

	@Override
	public void onAttachedPartWithMultiblockData(IMultiblockComponent part, NBTTagCompound data) {
		this.readFromNBT(data);
	}

	@Override
	protected void onBlockAdded(IMultiblockComponent newPart) {
		if (newPart instanceof IFarmComponent.Listener) {
			IFarmComponent.Listener listenerPart = (IFarmComponent.Listener) newPart;
			farmListeners.add(listenerPart.getFarmListener());
		}

		if (newPart instanceof IFarmComponent.Active) {
			farmActiveComponents.put((IFarmComponent.Active) newPart, worldObj.rand.nextInt(256));
		}
	}

	@Override
	protected void onBlockRemoved(IMultiblockComponent oldPart) {
		if (oldPart instanceof IFarmComponent.Listener) {
			IFarmComponent.Listener listenerPart = (IFarmComponent.Listener) oldPart;
			farmListeners.remove(listenerPart.getFarmListener());
		}

		if (oldPart instanceof IFarmComponent.Active) {
			farmActiveComponents.remove(oldPart);
		}
	}

	@Override
	protected void isMachineWhole() throws MultiblockValidationException {
		super.isMachineWhole();

		boolean hasGearbox = false;
		for (IMultiblockComponent part : connectedParts) {
			if (part instanceof TileFarmGearbox) {
				hasGearbox = true;
				break;
			}
		}

		if (!hasGearbox) {
			throw new MultiblockValidationException(StatCollector.translateToLocal("for.multiblock.farm.error.needGearbox"));
		}
	}

	@Override
	protected void onMachineDisassembled() {
		super.onMachineDisassembled();
		targets.clear();
	}

	@Override
	public void isGoodForExteriorLevel(IMultiblockComponent part, int level) throws MultiblockValidationException {
		if (level == 2 && !(part instanceof TileFarmPlain)) {
			throw new MultiblockValidationException(StatCollector.translateToLocal("for.multiblock.farm.error.needPlainBand"));
		}
	}

	@Override
	public void isGoodForInterior(IMultiblockComponent part) throws MultiblockValidationException {
		if (!(part instanceof TileFarmPlain)) {
			throw new MultiblockValidationException(StatCollector.translateToLocal("for.multiblock.farm.error.needPlainInterior"));
		}
	}

	@Override
	public void onAssimilate(IMultiblockControllerInternal assimilated) {

	}

	@Override
	public void onAssimilated(IMultiblockControllerInternal assimilator) {

	}

	@Override
	protected boolean updateServer(int tickCount) {
		hydrationManager.updateServer(worldObj, getBiome());

		if (updateOnInterval(20)) {
			inventory.drainCan(tankManager);
		}

		boolean hasPower = false;
		for (Map.Entry<IFarmComponent.Active, Integer> entry : farmActiveComponents.entrySet()) {
			IFarmComponent.Active farmComponent = entry.getKey();
			if (farmComponent instanceof TileFarmGearbox) {
				hasPower |= ((TileFarmGearbox) farmComponent).getEnergyManager().getTotalEnergyStored() > 0;
			}

			int tickOffset = entry.getValue();
			farmComponent.updateServer(tickCount + tickOffset);
		}

		if (hasPower) {
			noPowerTime = 0;
			getErrorLogic().setCondition(false, EnumErrorCode.NO_POWER);
		} else {
			if (noPowerTime <= 4) {
				noPowerTime++;
			} else {
				getErrorLogic().setCondition(true, EnumErrorCode.NO_POWER);
			}
		}

		//FIXME: be smarter about the farm needing to save
		return true;
	}

	@Override
	protected void updateClient(int tickCount) {
		for (Map.Entry<IFarmComponent.Active, Integer> entry : farmActiveComponents.entrySet()) {
			IFarmComponent.Active farmComponent = entry.getKey();
			int tickOffset = entry.getValue();
			farmComponent.updateClient(tickCount + tickOffset);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		sockets.writeToNBT(data);
		hydrationManager.writeToNBT(data);
		tankManager.writeToNBT(data);
		fertilizerManager.writeToNBT(data);
		inventory.writeToNBT(data);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		sockets.readFromNBT(data);
		hydrationManager.readFromNBT(data);
		tankManager.readFromNBT(data);
		fertilizerManager.readFromNBT(data);
		inventory.readFromNBT(data);

		refreshFarmLogics();
	}

	@Override
	public void formatDescriptionPacket(NBTTagCompound data) {
		sockets.writeToNBT(data);
		hydrationManager.writeToNBT(data);
		tankManager.writeToNBT(data);
		fertilizerManager.writeToNBT(data);
	}

	@Override
	public void decodeDescriptionPacket(NBTTagCompound data) {
		sockets.readFromNBT(data);
		hydrationManager.readFromNBT(data);
		tankManager.readFromNBT(data);
		fertilizerManager.readFromNBT(data);

		refreshFarmLogics();
	}

	@Override
	public ChunkCoordinates getCoordinates() {
		ChunkCoordinates coord = getReferenceCoord();
		return new ChunkCoordinates(coord);
	}

	@Override
	public void onSwitchAccess(EnumAccess oldAccess, EnumAccess newAccess) {
		if (oldAccess == EnumAccess.SHARED || newAccess == EnumAccess.SHARED) {
			// pipes connected to this need to update
			for (IMultiblockComponent part : connectedParts) {
				if (part instanceof TileEntity) {
					TileEntity tile = (TileEntity) part;
					tile.getWorldObj().notifyBlocksOfNeighborChange(tile.xCoord, tile.yCoord, tile.zCoord, tile.getBlockType());
				}
			}
			markDirty();
		}
	}

	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		tankManager.writeData(data);
		hydrationManager.writeData(data);
		fertilizerManager.writeData(data);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		tankManager.readData(data);
		hydrationManager.readData(data);
		fertilizerManager.readData(data);
	}

	private void refreshFarmLogics() {
		for (FarmDirection direction : FarmDirection.values()) {
			resetFarmLogic(direction);
		}

		// See whether we have socketed stuff.
		ItemStack chip = sockets.getStackInSlot(0);
		if (chip != null) {
			ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(chip);
			if (chipset != null) {
				chipset.onLoad(this);
			}
		}
	}

	@Override
	public EnumTemperature getTemperature() {
		ChunkCoordinates coords = getReferenceCoord();
		return EnumTemperature.getFromBiome(getBiome(), coords.posX, coords.posY, coords.posZ);
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getExactHumidity());
	}

	@Override
	public float getExactTemperature() {
		ChunkCoordinates coords = getReferenceCoord();
		return getBiome().getFloatTemperature(coords.posX, coords.posY, coords.posZ);
	}

	@Override
	public float getExactHumidity() {
		return getBiome().rainfall;
	}

	private int[] coords;
	private int[] offset;
	private int[] area;

	@Override
	public int[] getCoords() {
		if (coords == null) {
			ChunkCoordinates centerCoord = getCenterCoord();
			coords = new int[]{centerCoord.posX, centerCoord.posY, centerCoord.posZ};
		}
		return coords;
	}

	@Override
	public int[] getOffset() {
		if (offset == null) {
			offset = new int[]{-getArea()[0] / 2, -2, -getArea()[2] / 2};
		}
		return offset;
	}

	@Override
	public int[] getArea() {
		if (area == null) {
			area = new int[]{7 + (allowedExtent * 2), 13, 7 + (allowedExtent * 2)};
		}
		return area;
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public boolean doWork() {
		farmWorkTicks++;
		if (targets.isEmpty() || farmWorkTicks % 20 == 0) {
			setUpFarmlandTargets();
		}

		IErrorLogic errorLogic = getErrorLogic();

		if (!pendingProduce.isEmpty()) {
			boolean added = inventory.tryAddPendingProduce(pendingProduce);
			errorLogic.setCondition(!added, EnumErrorCode.NO_SPACE_INVENTORY);
			return added;
		}

		boolean hasFertilizer = fertilizerManager.maintainFertilizer(inventory);
		if (errorLogic.setCondition(!hasFertilizer, EnumErrorCode.NO_FERTILIZER)) {
			return false;
		}

		// Cull queued crops.
		if (!pendingCrops.isEmpty()) {
			if (cullCrop(pendingCrops.peek(), harvestProvider)) {
				pendingCrops.pop();
				return true;
			} else {
				return false;
			}
		}

		// Cultivation and collection
		FarmWorkStatus farmWorkStatus = new FarmWorkStatus();

		for (FarmDirection farmSide : FarmDirection.values()) {

			IFarmLogic logic = getFarmLogic(farmSide);
			if (logic == null) {
				continue;
			}

			// Allow listeners to cancel this cycle.
			if (isCycleCanceledByListeners(logic, farmSide, farmListeners)) {
				continue;
			}

			// Always try to collect windfall.
			if (collectWindfall(logic)) {
				farmWorkStatus.didWork = true;
			}

			List<FarmTarget> farmTargets = targets.get(farmSide);

			if (stage == Stage.HARVEST) {
				Collection<ICrop> harvested = harvestTargets(farmTargets, logic, farmListeners);
				farmWorkStatus.didWork = harvested.size() > 0;
				if (harvested.size() > 0) {
					pendingCrops.addAll(harvested);
					harvestProvider = logic;
				}
			} else if (stage == Stage.CULTIVATE) {
				farmWorkStatus = cultivateTargets(farmWorkStatus, farmTargets, logic);
			}

			if (farmWorkStatus.didWork) {
				break;
			}
		}

		if (stage == Stage.CULTIVATE) {
			errorLogic.setCondition(!farmWorkStatus.hasFarmland, EnumErrorCode.NO_FARMLAND);
			errorLogic.setCondition(!farmWorkStatus.hasFertilizer, EnumErrorCode.NO_FERTILIZER);
			errorLogic.setCondition(!farmWorkStatus.hasLiquid, EnumErrorCode.NO_LIQUID_FARM);
		}

		// alternate between cultivation and harvest.
		stage = stage.next();

		return farmWorkStatus.didWork;
	}

	private void setUpFarmlandTargets() {
		Vect targetStart = new Vect(getCoords());

		ChunkCoordinates max = getMaximumCoord();
		ChunkCoordinates min = getMinimumCoord();

		int sizeNorthSouth = Math.abs(max.posZ - min.posZ) + 1;
		int sizeEastWest = Math.abs(max.posX - min.posX) + 1;

		// Set the maximum allowed extent.
		allowedExtent = Math.max(sizeNorthSouth, sizeEastWest) * Config.farmSize + 1;

		createTargets(worldObj, targets, targetStart, allowedExtent, sizeNorthSouth, sizeEastWest);
		setExtents(worldObj, targets);
	}

	private static void createTargets(World world, Map<FarmDirection, List<FarmTarget>> targets, Vect targetStart, final int allowedExtent, final int farmSizeNorthSouth, final int farmSizeEastWest) {

		for (FarmDirection farmSide : FarmDirection.values()) {

			final int farmWidth;
			if (farmSide == FarmDirection.NORTH || farmSide == FarmDirection.SOUTH) {
				farmWidth = farmSizeEastWest;
			} else {
				farmWidth = farmSizeNorthSouth;
			}

			// targets extend sideways in a pinwheel pattern around the farm, so they need to go a little extra distance
			final int targetMaxLimit = allowedExtent + farmWidth;

			FarmDirection layoutDirection = getLayoutDirection(farmSide);

			Vect targetLocation = FarmHelper.getFarmMultiblockCorner(world, targetStart, farmSide, layoutDirection);
			Vect firstLocation = targetLocation.add(farmSide);
			Vect firstGroundPosition = getGroundPosition(world, firstLocation);
			if (firstGroundPosition == null) {
				continue;
			}
			int groundHeight = firstGroundPosition.getY();

			List<FarmTarget> farmSideTargets = new ArrayList<>();
			for (int i = 0; i < allowedExtent; i++) {
				targetLocation = targetLocation.add(farmSide);
				Vect groundLocation = new Vect(targetLocation.getX(), groundHeight, targetLocation.getZ());

				int targetLimit = targetMaxLimit;
				if (!Config.squareFarms) {
					targetLimit = targetMaxLimit - i - 1;
				}

				Block platform = VectUtil.getBlock(world, groundLocation);
				if (!FarmHelper.bricks.contains(platform)) {
					break;
				}

				FarmTarget target = new FarmTarget(targetLocation, layoutDirection, targetLimit);
				farmSideTargets.add(target);
			}

			targets.put(farmSide, farmSideTargets);
		}
	}

	private static Vect getGroundPosition(World world, Vect targetPosition) {
		for (int yOffset = 2; yOffset > -4; yOffset--) {
			Vect position = targetPosition.add(0, yOffset, 0);
			Block ground = VectUtil.getBlock(world, position);
			if (FarmHelper.bricks.contains(ground)) {
				return position;
			}
		}
		return null;
	}

	private static boolean isCycleCanceledByListeners(IFarmLogic logic, FarmDirection direction, Iterable<IFarmListener> farmListeners) {
		for (IFarmListener listener : farmListeners) {
			if (listener.cancelTask(logic, direction)) {
				return true;
			}
		}
		return false;
	}

	private static void setExtents(World worldObj, Map<FarmDirection, List<FarmTarget>> targets) {
		for (List<FarmTarget> targetsList : targets.values()) {
			if (!targetsList.isEmpty()) {
				Vect groundPosition = getGroundPosition(worldObj, targetsList.get(0).getStart());

				for (FarmTarget target : targetsList) {
					target.setExtentAndYOffset(worldObj, groundPosition);
				}
			}
		}
	}

	private static class FarmWorkStatus {
		public boolean didWork = false;
		public boolean hasFarmland = false;
		public boolean hasFertilizer = false;
		public boolean hasLiquid = false;
	}

	private FarmWorkStatus cultivateTargets(FarmWorkStatus farmWorkStatus, List<FarmTarget> farmTargets, IFarmLogic logic) {
		float hydrationModifier = hydrationManager.getHydrationModifier();

		final int fertilizerConsumption = logic.getFertilizerConsumption();
		int liquidConsumption = logic.getWaterConsumption(hydrationModifier);
		FluidStack liquid = Fluids.WATER.getFluid(liquidConsumption);

		if (farmTargets != null) {
			for (FarmTarget target : farmTargets) {
				if (target.getExtent() <= 0) {
					break;
				} else {
					farmWorkStatus.hasFarmland = true;
				}

				// Check fertilizer and water
				if (!fertilizerManager.hasFertilizer(fertilizerConsumption)) {
					continue;
				}

				if (liquid.amount > 0 && !hasLiquid(liquid)) {
					continue;
				}

				if (cultivateTarget(target, logic, farmListeners)) {
					// Remove fertilizer and water
					fertilizerManager.removeFertilizer(fertilizerConsumption);
					removeLiquid(liquid);

					farmWorkStatus.didWork = true;
				}
			}
		}

		farmWorkStatus.hasLiquid = liquid.amount <= 0 || hasLiquid(liquid);
		farmWorkStatus.hasFertilizer = fertilizerManager.hasFertilizer(fertilizerConsumption);

		return farmWorkStatus;
	}

	private static boolean cultivateTarget(FarmTarget target, IFarmLogic logic, Iterable<IFarmListener> farmListeners) {
		Vect targetPosition = target.getStart().add(0, target.getYOffset(), 0);
		if (logic.cultivate(targetPosition.x, targetPosition.y, targetPosition.z, target.getDirection(), target.getExtent())) {
			for (IFarmListener listener : farmListeners) {
				listener.hasCultivated(logic, targetPosition.x, targetPosition.y, targetPosition.z, target.getDirection(), target.getExtent());
			}
			return true;
		}

		return false;
	}

	private static Collection<ICrop> harvestTargets(List<FarmTarget> farmTargets, IFarmLogic logic, Iterable<IFarmListener> farmListeners) {
		if (farmTargets != null) {
			for (FarmTarget target : farmTargets) {
				Collection<ICrop> harvested = harvestTarget(target, logic, farmListeners);
				if (harvested.size() > 0) {
					return harvested;
				}
			}
		}

		return Collections.emptyList();
	}

	private static Collection<ICrop> harvestTarget(FarmTarget target, IFarmLogic logic, Iterable<IFarmListener> farmListeners) {
		Collection<ICrop> harvested = logic.harvest(target.getStart().x, target.getStart().y + target.getYOffset(), target.getStart().z, target.getDirection(), target.getExtent());
		if (harvested == null || harvested.size() == 0) {
			return Collections.emptyList();
		}

		// Let event handlers know.
		for (IFarmListener listener : farmListeners) {
			listener.hasScheduledHarvest(harvested, logic, target.getStart().x, target.getStart().y + target.getYOffset(), target.getStart().z, target.getDirection(), target.getExtent());
		}

		return harvested;
	}

	private boolean collectWindfall(IFarmLogic logic) {

		Collection<ItemStack> collected = logic.collect();
		if (collected == null || collected.size() <= 0) {
			return false;
		}

		// Let event handlers know.
		for (IFarmListener listener : farmListeners) {
			listener.hasCollected(collected, logic);
		}

		for (ItemStack produce : collected) {
			inventory.addProduce(produce);
			pendingProduce.push(produce);
		}

		return true;
	}

	private boolean cullCrop(ICrop crop, IFarmLogic provider) {

		// Let event handlers handle the harvest first.
		for (IFarmListener listener : farmListeners) {
			if (listener.beforeCropHarvest(crop)) {
				return true;
			}
		}

		final int fertilizerConsumption = provider.getFertilizerConsumption();

		IErrorLogic errorLogic = getErrorLogic();

		// Check fertilizer
		Boolean hasFertilizer = fertilizerManager.hasFertilizer(fertilizerConsumption);
		if (errorLogic.setCondition(!hasFertilizer, EnumErrorCode.NO_FERTILIZER)) {
			return false;
		}

		// Check water
		float hydrationModifier = hydrationManager.getHydrationModifier();
		int waterConsumption = provider.getWaterConsumption(hydrationModifier);
		FluidStack requiredLiquid = Fluids.WATER.getFluid(waterConsumption);
		boolean hasLiquid = requiredLiquid.amount == 0 || hasLiquid(requiredLiquid);

		if (errorLogic.setCondition(!hasLiquid, EnumErrorCode.NO_LIQUID_FARM)) {
			return false;
		}

		Collection<ItemStack> harvested = crop.harvest();
		if (harvested == null) {
			Log.fine("Failed to harvest crop: " + crop);
			return true;
		}

		// Remove fertilizer and water
		fertilizerManager.removeFertilizer(fertilizerConsumption);
		removeLiquid(requiredLiquid);

		// Let event handlers handle the harvest first.
		for (IFarmListener listener : farmListeners) {
			listener.afterCropHarvest(harvested, crop);
		}

		inventory.stowHarvest(harvested, pendingProduce);

		return true;
	}

	@Override
	public int getStoredFertilizerScaled(int scale) {
		return fertilizerManager.getStoredFertilizerScaled(scale);
	}

	@Override
	public boolean hasLiquid(FluidStack liquid) {
		return resourceTank.canDrain(liquid);
	}

	@Override
	public void removeLiquid(FluidStack liquid) {
		resourceTank.drain(liquid.amount, true);
	}

	@Override
	public boolean plantGermling(IFarmable germling, World world, int x, int y, int z) {
		EntityPlayer player = PlayerUtil.getPlayer(world, getAccessHandler().getOwner());
		return inventory.plantGermling(germling, player, x, y, z);
	}

	@Override
	public IFarmInventory getFarmInventory() {
		return inventory;
	}

	@Override
	public void setFarmLogic(FarmDirection direction, IFarmLogic logic) {
		if (logic == null) {
			throw new NullPointerException("logic must not be null");
		}
		farmLogics.put(direction, logic);
	}

	@Override
	public void resetFarmLogic(FarmDirection direction) {
		setFarmLogic(direction, new FarmLogicArboreal(this));
	}

	@Override
	public IFarmLogic getFarmLogic(FarmDirection direction) {
		return farmLogics.get(direction);
	}

	@Override
	public int getSocketCount() {
		return sockets.getSizeInventory();
	}

	@Override
	public ItemStack getSocket(int slot) {
		return sockets.getStackInSlot(slot);
	}

	@Override
	public void setSocket(int slot, ItemStack stack) {
		if (stack != null && !ChipsetManager.circuitRegistry.isChipset(stack)) {
			return;
		}

		// Dispose old chipsets correctly
		if (sockets.getStackInSlot(slot) != null) {
			if (ChipsetManager.circuitRegistry.isChipset(sockets.getStackInSlot(slot))) {
				ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(sockets.getStackInSlot(slot));
				if (chipset != null) {
					chipset.onRemoval(this);
				}
			}
		}

		sockets.setInventorySlotContents(slot, stack);
		refreshFarmLogics();

		if (stack == null) {
			return;
		}

		ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitboard(stack);
		if (chipset != null) {
			chipset.onInsertion(this);
		}
	}

	@Override
	public ICircuitSocketType getSocketType() {
		return CircuitSocketType.FARM;
	}
}
