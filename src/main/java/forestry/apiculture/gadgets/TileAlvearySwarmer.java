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

import java.util.Map.Entry;
import java.util.Stack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.ForestryAPI;
import forestry.api.core.ISpecialInventory;
import forestry.apiculture.worldgen.WorldGenHive;
import forestry.apiculture.worldgen.WorldGenHiveSwamer;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.TileInventoryAdapter;
import forestry.plugins.PluginApiculture;

public class TileAlvearySwarmer extends TileAlveary implements ISpecialInventory {

	/* CONSTANTS */
	public static final int BLOCK_META = 2;

	TileInventoryAdapter swarmerInventory;
	private final Stack<ItemStack> pendingSpawns = new Stack<ItemStack>();

	public TileAlvearySwarmer() {
		super(BLOCK_META);
	}

	@Override
	public void initialize() {
		super.initialize();
		if (swarmerInventory == null)
			createInventory();
	}

	@Override
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, GuiId.AlvearySwarmerGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
	}

	/* UPDATING */
	@Override
	protected void updateServerSide() {
		super.updateServerSide();

		if (worldObj.getTotalWorldTime() % 100 != 0)
			if (pendingSpawns.size() > 0)
				trySpawnSwarm();

		if (worldObj.getTotalWorldTime() % 500 != 0)
			return;

		if (!this.hasMaster())
			return;

		IAlvearyComponent master = (IAlvearyComponent) this.getCentralTE();
		if (!(master instanceof IBeeHousing))
			return;

		IBeeHousing housing = (IBeeHousing) master;
		ItemStack queenstack = housing.getQueen();
		if (queenstack == null)
			return;
		if (!PluginApiculture.beeInterface.isMated(queenstack))
			return;

		// Calculate chance
		int slot = getInducerSlot();
		if (slot < 0)
			return;
		int chance = getChanceFor(swarmerInventory.getStackInSlot(slot));

		// Remove resource
		swarmerInventory.decrStackSize(slot, 1);

		// Try to spawn princess
		if (worldObj.rand.nextInt(1000) >= chance)
			return;

		// Queue swarm spawn
		IBee spawn = PluginApiculture.beeInterface.getMember(queenstack);
		spawn.setIsNatural(false);
		pendingSpawns.push(PluginApiculture.beeInterface.getMemberStack(spawn, EnumBeeType.PRINCESS.ordinal()));

	}

	private int getChanceFor(ItemStack stack) {
		for (Entry<ItemStack, Integer> entry : BeeManager.inducers.entrySet()) {
			if (entry.getKey().isItemEqual(stack))
				return entry.getValue();
		}

		return 0;
	}

	private int getInducerSlot() {
		for (int i = 0; i < swarmerInventory.getSizeInventory(); i++) {
			if (swarmerInventory.getStackInSlot(i) == null)
				continue;
			for (Entry<ItemStack, Integer> entry : BeeManager.inducers.entrySet()) {
				if (entry.getKey().isItemEqual(swarmerInventory.getStackInSlot(i)))
					return i;
			}
		}

		return -1;
	}

	private void trySpawnSwarm() {

		ItemStack toSpawn = pendingSpawns.peek();
		WorldGenHive generator = new WorldGenHiveSwamer(new ItemStack[] { toSpawn });

		int i = 0;
		while (i < 10) {
			i++;
			int spawnX = xCoord + worldObj.rand.nextInt(40 * 2) - 40;
			int spawnY = yCoord + worldObj.rand.nextInt(40);
			int spawnZ = zCoord + worldObj.rand.nextInt(40 * 2) - 40;
			if (generator.generate(worldObj, worldObj.rand, spawnX, spawnY, spawnZ)) {
				pendingSpawns.pop();
				break;
			}
		}

	}

	@Override
	public boolean hasFunction() {
		return true;
	}

	/* TEXTURES */
	@Override
	public int getIcon(int side, int metadata) {
		if(side == 0 || side == 1)
			return BlockAlveary.BOTTOM;

		if (pendingSpawns.size() > 0)
			return BlockAlveary.TX_56_SWON;
		else
			return BlockAlveary.TX_55_SWOF;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		if (swarmerInventory == null)
			createInventory();
		swarmerInventory.readFromNBT(nbttagcompound);

		NBTTagList nbttaglist = nbttagcompound.getTagList("PendingSpawns", 10);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			pendingSpawns.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		if (swarmerInventory != null)
			swarmerInventory.writeToNBT(nbttagcompound);

		NBTTagList nbttaglist = new NBTTagList();
		ItemStack[] offspring = pendingSpawns.toArray(new ItemStack[pendingSpawns.size()]);
		for (int i = 0; i < offspring.length; i++)
			if (offspring[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				offspring[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		nbttagcompound.setTag("PendingSpawns", nbttaglist);

	}

	@Override
	protected void createInventory() {
		swarmerInventory = new TileInventoryAdapter(this, 4, "SwarmInv");
	}

	@Override
	public IInventory getInventory() {
		return swarmerInventory;
	}

	/* ISPECIALINVENTORY */
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		if (swarmerInventory != null)
			return swarmerInventory.addStack(stack, false, doAdd);
		else
			return 0;
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		return null;
	}

	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		if (swarmerInventory != null)
			return swarmerInventory.getSizeInventory();
		else
			return 0;
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		if (swarmerInventory != null)
			return swarmerInventory.getStackInSlot(slotIndex);
		else
			return null;
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		if (swarmerInventory != null)
			return swarmerInventory.decrStackSize(slotIndex, amount);
		else
			return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		if (swarmerInventory != null)
			return swarmerInventory.getStackInSlotOnClosing(slotIndex);
		else
			return null;
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		// Client side handling for container synch
		if (swarmerInventory == null && !Proxies.common.isSimulating(worldObj))
			createInventory();

		if (swarmerInventory != null)
			swarmerInventory.setInventorySlotContents(slotIndex, itemstack);
	}

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
	}

	@Override
	public int getInventoryStackLimit() {
		if (swarmerInventory != null)
			return swarmerInventory.getInventoryStackLimit();
		else
			return 0;
	}

	@Override public void openInventory() {}
	@Override public void closeInventory() {}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return super.isUseableByPlayer(player);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean hasCustomInventoryName() {
		return super.hasCustomInventoryName();
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		return super.isItemValidForSlot(slotIndex, itemstack);
	}
}
