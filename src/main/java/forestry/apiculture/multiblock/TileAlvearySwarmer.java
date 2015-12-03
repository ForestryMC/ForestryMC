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
package forestry.apiculture.multiblock;

import java.util.Map.Entry;
import java.util.Stack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.apiculture.blocks.BlockAlveary;
import forestry.apiculture.gui.ContainerAlvearySwarmer;
import forestry.apiculture.gui.GuiAlvearySwarmer;
import forestry.apiculture.inventory.InventorySwarmer;
import forestry.apiculture.network.packets.PacketActiveUpdate;
import forestry.apiculture.worldgen.Hive;
import forestry.apiculture.worldgen.HiveDecorator;
import forestry.apiculture.worldgen.HiveDescriptionSwarmer;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.inventory.wrappers.IInvSlot;
import forestry.core.inventory.wrappers.InventoryIterator;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.IActivatable;
import forestry.core.utils.ItemStackUtil;

public class TileAlvearySwarmer extends TileAlveary implements ISidedInventory, IActivatable, IAlvearyComponent.Active {

	private final InventorySwarmer inventory;
	private final Stack<ItemStack> pendingSpawns = new Stack<>();
	private boolean active;

	public TileAlvearySwarmer() {
		super(BlockAlveary.Type.SWARMER);
		this.inventory = new InventorySwarmer(this);
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return inventory;
	}

	@Override
	public boolean allowsAutomation() {
		return true;
	}

	/* UPDATING */
	@Override
	public void updateServer(int tickCount) {
		if (pendingSpawns.size() > 0) {
			setActive(true);
			if (tickCount % 1000 == 0) {
				trySpawnSwarm();
			}
		} else {
			setActive(false);
		}

		if (tickCount % 500 != 0) {
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

	@Override
	public void updateClient(int tickCount) {

	}

	private ItemStack getPrincessStack() {
		ItemStack princessStack = getMultiblockLogic().getController().getBeeInventory().getQueen();

		if (BeeManager.beeRoot.isMated(princessStack)) {
			return princessStack;
		}

		return null;
	}

	private int consumeInducerAndGetChance() {
		if (getInternalInventory() == null) {
			return 0;
		}

		for (IInvSlot slot : InventoryIterator.getIterable(getInternalInventory())) {
			ItemStack stack = slot.getStackInSlot();
			for (Entry<ItemStack, Integer> entry : BeeManager.inducers.entrySet()) {
				if (ItemStackUtil.isIdenticalItem(entry.getKey(), stack)) {
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

		if (HiveDecorator.genHive(worldObj, worldObj.rand, chunkX, chunkZ, hive)) {
			pendingSpawns.pop();
		}
	}

	/* NETWORK */

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

	/* TEXTURES */
	@Override
	public int getIcon(int side) {
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
		setActive(nbttagcompound.getBoolean("Active"));

		NBTTagList nbttaglist = nbttagcompound.getTagList("PendingSpawns", 10);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			pendingSpawns.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setBoolean("Active", active);

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
			Proxies.net.sendNetworkPacket(new PacketActiveUpdate(this), worldObj);
		}
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiAlvearySwarmer(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerAlvearySwarmer(player.inventory, this);
	}
}
