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

import com.mojang.authlib.GameProfile;
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
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.util.Optional;

public class TileEscritoire extends TileBase implements ISidedInventory, ISlotPickupWatcher, IStreamableGui, IItemStackDisplay {

    private final EscritoireGame game = new EscritoireGame();
    private ItemStack individualOnDisplayClient = ItemStack.EMPTY;

    public TileEscritoire() {
        super(CoreTiles.ESCRITOIRE.tileType());
        setInternalInventory(new InventoryEscritoire(this));
    }

    /* SAVING & LOADING */
    @Override
    public void read(BlockState state, CompoundNBT compoundNBT) {
        super.read(state, compoundNBT);
        game.read(compoundNBT);
    }


    @Override
    public CompoundNBT write(CompoundNBT compoundNBT) {
        compoundNBT = super.write(compoundNBT);
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

        Optional<IIndividual> optional = RootUtils.getIndividual(getStackInSlot(InventoryEscritoire.SLOT_ANALYZE));
        if (!optional.isPresent()) {
            return;
        }
        IIndividual individual = optional.get();

        IAlleleForestrySpecies species = individual.getGenome().getPrimary(IAlleleForestrySpecies.class);
        IIndividualRoot<IIndividual> root = (IIndividualRoot<IIndividual>) species.getRoot();
        IResearchHandler<IIndividual> handler = root.getComponent(ForestryComponentKeys.RESEARCH);
        for (ItemStack itemstack : handler.getResearchBounty(species, world, gameProfile, individual, game.getBountyLevel())) {
            InventoryUtil.addStack(getInternalInventory(), itemstack, InventoryEscritoire.SLOT_RESULTS_1, InventoryEscritoire.SLOTS_RESULTS_COUNT, true);
        }
    }

    private boolean areProbeSlotsFilled() {
        int filledSlots = 0;
        int required = game.getSampleSize(InventoryEscritoire.SLOTS_INPUT_COUNT);
        for (int i = InventoryEscritoire.SLOT_INPUT_1; i < InventoryEscritoire.SLOT_INPUT_1 + required; i++) {
            if (!getStackInSlot(i).isEmpty()) {
                filledSlots++;
            }
        }

        return filledSlots >= required;
    }

    public void probe() {
        if (world.isRemote) {
            return;
        }

        ItemStack analyze = getStackInSlot(InventoryEscritoire.SLOT_ANALYZE);

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
        data.writeItemStack(displayStack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void readData(PacketBufferForestry data) throws IOException {
        super.readData(data);
        individualOnDisplayClient = data.readItemStack();
    }

    /* ISlotPickupWatcher */
    @Override
    public void onTake(int slotIndex, PlayerEntity player) {
        if (slotIndex == InventoryEscritoire.SLOT_ANALYZE) {
            game.reset();
            PacketItemStackDisplay packet = new PacketItemStackDisplay(this, getIndividualOnDisplay());
            NetworkUtil.sendNetworkPacket(packet, pos, world);
        }
    }

    @Override
    public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
        super.setInventorySlotContents(slotIndex, itemstack);
        if (slotIndex == InventoryEscritoire.SLOT_ANALYZE) {
            PacketItemStackDisplay packet = new PacketItemStackDisplay(this, getIndividualOnDisplay());
            NetworkUtil.sendNetworkPacket(packet, pos, world);
        }
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        return new ContainerEscritoire(windowId, player, this);
    }

    @Override
    public void handleItemStackForDisplay(ItemStack itemStack) {
        if (!ItemStack.areItemStacksEqual(itemStack, individualOnDisplayClient)) {
            individualOnDisplayClient = itemStack;
            //TODO
            Minecraft.getInstance().worldRenderer.markForRerender(getPos().getX(), getPos().getY(), getPos().getZ());
            //			world.markForRerender(getPos());
        }
    }

    public ItemStack getIndividualOnDisplay() {
        if (world.isRemote) {
            return individualOnDisplayClient;
        }
        return getStackInSlot(InventoryAnalyzer.SLOT_ANALYZE);
    }
}
