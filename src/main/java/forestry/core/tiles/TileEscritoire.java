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
package forestry.core.tiles;

import javax.annotation.Nonnull;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.core.gui.ContainerEscritoire;
import forestry.core.gui.GuiEscritoire;
import forestry.core.inventory.InventoryAnalyzer;
import forestry.core.inventory.InventoryEscritoire;
import forestry.core.inventory.watchers.ISlotPickupWatcher;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.network.packets.PacketItemStackDisplay;
import forestry.core.proxy.Proxies;
import forestry.core.utils.InventoryUtil;

public class TileEscritoire extends TileBase implements ISidedInventory, ISlotPickupWatcher, IStreamableGui, IItemStackDisplay {

	private final EscritoireGame game = new EscritoireGame();
	private ItemStack individualOnDisplayClient;

	public TileEscritoire() {
		super("escritoire");
		setInternalInventory(new InventoryEscritoire(this));
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		game.readFromNBT(nbttagcompound);
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
		game.writeToNBT(nbttagcompound);
		return nbttagcompound;
	}

	/* GAME */
	public EscritoireGame getGame() {
		return game;
	}

	public void choose(GameProfile gameProfile, int index) {
		game.choose(index);
		processTurnResult(gameProfile);
	}

	private void processTurnResult(GameProfile gameProfile) {
		if (getGame().getStatus() != EscritoireGame.Status.SUCCESS) {
			return;
		}

		IIndividual individual = AlleleManager.alleleRegistry.getIndividual(getStackInSlot(InventoryEscritoire.SLOT_ANALYZE));
		if (individual == null) {
			return;
		}

		IAlleleSpecies species = individual.getGenome().getPrimary();
		for (ItemStack itemstack : species.getResearchBounty(worldObj, gameProfile, individual, game.getBountyLevel())) {
			InventoryUtil.addStack(getInternalInventory(), itemstack, InventoryEscritoire.SLOT_RESULTS_1, InventoryEscritoire.SLOTS_RESULTS_COUNT, true);
		}
	}

	private boolean areProbeSlotsFilled() {
		int filledSlots = 0;
		int required = game.getSampleSize(InventoryEscritoire.SLOTS_INPUT_COUNT);
		for (int i = InventoryEscritoire.SLOT_INPUT_1; i < InventoryEscritoire.SLOT_INPUT_1 + required; i++) {
			if (getStackInSlot(i) != null) {
				filledSlots++;
			}
		}

		return filledSlots >= required;
	}

	public void probe() {
		if (worldObj.isRemote) {
			return;
		}

		ItemStack analyze = getStackInSlot(InventoryEscritoire.SLOT_ANALYZE);

		if (analyze != null && areProbeSlotsFilled()) {
			game.probe(analyze, this, InventoryEscritoire.SLOT_INPUT_1, InventoryEscritoire.SLOTS_INPUT_COUNT);
		}
	}

	/* NETWORK */
	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		game.writeData(data);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		game.readData(data);
	}
	
	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		ItemStack displayStack = getIndividualOnDisplay();
		data.writeItemStack(displayStack);
	}
	
	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		individualOnDisplayClient = data.readItemStack();
	}
	
	/* ISlotPickupWatcher */
	@Override
	public void onPickupFromSlot(int slotIndex, EntityPlayer player) {
		if (slotIndex == InventoryEscritoire.SLOT_ANALYZE) {
			game.reset();
			PacketItemStackDisplay packet = new PacketItemStackDisplay(this, getIndividualOnDisplay());
			Proxies.net.sendNetworkPacket(packet, worldObj);
		}
	}
	
	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		super.setInventorySlotContents(slotIndex, itemstack);
		if (slotIndex == InventoryEscritoire.SLOT_ANALYZE) {
			PacketItemStackDisplay packet = new PacketItemStackDisplay(this, getIndividualOnDisplay());
			Proxies.net.sendNetworkPacket(packet, worldObj);
		}
	}
	
	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiEscritoire(player, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerEscritoire(player, this);
	}
	
	@Override
	public void handleItemStackForDisplay(ItemStack itemStack) {
		if (!ItemStack.areItemStacksEqual(itemStack, individualOnDisplayClient)) {
			individualOnDisplayClient = itemStack;
			worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}
	
	public ItemStack getIndividualOnDisplay() {
		if (worldObj.isRemote) {
			return individualOnDisplayClient;
		}
		return getStackInSlot(InventoryAnalyzer.SLOT_ANALYZE);
	}
}
