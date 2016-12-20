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

import java.io.IOException;
import java.util.List;

import forestry.api.circuits.ICircuitSocketType;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.multiblock.IFarmComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.circuits.ISocketable;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.tiles.ITitled;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.gui.ContainerFarm;
import forestry.farming.gui.GuiFarm;
import forestry.farming.models.EnumFarmBlockTexture;
import forestry.farming.multiblock.MultiblockLogicFarm;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TileFarm extends MultiblockTileEntityForestry<MultiblockLogicFarm> implements IFarmComponent, IHintSource, ISocketable, IStreamableGui, IErrorLogicSource, IOwnedTile, ITitled {
	private EnumFarmBlockTexture farmBlockTexture = EnumFarmBlockTexture.BRICK_STONE;

	protected TileFarm() {
		super(new MultiblockLogicFarm());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void onMachineAssembled(IMultiblockController multiblockController, BlockPos minCoord, BlockPos maxCoord) {
		world.notifyNeighborsOfStateChange(getPos(), world.getBlockState(pos).getBlock(), false);
		markDirty();
	}

	@Override
	public void onMachineBroken() {
		world.notifyNeighborsOfStateChange(getPos(), world.getBlockState(pos).getBlock(), false);
		markDirty();
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return getMultiblockLogic().getController().getInternalInventory();
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		farmBlockTexture = EnumFarmBlockTexture.getFromCompound(nbttagcompound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);
		farmBlockTexture.saveToCompound(nbttagcompound);
		return nbttagcompound;
	}

	/* CONSTRUCTION MATERIAL */

	public void setFarmBlockTexture(EnumFarmBlockTexture farmBlockTexture) {
		if (this.farmBlockTexture != farmBlockTexture) {
			this.farmBlockTexture = farmBlockTexture;
			world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}

	public EnumFarmBlockTexture getFarmBlockTexture() {
		return farmBlockTexture;
	}

	public EnumFarmBlockType getFarmBlockType() {
		return EnumFarmBlockType.VALUES[getBlockMetadata()];
	}

	/* TILEFORESTRY */

	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		super.encodeDescriptionPacket(packetData);
		farmBlockTexture.saveToCompound(packetData);
	}

	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		EnumFarmBlockTexture farmBlockTexture = EnumFarmBlockTexture.getFromCompound(packetData);
		setFarmBlockTexture(farmBlockTexture);
	}

	/* IHintSource */
	@Override
	public List<String> getHints() {
		return Config.hints.get("farm");
	}

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
	public Object getGui(EntityPlayer player, int data) {
		return new GuiFarm(player, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerFarm(player.inventory, this);
	}
}
