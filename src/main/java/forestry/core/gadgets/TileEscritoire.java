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
package forestry.core.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.mojang.authlib.GameProfile;

import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.core.interfaces.ICrafter;
import forestry.core.interfaces.IRenderableMachine;
import forestry.core.network.ForestryPacket;
import forestry.core.network.GuiId;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketTileNBT;
import forestry.core.network.PacketTileUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.StringUtil;
import forestry.core.utils.TileInventoryAdapter;
import forestry.core.utils.Utils;

public class TileEscritoire extends TileBase implements ISidedInventory, IRenderableMachine, ICrafter {

	public static final short SLOT_ANALYZE = 0;
	public static final short SLOT_RESULTS_1 = 1;
	public static final short SLOTS_RESULTS_COUNT = 6;
	public static final short SLOT_INPUT_1 = 7;
	public static final short SLOTS_INPUT_COUNT = 5;

	/* MEMBER */
	private final NaturalistGame game = new NaturalistGame();
	private final InventoryAdapter inventory;
	@Override
	public String getInventoryName() {
		return StringUtil.localize("core.1");
	}

	public TileEscritoire() {
		inventory = new TileInventoryAdapter(this, 12, "Items");
	}

	/* GUI */
	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.NaturalistBenchGUI.ordinal(), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		game.writeToNBT(nbttagcompound);
		inventory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		game.readFromNBT(nbttagcompound);
		inventory.readFromNBT(nbttagcompound);
	}

	/* GAME */
	public NaturalistGame getGame() {
		return game;
	}

	public void processTurnResult(GameProfile gameProfile) {
		if(!game.isWon())
			return;

		IIndividual individual = AlleleManager.alleleRegistry.getIndividual(inventory.getStackInSlot(SLOT_ANALYZE));
		if(individual == null)
			return;

		for(ItemStack itemstack : individual.getGenome().getPrimary().getResearchBounty(worldObj, gameProfile, individual, game.getBountyLevel()))
			inventory.addStack(itemstack, SLOT_RESULTS_1, SLOTS_RESULTS_COUNT, false, true);
	}

	private boolean areProbeSlotsFilled() {
		int filledSlots = 0;
		int required = game.getSampleSize() < SLOTS_INPUT_COUNT ? game.getSampleSize() : SLOTS_INPUT_COUNT;
		for(int i = SLOT_INPUT_1; i < SLOT_INPUT_1 + required; i++)
			if(inventory.getStackInSlot(i) != null)
				filledSlots++;

		return filledSlots >= required;
	}

	public void probe() {
		if (!worldObj.isRemote && inventory.getStackInSlot(SLOT_ANALYZE) != null && areProbeSlotsFilled()) {
			game.probe(inventory.getStackInSlot(SLOT_ANALYZE), this, SLOT_INPUT_1, SLOTS_INPUT_COUNT);
		}
	}

	/* NETWORK */
	@Override
	public void fromPacket(ForestryPacket packetRaw) {
		if(packetRaw instanceof PacketTileUpdate) {
			super.fromPacket(packetRaw);
			return;
		}

		PacketTileNBT packet = (PacketTileNBT)packetRaw;
		readFromNBT(packet.getTagCompound());
	}

	public void sendBoard(EntityPlayer player) {
		Proxies.net.sendToPlayer(new PacketTileNBT(PacketIds.TILE_NBT, this), player);
	}

	/* ISIDEDINVENTORY */
	@Override
	public InventoryAdapter getInternalInventory() {
		return inventory;
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		if(slotIndex >= SLOT_INPUT_1 && slotIndex < SLOT_INPUT_1 + SLOTS_INPUT_COUNT)
			return slotIndex < SLOT_INPUT_1 + game.getSampleSize();

		return true;
	}

	@Override
	protected boolean canTakeStackFromSide(int slotIndex, ItemStack itemstack, int side) {
		if(!super.canTakeStackFromSide(slotIndex, itemstack, side))
			return false;

		return slotIndex >= SLOT_RESULTS_1 && slotIndex < SLOT_RESULTS_1 + SLOTS_RESULTS_COUNT;
	}

	@Override
	protected boolean canPutStackFromSide(int slotIndex, ItemStack itemstack, int side) {

		if(!super.canPutStackFromSide(slotIndex, itemstack, side))
			return false;

		int samples = game.getSampleSize();
		return slotIndex >= SLOT_INPUT_1 && slotIndex < SLOT_INPUT_1 + (samples < SLOTS_INPUT_COUNT ? samples : SLOTS_INPUT_COUNT);
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		inventory.setInventorySlotContents(slotIndex, itemstack);
		if(slotIndex == SLOT_ANALYZE && Proxies.common.isSimulating(worldObj)) {
			if(!AlleleManager.alleleRegistry.isIndividual(inventory.getStackInSlot(SLOT_ANALYZE))
					&& inventory.getStackInSlot(SLOT_ANALYZE) != null) {
				ItemStack ersatz = Utils.convertSaplingToGeneticEquivalent(inventory.getStackInSlot(SLOT_ANALYZE));
				if(ersatz != null)
					inventory.setInventorySlotContents(SLOT_ANALYZE, ersatz);
			}
			game.initialize(inventory.getStackInSlot(SLOT_ANALYZE));
		}
	}

	@Override public int getSizeInventory() { return inventory.getSizeInventory(); }
	@Override public ItemStack getStackInSlot(int i) { return inventory.getStackInSlot(i); }
	@Override public ItemStack decrStackSize(int i, int j) { return inventory.decrStackSize(i, j); }
	@Override public int getInventoryStackLimit() { return inventory.getInventoryStackLimit(); }
	@Override public ItemStack getStackInSlotOnClosing(int slot) { return inventory.getStackInSlotOnClosing(slot); }
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
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return super.canInsertItem(i, itemstack, j);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return super.canExtractItem(i, itemstack, j);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return super.getAccessibleSlotsFromSide(side);
	}

	@Override
	public EnumTankLevel getPrimaryLevel() {
		return EnumTankLevel.EMPTY;
	}

	@Override
	public EnumTankLevel getSecondaryLevel() {
		return EnumTankLevel.EMPTY;
	}

	@Override
	public ItemStack takenFromSlot(int slotIndex, boolean consumeRecipe, EntityPlayer player) {
		if(slotIndex == SLOT_ANALYZE)
			game.reset();
		return null;
	}

	@Override
	public ItemStack getResult() {
		return null;
	}

	@Override
	public boolean canTakeStack(int slotIndex) {
		return true;
	}


}
