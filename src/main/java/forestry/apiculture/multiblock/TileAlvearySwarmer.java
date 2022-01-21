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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

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
import forestry.core.utils.WorldUtils;

public class TileAlvearySwarmer extends TileAlveary implements WorldlyContainer, IActivatable, IAlvearyComponent.Active {

	private final InventorySwarmer inventory;
	private final Stack<ItemStack> pendingSpawns = new Stack<>();
	private boolean active;

	public TileAlvearySwarmer(BlockPos pos, BlockState state) {
		super(BlockAlvearyType.SWARMER, pos, state);
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
		if (level.random.nextInt(1000) >= chance) {
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
		for (int slotIndex = 0; slotIndex < getContainerSize(); slotIndex++) {
			ItemStack stack = getItem(slotIndex);
			for (Entry<ItemStack, Integer> entry : BeeManager.inducers.entrySet()) {
				if (ItemStackUtil.isIdenticalItem(entry.getKey(), stack)) {
					removeItem(slotIndex, 1);
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

		int x = getBlockPos().getX() + level.random.nextInt(40 * 2) - 40;
		int z = getBlockPos().getZ() + level.random.nextInt(40 * 2) - 40;

		if (HiveDecorator.tryGenHive(WorldUtils.asServer(level), level.random, x, z, hive)) {
			pendingSpawns.pop();
		}
	}

	/* NETWORK */

	@Override
	protected void encodeDescriptionPacket(CompoundTag packetData) {
		super.encodeDescriptionPacket(packetData);
		packetData.putBoolean("Active", active);
	}

	@Override
	protected void decodeDescriptionPacket(CompoundTag packetData) {
		super.decodeDescriptionPacket(packetData);
		setActive(packetData.getBoolean("Active"));
	}

	/* SAVING & LOADING */
	@Override
	public void load(BlockState state, CompoundTag compoundNBT) {
		super.load(state, compoundNBT);
		setActive(compoundNBT.getBoolean("Active"));

		ListTag nbttaglist = compoundNBT.getList("PendingSpawns", 10);
		for (int i = 0; i < nbttaglist.size(); i++) {
			CompoundTag compoundNBT1 = nbttaglist.getCompound(i);
			pendingSpawns.add(ItemStack.of(compoundNBT1));
		}
	}


	@Override
	public CompoundTag save(CompoundTag compoundNBT) {
		compoundNBT = super.save(compoundNBT);
		compoundNBT.putBoolean("Active", active);

		ListTag nbttaglist = new ListTag();
		ItemStack[] offspring = pendingSpawns.toArray(new ItemStack[0]);
		for (int i = 0; i < offspring.length; i++) {
			if (offspring[i] != null) {
				CompoundTag compoundNBT1 = new CompoundTag();
				compoundNBT1.putByte("Slot", (byte) i);
				offspring[i].save(compoundNBT1);
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

		if (level != null && !level.isClientSide) {
			NetworkUtil.sendNetworkPacket(new PacketActiveUpdate(this), worldPosition, level);
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerAlvearySwarmer(windowId, inv, this);
	}
}
