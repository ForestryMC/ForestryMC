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
package forestry.apiculture.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeListener;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.core.interfaces.ICrafter;
import forestry.core.network.GuiId;
import forestry.core.utils.InventoryAdapter;

public class TileAlvearySieve extends TileAlveary implements ICrafter, IBeeListener {

	/* CONSTANTS */
	public static final int BLOCK_META = 7;
	
	public static final int SLOT_POLLEN_1 = 0;
	public static final int SLOTS_POLLEN_COUNT = 4;
	public static final int SLOT_SIEVE = 4;
	
	InventoryAdapter inventory = new InventoryAdapter(5, "Items", 1);

	public TileAlvearySieve() {
		super(BLOCK_META);
	}

	@Override
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, GuiId.AlvearySieveGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public String getInventoryName() {
		return "tile.alveary.7";
	}

	@Override
	public boolean hasFunction() {
		return true;
	}
	
	/* UPDATING */
	@Override
	public void initialize() {
		super.initialize();
		
		if(!hasMaster() || !isIntegratedIntoStructure())
			return;
		
		((IAlvearyComponent)getCentralTE()).registerBeeListener(this);
	}
	
	
	@Override
	protected void updateServerSide() {
		super.updateServerSide();
		if(worldObj.getTotalWorldTime() % 200 != 0)
			return;
		
		if(!hasMaster() || !isIntegratedIntoStructure())
			return;
		
		((IAlvearyComponent)getCentralTE()).registerBeeListener(this);
	}
	
	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		inventory.readFromNBT(nbttagcompound);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		inventory.writeToNBT(nbttagcompound);
	}
	
	/* TEXTURES & INTERNAL */
	@Override
	public int getIcon(int side, int metadata) {
		if(side == 0 || side == 1)
			return BlockAlveary.BOTTOM;
		return BlockAlveary.SIEVE;
	}

	private void destroySieve() {
		inventory.setInventorySlotContents(SLOT_SIEVE, null);
	}
	private void destroyPollen() {
		for(int i = SLOT_POLLEN_1; i < SLOT_POLLEN_1 + SLOTS_POLLEN_COUNT; i++) {
			inventory.setInventorySlotContents(i, null);
		}
	}
	
	private boolean canStorePollen() {
		if(inventory.getStackInSlot(SLOT_SIEVE) == null)
			return false;
		
		for(int i = SLOT_POLLEN_1; i < SLOT_POLLEN_1 + SLOTS_POLLEN_COUNT; i++) {
			if(inventory.getStackInSlot(i) == null)
				return true;
		}
		
		return false;
	}
	
	private void storePollenStack(ItemStack itemstack) {
		for(int i = SLOT_POLLEN_1; i < SLOT_POLLEN_1 + SLOTS_POLLEN_COUNT; i++) {
			if(inventory.getStackInSlot(i) == null) {
				inventory.setInventorySlotContents(i, itemstack);
				return;
			}
		}
	}
	
	/* INVENTORY */
	public InventoryAdapter getInternalInventory() {
		return inventory;
	}

	/* ICRAFTER */
	@Override
	public boolean canTakeStack(int slotIndex) {
		return true;
	}

	@Override
	public ItemStack takenFromSlot(int slotIndex, boolean consumeRecipe, EntityPlayer player) {
		if(slotIndex == SLOT_SIEVE) {
			destroyPollen();
			return inventory.getStackInSlot(SLOT_SIEVE);
		} else {
			destroySieve();
			return inventory.getStackInSlot(slotIndex);
		}
	}

	@Override
	public ItemStack getResult() {
		return null;
	}

	/* IBEELISTENER */
	@Override public void onQueenChange(ItemStack queen) {}
	@Override public void wearOutEquipment(int amount) {}
	@Override public void onQueenDeath(IBee queen) {}
	@Override public void onPostQueenDeath(IBee queen) {}

	@Override
	public boolean onPollenRetrieved(IBee queen, IIndividual pollen, boolean isHandled) {

		if(isHandled)
			return isHandled;
		if(!canStorePollen())
			return false;
		
		storePollenStack(AlleleManager.alleleRegistry.getSpeciesRoot(pollen.getClass()).getMemberStack(pollen, EnumGermlingType.POLLEN.ordinal()));
		return true;
	}

	@Override
	public boolean onEggLaid(IBee queen) {
		return false;
	}
}
