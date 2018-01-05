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

import javax.annotation.Nullable;
import java.util.Map.Entry;
import java.util.Stack;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.apiculture.blocks.BlockAlvearyType;
import forestry.apiculture.gui.ContainerAlvearySwarmer;
import forestry.apiculture.gui.GuiAlvearySwarmer;
import forestry.apiculture.inventory.InventorySwarmer;
import forestry.apiculture.worldgen.Hive;
import forestry.apiculture.worldgen.HiveDecorator;
import forestry.apiculture.worldgen.HiveDescriptionSwarmer;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.packets.PacketActiveUpdate;
import forestry.core.tiles.IActivatable;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;

public class TileAlvearySwarmer extends TileAlveary implements ISidedInventory, IActivatable, IAlvearyComponent.Active {

	private final InventorySwarmer inventory;
	private final Stack<ItemStack> pendingSpawns = new Stack<>();
	private boolean active;

	public TileAlvearySwarmer() {
		super(BlockAlvearyType.SWARMER);
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
		if (!pendingSpawns.isEmpty()) {
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
		if (world.rand.nextInt(1000) >= chance) {
			return;
		}

		// Queue swarm spawn
		IBee princess = BeeManager.beeRoot.getMember(princessStack);
		princess.setIsNatural(false);
		pendingSpawns.push(BeeManager.beeRoot.getMemberStack(princess, EnumBeeType.PRINCESS));
	}

	@Override
	public void updateClient(int tickCount) {

	}

	@Nullable
	private ItemStack getPrincessStack() {
		ItemStack princessStack = getMultiblockLogic().getController().getBeeInventory().getQueen();

		if (BeeManager.beeRoot.isMated(princessStack)) {
			return princessStack;
		}

		return null;
	}

	private int consumeInducerAndGetChance() {
		for (int slotIndex = 0; slotIndex < getSizeInventory(); slotIndex++) {
			ItemStack stack = getStackInSlot(slotIndex);
			for (Entry<ItemStack, Integer> entry : BeeManager.inducers.entrySet()) {
				if (ItemStackUtil.isIdenticalItem(entry.getKey(), stack)) {
					decrStackSize(slotIndex, 1);
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

		int x = getPos().getX() + world.rand.nextInt(40 * 2) - 40;
		int z = getPos().getZ() + world.rand.nextInt(40 * 2) - 40;

		if (HiveDecorator.tryGenHive(world, world.rand, x, z, hive)) {
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

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		setActive(nbttagcompound.getBoolean("Active"));

		NBTTagList nbttaglist = nbttagcompound.getTagList("PendingSpawns", 10);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			pendingSpawns.add(new ItemStack(nbttagcompound1));
		}
	}


	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
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
		return nbttagcompound;
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

		if (world != null && !world.isRemote) {
			NetworkUtil.sendNetworkPacket(new PacketActiveUpdate(this), pos, world);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, int data) {
		return new GuiAlvearySwarmer(player.inventory, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
		return new ContainerAlvearySwarmer(player.inventory, this);
	}
}
