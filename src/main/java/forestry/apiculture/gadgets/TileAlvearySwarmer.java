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

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Stack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlvearyComponent;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.ForestryAPI;
import forestry.apiculture.network.PacketActiveUpdate;
import forestry.apiculture.worldgen.Hive;
import forestry.apiculture.worldgen.HiveDecorator;
import forestry.apiculture.worldgen.HiveDescriptionSwarmer;
import forestry.core.interfaces.IActivatable;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.inventory.wrappers.IInvSlot;
import forestry.core.inventory.wrappers.InventoryIterator;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;

public class TileAlvearySwarmer extends TileAlveary implements ISidedInventory, IActivatable {

	/* CONSTANTS */
	public static final int BLOCK_META = 2;

	private final Stack<ItemStack> pendingSpawns = new Stack<ItemStack>();
	private boolean active;

	public TileAlvearySwarmer() {
		super(BLOCK_META);
		setInternalInventory(new SwarmerInventoryAdapter(this));
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
			setActive(true);
			if (updateOnInterval(1000)) {
				trySpawnSwarm();
			}
		} else {
			setActive(false);
		}

		if (!updateOnInterval(500)) {
			return;
		}

		ItemStack princessStack = getPrincessStack();
		if (princessStack == null) {
			return;
		}

		int chance = consumeInducerAndGetChance();
		if (chance == 0) {
			return;
		}

		// Try to spawn princess
		if (worldObj.rand.nextInt(1000) >= chance) {
			return;
		}

		// Queue swarm spawn
		IBee princess = BeeManager.beeRoot.getMember(princessStack);
		princess.setIsNatural(false);
		pendingSpawns.push(BeeManager.beeRoot.getMemberStack(princess, EnumBeeType.PRINCESS.ordinal()));
	}

	private ItemStack getPrincessStack() {
		if (!this.hasMaster()) {
			return null;
		}

		IAlvearyComponent master = (IAlvearyComponent) this.getCentralTE();
		if (!(master instanceof IBeeHousing)) {
			return null;
		}

		IBeeHousing housing = (IBeeHousing) master;
		ItemStack princessStack = housing.getQueen();
		if (princessStack == null || !BeeManager.beeRoot.isMated(princessStack)) {
			return null;
		}

		return princessStack;
	}

	private int consumeInducerAndGetChance() {
		if (getInternalInventory() == null) {
			return 0;
		}

		for (IInvSlot slot : InventoryIterator.getIterable(getInternalInventory())) {
			ItemStack stack = slot.getStackInSlot();
			for (Entry<ItemStack, Integer> entry : BeeManager.inducers.entrySet()) {
				if (StackUtils.isIdenticalItem(entry.getKey(), stack)) {
					slot.decreaseStackInSlot();
					return entry.getValue();
				}
			}
		}

		return 0;
	}

	private void trySpawnSwarm() {

		ItemStack toSpawn = pendingSpawns.peek();
		HiveDescriptionSwarmer hiveDescription = new HiveDescriptionSwarmer(toSpawn);
		Hive hive = new Hive(hiveDescription);

		int chunkX = (xCoord + worldObj.rand.nextInt(40 * 2) - 40) / 16;
		int chunkZ = (zCoord + worldObj.rand.nextInt(40 * 2) - 40) / 16;

		if (HiveDecorator.instance().genHive(worldObj, worldObj.rand, chunkX, chunkZ, hive)) {
			pendingSpawns.pop();
		}
	}

	/* NETWORK */

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeBoolean(active);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		boolean active = data.readBoolean();
		if (this.active != active) {
			this.active = active;
			worldObj.func_147479_m(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public boolean hasFunction() {
		return true;
	}

	/* TEXTURES */
	@Override
	public int getIcon(int side, int metadata) {
		if (side == 0 || side == 1) {
			return BlockAlveary.BOTTOM;
		}

		if (active) {
			return BlockAlveary.ALVEARY_SWARMER_ON;
		} else {
			return BlockAlveary.ALVEARY_SWARMER_OFF;
		}
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		NBTTagList nbttaglist = nbttagcompound.getTagList("PendingSpawns", 10);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			pendingSpawns.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		NBTTagList nbttaglist = new NBTTagList();
		ItemStack[] offspring = pendingSpawns.toArray(new ItemStack[pendingSpawns.size()]);
		for (int i = 0; i < offspring.length; i++) {
			if (offspring[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				offspring[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbttagcompound.setTag("PendingSpawns", nbttaglist);

	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		if (this.active == active) {
			return;
		}

		this.active = active;

		if (!worldObj.isRemote) {
			Proxies.net.sendNetworkPacket(new PacketActiveUpdate(this));
		}
	}

	private static class SwarmerInventoryAdapter extends TileInventoryAdapter<TileAlvearySwarmer> {
		public SwarmerInventoryAdapter(TileAlvearySwarmer alvearySwarmer) {
			super(alvearySwarmer, 4, "SwarmInv");
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			return StackUtils.containsItemStack(BeeManager.inducers.keySet(), itemStack);
		}
	}
}
