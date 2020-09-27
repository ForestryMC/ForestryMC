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
package forestry.farming.tiles;

import forestry.api.circuits.ICircuitSocketType;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.multiblock.IFarmComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.circuits.ISocketable;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.tiles.ITitled;
import forestry.farming.gui.ContainerFarm;
import forestry.farming.multiblock.MultiblockLogicFarm;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

public abstract class TileFarm extends MultiblockTileEntityForestry<MultiblockLogicFarm> implements IFarmComponent, ISocketable, IStreamableGui, IErrorLogicSource, IOwnedTile, ITitled {

    protected TileFarm(TileEntityType<?> tileEntityType) {
        super(tileEntityType, new MultiblockLogicFarm());
    }

    //TODO don't know
    //	@Override
    //	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState) {
    //		return oldState.getBlock() != newState.getBlock();
    //	}

    @Override
    public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
        world.notifyNeighborsOfStateChange(
                getPos(),
                world.getBlockState(pos).getBlock()
        );    //TODO - removing false OK?
        markDirty();
    }

    @Override
    public void onMachineBroken() {
        world.notifyNeighborsOfStateChange(getPos(), world.getBlockState(pos).getBlock());
        markDirty();
    }

    @Override
    public IInventoryAdapter getInternalInventory() {
        return getMultiblockLogic().getController().getInternalInventory();
    }

    //	public EnumFarmBlockTexture getFarmBlockTexture() {
    //		return farmBlockTexture;
    //	}
    //
    //	public EnumFarmBlockType getFarmBlockType() {
    //		return EnumFarmBlockType.VALUES[getBlockMetadata()];
    //	}

    /* ISocketable */
    @Override
    public int getSocketCount() {
        return getMultiblockLogic().getController().getSocketCount();
    }

    @Override
    public ItemStack getSocket(int slot) {
        return getMultiblockLogic().getController().getSocket(slot);
    }

    @Override
    public void setSocket(int slot, ItemStack stack) {
        getMultiblockLogic().getController().setSocket(slot, stack);
    }

    @Override
    public ICircuitSocketType getSocketType() {
        return getMultiblockLogic().getController().getSocketType();
    }

    /* IStreamableGui */
    @Override
    public void writeGuiData(PacketBufferForestry data) {
        getMultiblockLogic().getController().writeGuiData(data);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void readGuiData(PacketBufferForestry data) throws IOException {
        getMultiblockLogic().getController().readGuiData(data);
    }

    /* IErrorLogicSource */
    @Override
    public IErrorLogic getErrorLogic() {
        return getMultiblockLogic().getController().getErrorLogic();
    }

    @Override
    public IOwnerHandler getOwnerHandler() {
        return getMultiblockLogic().getController().getOwnerHandler();
    }

    /* ITitled */
    @Override
    public String getUnlocalizedTitle() {
        return "for.gui.farm.title";
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        return new ContainerFarm(windowId, inv, this);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(this.getUnlocalizedTitle());
    }
}
