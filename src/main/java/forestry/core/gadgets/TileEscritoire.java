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
import forestry.core.inventory.InvTools;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.ForestryPacket;
import forestry.core.network.GuiId;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketTileNBT;
import forestry.core.network.PacketTileUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.GuiUtil;

public class TileEscritoire extends TileBase implements ISidedInventory, IRenderableMachine, ICrafter {

	public static final short SLOT_ANALYZE = 0;
	public static final short SLOT_RESULTS_1 = 1;
	public static final short SLOTS_RESULTS_COUNT = 6;
	public static final short SLOT_INPUT_1 = 7;
	public static final short SLOTS_INPUT_COUNT = 5;

	/* MEMBER */
	private final NaturalistGame game = new NaturalistGame();

	public TileEscritoire() {
		setInternalInventory(new TileInventoryAdapter(this, 12, "Items") {
			@Override
			public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
				if (slotIndex >= SLOT_INPUT_1 && slotIndex < SLOT_INPUT_1 + Math.min(game.getSampleSize(), SLOTS_INPUT_COUNT)) {
					ItemStack specimen = getStackInSlot(SLOT_ANALYZE);
					if (specimen == null)
						return false;
					IIndividual individual = AlleleManager.alleleRegistry.getIndividual(specimen);
					return individual != null && individual.getGenome().getPrimary().getResearchSuitability(itemStack) > 0;
				}

				if (slotIndex == SLOT_ANALYZE) {
					return AlleleManager.alleleRegistry.isIndividual(itemStack);
				}

				return false;
			}

			@Override
			public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
				return GuiUtil.isIndexInRange(slotIndex, SLOT_RESULTS_1, SLOTS_RESULTS_COUNT);
			}

			@Override
			public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
				super.setInventorySlotContents(slotIndex, itemstack);
				if (slotIndex == SLOT_ANALYZE && Proxies.common.isSimulating(worldObj)) {
					if (!AlleleManager.alleleRegistry.isIndividual(getStackInSlot(SLOT_ANALYZE)) && getStackInSlot(SLOT_ANALYZE) != null) {
						ItemStack ersatz = GeneticsUtil.convertSaplingToGeneticEquivalent(getStackInSlot(SLOT_ANALYZE));
						if (ersatz != null)
							setInventorySlotContents(SLOT_ANALYZE, ersatz);
					}
					game.initialize(getStackInSlot(SLOT_ANALYZE));
				}
			}
		});
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
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		game.readFromNBT(nbttagcompound);
	}

	/* GAME */
	public NaturalistGame getGame() {
		return game;
	}

	public void processTurnResult(GameProfile gameProfile) {
		if (!game.isWon())
			return;

		IIndividual individual = AlleleManager.alleleRegistry.getIndividual(getInternalInventory().getStackInSlot(SLOT_ANALYZE));
		if (individual == null)
			return;

		for (ItemStack itemstack : individual.getGenome().getPrimary().getResearchBounty(worldObj, gameProfile, individual, game.getBountyLevel())) {
			InvTools.addStack(getInternalInventory(), itemstack, SLOT_RESULTS_1, SLOTS_RESULTS_COUNT, false, true);
		}
	}

	private boolean areProbeSlotsFilled() {
		int filledSlots = 0;
		int required = game.getSampleSize() < SLOTS_INPUT_COUNT ? game.getSampleSize() : SLOTS_INPUT_COUNT;
		for (int i = SLOT_INPUT_1; i < SLOT_INPUT_1 + required; i++) {
			if (getInternalInventory().getStackInSlot(i) != null)
				filledSlots++;
		}

		return filledSlots >= required;
	}

	public void probe() {
		if (!worldObj.isRemote && getInternalInventory().getStackInSlot(SLOT_ANALYZE) != null && areProbeSlotsFilled())
			game.probe(getInternalInventory().getStackInSlot(SLOT_ANALYZE), this, SLOT_INPUT_1, SLOTS_INPUT_COUNT);
	}

	/* NETWORK */
	@Override
	public void fromPacket(ForestryPacket packetRaw) {
		if (packetRaw instanceof PacketTileUpdate) {
			super.fromPacket(packetRaw);
			return;
		}

		PacketTileNBT packet = (PacketTileNBT) packetRaw;
		readFromNBT(packet.getTagCompound());
	}

	public void sendBoard(EntityPlayer player) {
		Proxies.net.sendToPlayer(new PacketTileNBT(PacketIds.TILE_NBT, this), player);
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
		if (slotIndex == SLOT_ANALYZE)
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
