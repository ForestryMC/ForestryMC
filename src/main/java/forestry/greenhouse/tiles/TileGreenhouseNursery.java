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
import java.util.Random;

import javax.annotation.Nullable;

import forestry.api.core.IErrorLogic;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyCocoon;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.core.errors.EnumErrorCode;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.InventoryAnalyzer;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.utils.InventoryUtil;
import forestry.energy.EnergyHelper;
import forestry.energy.EnergyManager;
import forestry.greenhouse.gui.ContainerGreenhouseNursery;
import forestry.greenhouse.gui.GuiGreenhouseNursery;
import forestry.greenhouse.inventory.InventoryGreenhouseNursery;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.lepidopterology.items.ItemButterflyGE;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileGreenhouseNursery extends TileGreenhouse implements IGreenhouseComponent.Nursery, IStreamableGui, ITickable {
	private static final int WORK_TICK_INTERVAL = 5; // one Forestry work tick happens every WORK_TICK_INTERVAL game ticks
	private static final Random rand = new Random();
	private static final int ENERGY_PER_WORK_CYCLE = 3200;
	private static final int MATURE_TIME_MULTIPLIER = 250;

	private int workCounter;
	private int ticksPerWorkCycle;
	private int energyPerWorkCycle;
	@Nullable
	private IButterfly butterfly; 

	private int tickCount = rand.nextInt(256);
	
	// the number of work ticks that this tile has had no power
	private int noPowerTime = 0;

	private final InventoryGreenhouseNursery inventory;

	public TileGreenhouseNursery() {
		this.inventory = new InventoryGreenhouseNursery(this);
		this.ticksPerWorkCycle = 4;
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
	public void update() {
		if(world.isRemote){
			return;
		}
		tickCount++;
		
		if (!updateOnInterval(WORK_TICK_INTERVAL)) {
			return;
		}

		IErrorLogic errorLogic = getErrorLogic();

		if(!hasWork()){
			return;
		}
		
		EnergyManager energyManager = getMultiblockLogic().getController().getEnergyManager();
		if(energyManager == null){
			return;
		}
		int ticksPerWorkCycle = getTicksPerWorkCycle();

		if (workCounter < ticksPerWorkCycle) {
			int energyPerWorkCycle = getEnergyPerWorkCycle();
			boolean consumedEnergy = EnergyHelper.consumeEnergyToDoWork(energyManager, ticksPerWorkCycle, energyPerWorkCycle);
			if (consumedEnergy) {
				errorLogic.setCondition(false, EnumErrorCode.NO_POWER);
				workCounter++;
				noPowerTime = 0;
			} else {
				noPowerTime++;
				if (noPowerTime > 4) {
					errorLogic.setCondition(true, EnumErrorCode.NO_POWER);
				}
			}
		}

		if (workCounter >= ticksPerWorkCycle) {
			if (workCycle()) {
				workCounter = 0;
			}
		}
	}
	
	protected final boolean updateOnInterval(int tickInterval) {
		return tickCount % tickInterval == 0;
	}
	
	protected boolean hasWork(){
		IErrorLogic errorLogic = getErrorLogic();
		Integer inputSlotIndex = getInputSlotIndex();
		boolean hasCocoon = inputSlotIndex != null || !getStackInSlot(InventoryGreenhouseNursery.SLOT_WORK).isEmpty();
		
		getErrorLogic().setCondition(!hasCocoon, EnumErrorCode.NO_RESOURCE_INVENTORY);
		return hasCocoon;
	}

	protected boolean workCycle(){
		moveCocoonToWorkSlot();
		ItemStack stack = getStackInSlot(InventoryGreenhouseNursery.SLOT_WORK);
		if(butterfly == null){
			butterfly = ButterflyManager.butterflyRoot.getMember(stack);
			if(butterfly == null){
				setInventorySlotContents(InventoryGreenhouseNursery.SLOT_WORK, ItemStack.EMPTY);
			}
		}
		if(butterfly != null){
			int age = getAge();
			if(age >= 0 && age < 2){
				IButterflyGenome genome = butterfly.getGenome();
				float matureTime = genome.getLifespan() / (genome.getFertility() * 2);
				matureTime*=MATURE_TIME_MULTIPLIER;
				int caterpillarMatureTime = Math.round(matureTime);
				if(workCounter > 4){
					age++;
					ItemButterflyGE.setAge(stack, age);
				}
				setTicksPerWorkCycle(caterpillarMatureTime);
				setEnergyPerWorkCycle(ENERGY_PER_WORK_CYCLE);
			}else{
				IGreenhouseControllerInternal controller = getMultiblockLogic().getController();
				if(controller.spawnButterfly(butterfly)){
					setInventorySlotContents(InventoryGreenhouseNursery.SLOT_WORK, ItemStack.EMPTY);
					setTicksPerWorkCycle(1);
					setEnergyPerWorkCycle(0);
					butterfly = null;
				}
				return false;
			}
		}
		return true;
	}
	
	
	
	private void moveCocoonToWorkSlot() {
		if (!getStackInSlot(InventoryAnalyzer.SLOT_ANALYZE).isEmpty()) {
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
		setInventorySlotContents(InventoryGreenhouseNursery.SLOT_WORK, stack);
		if(inputStack.isEmpty()){
			inventory.setInventorySlotContents(slotIndex, ItemStack.EMPTY);
		}
	}
	
	@Nullable
	private Integer getInputSlotIndex() {
		for (int slotIndex = 0; slotIndex < InventoryGreenhouseNursery.SLOT_INPUT_COUNT; slotIndex++) {
			ItemStack inputStack = inventory.getStackInSlot(InventoryGreenhouseNursery.SLOT_INPUT_1 + slotIndex);
			if (!inputStack.isEmpty()) {
				return InventoryGreenhouseNursery.SLOT_INPUT_1 + slotIndex;
			}
		}
		return null;
	}
	
	public int getAge(){
		ItemStack cocoon = getStackInSlot(InventoryGreenhouseNursery.SLOT_WORK);
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
	public void addCocoonLoot(IButterflyCocoon cocoon, NonNullList<ItemStack> cocoonDrops) {
		for (ItemStack drop : cocoonDrops) {
			InventoryUtil.tryAddStack(this, drop, InventoryGreenhouseNursery.SLOT_OUTPUT_1, InventoryGreenhouseNursery.SLOT_OUTPUT_COUNT, true);
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
	
}
