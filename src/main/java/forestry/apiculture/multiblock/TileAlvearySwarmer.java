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
import java.util.Optional;
import java.util.Stack;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.apiculture.blocks.BlockAlvearyType;
import forestry.apiculture.gui.ContainerAlvearySwarmer;
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
		Optional<IBee> optionalPrincess = BeeManager.beeRoot.create(princessStack);
		if (!optionalPrincess.isPresent()) {
			return;
		}
		IBee princess = optionalPrincess.get();
		princess.setIsNatural(false);
		pendingSpawns.push(BeeManager.beeRoot.getTypes().createStack(princess, EnumBeeType.PRINCESS));
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
	protected void encodeDescriptionPacket(CompoundNBT packetData) {
		super.encodeDescriptionPacket(packetData);
		packetData.putBoolean("Active", active);
	}

	@Override
	protected void decodeDescriptionPacket(CompoundNBT packetData) {
		super.decodeDescriptionPacket(packetData);
		setActive(packetData.getBoolean("Active"));
	}

	/* SAVING & LOADING */
	@Override
	public void read(CompoundNBT compoundNBT) {
		super.read(compoundNBT);
		setActive(compoundNBT.getBoolean("Active"));

		ListNBT nbttaglist = compoundNBT.getList("PendingSpawns", 10);
		for (int i = 0; i < nbttaglist.size(); i++) {
			CompoundNBT compoundNBT1 = nbttaglist.getCompound(i);
			pendingSpawns.add(ItemStack.read(compoundNBT1));
		}
	}


	@Override
	public CompoundNBT write(CompoundNBT compoundNBT) {
		compoundNBT = super.write(compoundNBT);
		compoundNBT.putBoolean("Active", active);

		ListNBT nbttaglist = new ListNBT();
		ItemStack[] offspring = pendingSpawns.toArray(new ItemStack[0]);
		for (int i = 0; i < offspring.length; i++) {
			if (offspring[i] != null) {
				CompoundNBT compoundNBT1 = new CompoundNBT();
				compoundNBT1.putByte("Slot", (byte) i);
				offspring[i].write(compoundNBT1);
				nbttaglist.add(compoundNBT1);
			}
		}
		compoundNBT.put("PendingSpawns", nbttaglist);
		return compoundNBT;
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
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
		return new ContainerAlvearySwarmer(windowId, inv, this);
	}
}
