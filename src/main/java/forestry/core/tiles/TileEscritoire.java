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
import forestry.core.inventory.InventoryEscritoire;
import forestry.core.inventory.watchers.ISlotPickupWatcher;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.utils.InventoryUtil;

public class TileEscritoire extends TileBase implements ISidedInventory, ISlotPickupWatcher, IStreamableGui {

	private final EscritoireGame game = new EscritoireGame();

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

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		game.writeToNBT(nbttagcompound);
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

	/* ISlotPickupWatcher */
	@Override
	public void onPickupFromSlot(int slotIndex, EntityPlayer player) {
		if (slotIndex == InventoryEscritoire.SLOT_ANALYZE) {
			game.reset();
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
}
