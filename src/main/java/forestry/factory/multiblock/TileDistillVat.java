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
package forestry.factory.multiblock;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import forestry.api.core.IErrorLogicSource;
import forestry.api.multiblock.IDistillVatComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.access.EnumAccess;
import forestry.core.access.IAccessHandler;
import forestry.core.access.IRestrictedAccess;
import forestry.core.config.Config;
import forestry.core.fluids.ITankManager;
import forestry.core.gui.IHintSource;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.tiles.ILiquidTankTile;
import forestry.core.tiles.IPowerHandler;
import forestry.core.tiles.ITitled;
import forestry.energy.EnergyManager;
import forestry.factory.blocks.BlockDistillVatType;
import forestry.factory.gui.ContainerDistillVat;
import forestry.factory.gui.GuiDistillVat;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;

public abstract class TileDistillVat extends MultiblockTileEntityForestry<MultiblockLogicDistillVat> implements IDistillVatComponent, IEnergyHandler, IPowerHandler, IEnergyReceiver, ILiquidTankTile, IErrorLogicSource, IRestrictedAccess, IStreamableGui, ITitled, IHintSource {
	private final String unlocalizedTitle;


	protected TileDistillVat() {
		this(BlockDistillVatType.PLAIN);

	}

	protected TileDistillVat(BlockDistillVatType type) {
		super(new MultiblockLogicDistillVat());
		this.unlocalizedTitle = "tile.for.distillvat." + type + ".name";
	}

	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
		// Re-render this block on the client
		if (worldObj.isRemote) {
			this.worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
		worldObj.notifyBlockOfStateChange(getPos(), getBlockType());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void onMachineBroken() {
		// Re-render this block on the client
		if (worldObj.isRemote) {
			this.worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
		worldObj.notifyBlockOfStateChange(getPos(), getBlockType());
		markDirty();
	}


	@Override
	public IAccessHandler getAccessHandler() {
		return getMultiblockLogic().getController().getAccessHandler();
	}

	@Override
	public void onSwitchAccess(EnumAccess oldAccess, EnumAccess newAccess) {
		getMultiblockLogic().getController().onSwitchAccess(oldAccess, newAccess);
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return getMultiblockLogic().getController().getInternalInventory();
	}

	@Override
	public String getUnlocalizedTitle() {
		return unlocalizedTitle;
	}

	/* IHintSource */
	@Override
	public List<String> getHints() {
		return Config.hints.get("powered.machine");
	}

	/* IStreamableGui */
	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		getMultiblockLogic().getController().writeGuiData(data);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		getMultiblockLogic().getController().readGuiData(data);
	}

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiDistillVat(player.inventory, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerDistillVat(player.inventory, this);
	}

	@Override
	@Nonnull
	public ITankManager getTankManager() {return getMultiblockLogic().getController().getTankManager();}

	public EnergyManager getEnergyManager() {
		return getMultiblockLogic().getController().getEnergyManager();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (super.hasCapability(capability, facing)) {
			return true;
		}
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (super.hasCapability(capability, facing)) {
			return super.getCapability(capability, facing);
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(getTankManager());
		}
		return null;
	}

}
