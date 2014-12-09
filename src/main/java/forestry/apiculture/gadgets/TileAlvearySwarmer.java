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

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.ForestryAPI;
import forestry.api.core.ISpecialInventory;
import forestry.apiculture.worldgen.HiveDecorator;
import forestry.apiculture.worldgen.HiveSwarmer;
import forestry.core.network.GuiId;
import forestry.core.network.PacketPayload;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.core.utils.TileInventoryAdapter;
import forestry.plugins.PluginApiculture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Map.Entry;
import java.util.Stack;

public class TileAlvearySwarmer extends TileAlveary implements ISpecialInventory {

	/* CONSTANTS */
	public static final int BLOCK_META = 2;

	TileInventoryAdapter swarmerInventory;
	private final Stack<ItemStack> pendingSpawns = new Stack<ItemStack>();
	private boolean isActive;

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

		if (pendingSpawns.size() > 0) {
			setIsActive(true);
			if (worldObj.getTotalWorldTime() % 1000 == 0)
				trySpawnSwarm();
		} else {
			setIsActive(false);
		}

		if (worldObj.getTotalWorldTime() % 500 != 0)
			return;

		ItemStack princessStack = getPrincessStack();
		if (princessStack == null)
			return;

		int chance = consumeInducerAndGetChance();
		if (chance == 0)
			return;

		// Try to spawn princess
		if (worldObj.rand.nextInt(1000) >= chance)
			return;

		// Queue swarm spawn
		IBee princess = PluginApiculture.beeInterface.getMember(princessStack);
		princess.setIsNatural(false);
		pendingSpawns.push(PluginApiculture.beeInterface.getMemberStack(princess, EnumBeeType.PRINCESS.ordinal()));
	}

	private ItemStack getPrincessStack() {
		if (!this.hasMaster())
			return null;

		IAlvearyComponent master = (IAlvearyComponent) this.getCentralTE();
		if (!(master instanceof IBeeHousing))
			return null;

		IBeeHousing housing = (IBeeHousing) master;
		ItemStack princessStack = housing.getQueen();
		if (princessStack == null || !PluginApiculture.beeInterface.isMated(princessStack))
			return null;

		return princessStack;
	}

	private int consumeInducerAndGetChance() {
		if (swarmerInventory == null)
			return 0;

		for (int i = 0; i < swarmerInventory.getSizeInventory(); i++) {
			ItemStack stack = swarmerInventory.getStackInSlot(i);
			for (Entry<ItemStack, Integer> entry : BeeManager.inducers.entrySet()) {
				if (StackUtils.isIdenticalItem(entry.getKey(), stack)) {
					swarmerInventory.decrStackSize(i, 1);
					return entry.getValue();
				}
			}
		}

		return 0;
	}

	private void trySpawnSwarm() {

		ItemStack toSpawn = pendingSpawns.peek();
		HiveSwarmer hive = new HiveSwarmer(128, toSpawn);

		int chunkX = (xCoord + worldObj.rand.nextInt(40 * 2) - 40) / 16;
		int chunkZ = (zCoord + worldObj.rand.nextInt(40 * 2) - 40) / 16;

		if (HiveDecorator.instance().genHive(worldObj, worldObj.rand, chunkX, chunkZ, hive)) {
			pendingSpawns.pop();
		}
	}

	private void setIsActive(boolean isActive) {
		if (this.isActive != isActive) {
			this.isActive = isActive;
			sendNetworkUpdate();
		}
	}

	/* NETWORK */
	@Override
	public void fromPacketPayload(PacketPayload payload) {
		boolean isActive = payload.shortPayload[0] > 0;
		if (this.isActive != isActive) {
			this.isActive = isActive;
			worldObj.func_147479_m(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public PacketPayload getPacketPayload() {
		PacketPayload payload = new PacketPayload(0, 1);
		payload.shortPayload[0] = (short) (isActive ? 1 : 0);
		return payload;
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

		if (isActive)
			return BlockAlveary.ALVEARY_SWARMER_ON;
		else
			return BlockAlveary.ALVEARY_SWARMER_OFF;
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
