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

import java.io.IOException;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorState;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyCocoon;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.core.errors.EnumErrorCode;
import forestry.core.errors.ErrorLogic;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.IClimatised;
import forestry.core.utils.ClimateUtil;
import forestry.core.utils.InventoryUtil;
import forestry.energy.EnergyHelper;
import forestry.energy.EnergyManager;
import forestry.greenhouse.gui.ContainerGreenhouseNursery;
import forestry.greenhouse.gui.GuiGreenhouseNursery;
import forestry.greenhouse.inventory.InventoryNursery;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.lepidopterology.items.ItemButterflyGE;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileGreenhouseNursery extends TileGreenhouse implements IGreenhouseComponent.Nursery, IStreamableGui, ITickable, IClimatised {
	private static final int WORK_TICK_INTERVAL = 5; // one Forestry work tick happens every WORK_TICK_INTERVAL game ticks
	private static final Random rand = new Random();
	private static final int ENERGY_PER_WORK_CYCLE = 3200;
	private static final int MATURE_TIME_MULTIPLIER = 250;
	private final ButterflyCanSpawnCache butterflyCanSpawnCache = new ButterflyCanSpawnCache();
	private final ErrorLogic errorHandler = new ErrorLogic();

	private int workCounter;
	private int ticksPerWorkCycle;
	private int energyPerWorkCycle;
	@Nullable
	private IButterfly butterfly;

	private int tickCount = rand.nextInt(256);
	
	// the number of work ticks that this tile has had no power
	private int noPowerTime = 0;

	private final InventoryNursery inventory;

	public TileGreenhouseNursery() {
		this.inventory = new InventoryNursery(this);
	}

	public int getWorkCounter() {
		return workCounter;
	}

	public void setTicksPerWorkCycle(int ticksPerWorkCycle) {
		this.ticksPerWorkCycle = ticksPerWorkCycle;
		this.workCounter = 0;
	}

	public int getTicksPerWorkCycle() {
		return ticksPerWorkCycle;
	}

	public void setEnergyPerWorkCycle(int energyPerWorkCycle) {
		this.energyPerWorkCycle = EnergyHelper.scaleForDifficulty(energyPerWorkCycle);
	}

	public int getEnergyPerWorkCycle() {
		return energyPerWorkCycle;
	}
	
	@Override
	public IErrorLogic getErrorLogic() {
		return errorHandler;
	}

	@Override
	public void update() {
		if(world.isRemote){
			return;
		}
		tickCount++;
		
		if (!updateOnInterval(WORK_TICK_INTERVAL)) {
			return;
		}

		if(!canWork()){
			return;
		}
		
		int ticksPerWorkCycle = getTicksPerWorkCycle();
		if (workCounter >= ticksPerWorkCycle) {
			if (workCycle()) {
				workCounter = 0;
			}
		}
	}
	
	protected final boolean updateOnInterval(int tickInterval) {
		return tickCount % tickInterval == 0;
	}
	
	protected boolean canWork(){
		moveCocoonToWorkSlot();
		ItemStack butterflyItem = getButterflyItem();
		boolean hasCocoon = !butterflyItem.isEmpty();
		
		errorHandler.setCondition(!hasCocoon, EnumErrorCode.NO_RESOURCE_INVENTORY);
		if(butterfly == null){
			butterfly = ButterflyManager.butterflyRoot.getMember(butterflyItem);
			if(!butterflyItem.isEmpty() && butterfly == null){
				setInventorySlotContents(InventoryNursery.SLOT_WORK, ItemStack.EMPTY);
				errorHandler.setCondition(true, EnumErrorCode.NO_RESOURCE_INVENTORY);
				return false;
			}
		}
		
		int ticksPerWorkCycle = getTicksPerWorkCycle();

		if (workCounter < ticksPerWorkCycle) {
			EnergyManager energyManager = getMultiblockLogic().getController().getEnergyManager();
			if(energyManager == null){
				return false;
			}
			
			int energyPerWorkCycle = getEnergyPerWorkCycle();
			
			boolean consumedEnergy = EnergyHelper.consumeEnergyToDoWork(energyManager, ticksPerWorkCycle, energyPerWorkCycle);
			if (consumedEnergy) {
				errorHandler.setCondition(false, EnumErrorCode.NO_POWER);
				workCounter++;
				noPowerTime = 0;
				if(workCounter >= ticksPerWorkCycle){
					butterflyCanSpawnCache.clear();
				}
			} else {
				noPowerTime++;
				if (noPowerTime > 4) {
					errorHandler.setCondition(true, EnumErrorCode.NO_POWER);
				}
			}
		}
		
		if(butterfly == null){
			return hasCocoon;
		}
		Set<IErrorState> butterflyErrors = butterflyCanSpawnCache.butterflyCanSpawn(butterfly, this);
		for (IErrorState errorState : butterflyErrors) {
			errorHandler.setCondition(true, errorState);
		}
		return !errorHandler.hasErrors();
	}

	protected boolean workCycle(){
		if(butterfly != null){
			if(canSpawnButterfly()){
				IGreenhouseControllerInternal controller = getMultiblockLogic().getController();
				if(controller.spawnButterfly(this)){
					setInventorySlotContents(InventoryNursery.SLOT_WORK, ItemStack.EMPTY);
					setTicksPerWorkCycle(1);
					setEnergyPerWorkCycle(0);
					butterfly = null;
					butterflyCanSpawnCache.clear();
				}
				return false;
			} else {
				ItemStack stack = getButterflyItem();
				int age = getAge();
				int caterpillarMatureTime = getCaterpillarMatureTime();
				age++;
				ItemButterflyGE.setAge(stack, age);
				setTicksPerWorkCycle(caterpillarMatureTime);
				setEnergyPerWorkCycle(ENERGY_PER_WORK_CYCLE);
			}
		}
		return true;
	}
	
	public boolean canSpawnButterfly(){
		int age = getAge();
		return age > 2 && butterfly != null;
	}
	
	private int getCaterpillarMatureTime(){
		IButterflyGenome genome = butterfly.getGenome();
		float matureTime = genome.getLifespan() / (genome.getFertility() * 2);
		matureTime*=MATURE_TIME_MULTIPLIER;
		return Math.round(matureTime);
	}
	
	private void moveCocoonToWorkSlot() {
		if (!getStackInSlot(InventoryNursery.SLOT_WORK).isEmpty()) {
			return;
		}

		Integer slotIndex = getInputSlotIndex();
		if (slotIndex == null) {
			return;
		}

		ItemStack inputStack = inventory.getStackInSlot(slotIndex);
		if (inputStack.isEmpty()) {
			return;
		}

		ItemStack stack = inputStack.copy();
		stack.setCount(1);
		inputStack.shrink(1);
		setInventorySlotContents(InventoryNursery.SLOT_WORK, stack);
		if(inputStack.isEmpty()){
			inventory.setInventorySlotContents(slotIndex, ItemStack.EMPTY);
		}
		butterfly = ButterflyManager.butterflyRoot.getMember(stack);
		int caterpillarMatureTime = getCaterpillarMatureTime();
		setTicksPerWorkCycle(caterpillarMatureTime);
		setEnergyPerWorkCycle(ENERGY_PER_WORK_CYCLE);
	}
	
	@Nullable
	private Integer getInputSlotIndex() {
		for (int slotIndex = 0; slotIndex < InventoryNursery.SLOT_INPUT_COUNT; slotIndex++) {
			ItemStack inputStack = inventory.getStackInSlot(InventoryNursery.SLOT_INPUT_1 + slotIndex);
			if (!inputStack.isEmpty()) {
				return InventoryNursery.SLOT_INPUT_1 + slotIndex;
			}
		}
		return null;
	}
	
	private ItemStack getButterflyItem(){
		return getStackInSlot(InventoryNursery.SLOT_WORK);
	}
	
	public int getAge(){
		ItemStack cocoon = getButterflyItem();
		if(cocoon.isEmpty() || !cocoon.hasTagCompound()){
			return -1;
		}
		return cocoon.getTagCompound().getInteger(ItemButterflyGE.NBT_AGE);
	}

	public int getProgressScaled(int i) {
		int ticksPerWorkCycle = getTicksPerWorkCycle();
		if (ticksPerWorkCycle == 0) {
			return 0;
		}

		return workCounter * i / ticksPerWorkCycle;
	}

	@Override
	public void writeGuiData(PacketBufferForestry data) {
		super.writeGuiData(data);
		data.writeVarInt(workCounter);
		data.writeVarInt(getTicksPerWorkCycle());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readGuiData(PacketBufferForestry data) throws IOException {
		super.readGuiData(data);
		workCounter = data.readVarInt();
		ticksPerWorkCycle = data.readVarInt();
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setInteger("workCounter", workCounter);
		data.setInteger("ticksPerWorkCycle", ticksPerWorkCycle);
		data.setInteger("energyPerWorkCycle", energyPerWorkCycle);
		return data;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		workCounter = data.getInteger("workCounter");
		ticksPerWorkCycle = data.getInteger("ticksPerWorkCycle");
		energyPerWorkCycle = data.getInteger("energyPerWorkCycle");
	}

	@Override
	public void addCocoonLoot(IButterflyCocoon cocoon, NonNullList<ItemStack> cocoonDrops) {
		for (ItemStack drop : cocoonDrops) {
			InventoryUtil.tryAddStack(this, drop, InventoryNursery.SLOT_OUTPUT_1, InventoryNursery.SLOT_OUTPUT_COUNT, true);
		}
	}

	/* IGuiHandlerTile */
	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, int data) {
		return new GuiGreenhouseNursery(player, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
		return new ContainerGreenhouseNursery(player.inventory, this);
	}
	
	/* ITitled */
	@Override
	public String getUnlocalizedTitle() {
		return "for.gui.greenhouse.nursery.title";
	}
	
	@Override
	public IInventoryAdapter getInternalInventory() {
		return inventory;
	}

	@Override
	public IButterfly getCaterpillar() {
		return butterfly;
	}

	@Nullable
	@Override
	public IIndividual getNanny() {
		return null;
	}

	@Override
	public void setCaterpillar(IButterfly caterpillar) {
	}

	@Override
	public boolean canNurse(IButterfly caterpillar) {
		return false;
	}

	@Override
	public Biome getBiome() {
		return world.getBiome(pos);
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromBiome(getBiome(), world, pos);
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(ClimateUtil.getHumidity(world, pos));
	}
	
	@Override
	public float getExactHumidity() {
		return ClimateUtil.getHumidity(world, pos);
	}
	
	@Override
	public float getExactTemperature() {
		return ClimateUtil.getTemperature(world, pos);
	}
	
	private static class ButterflyCanSpawnCache {
		private static final int ticksPerCheckButterflyCanSpawn = 10;

		private Set<IErrorState> butterflyCanSpawnCached = Collections.emptySet();
		private int butterflzCanSpawnCooldown = 0;

		public Set<IErrorState> butterflyCanSpawn(IButterfly butterfly, TileGreenhouseNursery nursery) {
			if (butterflzCanSpawnCooldown <= 0) {
				if(nursery.canSpawnButterfly()){
					butterflyCanSpawnCached = butterfly.getCanSpawn(nursery, null);
				}else{
					butterflyCanSpawnCached = butterfly.getCanGrow(nursery, null);
				}
				butterflzCanSpawnCooldown = ticksPerCheckButterflyCanSpawn;
			} else {
				butterflzCanSpawnCooldown--;
			}

			return butterflyCanSpawnCached;
		}

		public void clear() {
			butterflyCanSpawnCached.clear();
			butterflzCanSpawnCooldown = 0;
		}
	}
	
}
