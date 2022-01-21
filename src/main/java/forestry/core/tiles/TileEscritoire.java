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
import java.util.Optional;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.genetics.ForestryComponentKeys;
import forestry.api.genetics.IResearchHandler;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.core.features.CoreTiles;
import forestry.core.gui.ContainerEscritoire;
import forestry.core.inventory.InventoryAnalyzer;
import forestry.core.inventory.InventoryEscritoire;
import forestry.core.inventory.watchers.ISlotPickupWatcher;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.packets.PacketItemStackDisplay;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.NetworkUtil;

import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRoot;
import genetics.utils.RootUtils;

public class TileEscritoire extends TileBase implements WorldlyContainer, ISlotPickupWatcher, IStreamableGui, IItemStackDisplay {

	private final EscritoireGame game = new EscritoireGame();
	private ItemStack individualOnDisplayClient = ItemStack.EMPTY;

	public TileEscritoire() {
		super(CoreTiles.ESCRITOIRE.tileType());
		setInternalInventory(new InventoryEscritoire(this));
	}

	/* SAVING & LOADING */
	@Override
	public void load(BlockState state, CompoundTag compoundNBT) {
		super.load(state, compoundNBT);
		game.read(compoundNBT);
	}


	@Override
	public CompoundTag save(CompoundTag compoundNBT) {
		compoundNBT = super.save(compoundNBT);
		game.write(compoundNBT);
		return compoundNBT;
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

		Optional<IIndividual> optional = RootUtils.getIndividual(getItem(InventoryEscritoire.SLOT_ANALYZE));
		if (!optional.isPresent()) {
			return;
		}
		IIndividual individual = optional.get();

		IAlleleForestrySpecies species = individual.getGenome().getPrimary(IAlleleForestrySpecies.class);
		IIndividualRoot<IIndividual> root = (IIndividualRoot<IIndividual>) species.getRoot();
		IResearchHandler<IIndividual> handler = root.getComponent(ForestryComponentKeys.RESEARCH);
		for (ItemStack itemstack : handler.getResearchBounty(species, level, gameProfile, individual, game.getBountyLevel())) {
			InventoryUtil.addStack(getInternalInventory(), itemstack, InventoryEscritoire.SLOT_RESULTS_1, InventoryEscritoire.SLOTS_RESULTS_COUNT, true);
		}
	}

	private boolean areProbeSlotsFilled() {
		int filledSlots = 0;
		int required = game.getSampleSize(InventoryEscritoire.SLOTS_INPUT_COUNT);
		for (int i = InventoryEscritoire.SLOT_INPUT_1; i < InventoryEscritoire.SLOT_INPUT_1 + required; i++) {
			if (!getItem(i).isEmpty()) {
				filledSlots++;
			}
		}

		return filledSlots >= required;
	}

	public void probe() {
		if (level.isClientSide) {
			return;
		}

		ItemStack analyze = getItem(InventoryEscritoire.SLOT_ANALYZE);

		if (!analyze.isEmpty() && areProbeSlotsFilled()) {
			game.probe(analyze, this, InventoryEscritoire.SLOT_INPUT_1, InventoryEscritoire.SLOTS_INPUT_COUNT);
		}
	}

	/* NETWORK */
	@Override
	public void writeGuiData(PacketBufferForestry data) {
		game.writeData(data);
	}

	@Override
	public void readGuiData(PacketBufferForestry data) throws IOException {
		game.readData(data);
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		super.writeData(data);
		ItemStack displayStack = getIndividualOnDisplay();
		data.writeItem(displayStack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
		super.readData(data);
		individualOnDisplayClient = data.readItem();
	}

	/* ISlotPickupWatcher */
	@Override
	public void onTake(int slotIndex, Player player) {
		if (slotIndex == InventoryEscritoire.SLOT_ANALYZE) {
			game.reset();
			PacketItemStackDisplay packet = new PacketItemStackDisplay(this, getIndividualOnDisplay());
			NetworkUtil.sendNetworkPacket(packet, worldPosition, level);
		}
	}

	@Override
	public void setItem(int slotIndex, ItemStack itemstack) {
		super.setItem(slotIndex, itemstack);
		if (slotIndex == InventoryEscritoire.SLOT_ANALYZE) {
			PacketItemStackDisplay packet = new PacketItemStackDisplay(this, getIndividualOnDisplay());
			NetworkUtil.sendNetworkPacket(packet, worldPosition, level);
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerEscritoire(windowId, player, this);
	}

	@Override
	public void handleItemStackForDisplay(ItemStack itemStack) {
		if (!ItemStack.matches(itemStack, individualOnDisplayClient)) {
			individualOnDisplayClient = itemStack;
			//TODO
			Minecraft.getInstance().levelRenderer.setSectionDirty(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
			//			world.markForRerender(getPos());
		}
	}

	public ItemStack getIndividualOnDisplay() {
		if (level.isClientSide) {
			return individualOnDisplayClient;
		}
		return getItem(InventoryAnalyzer.SLOT_ANALYZE);
	}
}
