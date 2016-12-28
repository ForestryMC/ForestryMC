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
package forestry.greenhouse.tiles;

import java.io.IOException;

import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.greenhouse.GreenhouseEvents.CamouflageChangeEvent;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.packets.CamouflageSelectionType;
import forestry.core.network.packets.PacketCamouflageSelectServer;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.ITitled;
import forestry.core.utils.ItemStackUtil;
import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseDoor;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.gui.ContainerGreenhouse;
import forestry.greenhouse.gui.GuiGreenhouse;
import forestry.greenhouse.multiblock.MultiblockLogicGreenhouse;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public abstract class TileGreenhouse extends MultiblockTileEntityForestry<MultiblockLogicGreenhouse> implements IGreenhouseComponent, IStreamableGui, IErrorLogicSource, IOwnedTile, ITitled, ICamouflageHandler, ICamouflagedTile {
	protected ItemStack camouflageBlock;

	protected TileGreenhouse() {
		super(new MultiblockLogicGreenhouse());
		camouflageBlock = ItemStack.EMPTY;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		if (newSate.getBlock() instanceof BlockGreenhouseDoor && oldState.getBlock() instanceof BlockGreenhouseDoor) {
			return false;
		}
		return super.shouldRefresh(world, pos, oldState, newSate);
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

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		if (data.hasKey("CamouflageBlock")) {
			camouflageBlock = new ItemStack(data.getCompoundTag("CamouflageBlock"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);
		if (!camouflageBlock.isEmpty()) {
			NBTTagCompound nbtTag = new NBTTagCompound();
			camouflageBlock.writeToNBT(nbtTag);
			data.setTag("CamouflageBlock", nbtTag);
		}
		return data;
	}

	@Override
	public boolean setCamouflageBlock(String type, ItemStack camouflageBlock, boolean sendClientUpdate) {
		if (!ItemStackUtil.isIdenticalItem(camouflageBlock, this.camouflageBlock)) {
			this.camouflageBlock = camouflageBlock;

			if (sendClientUpdate && world != null && world.isRemote) {
				Proxies.net.sendToServer(new PacketCamouflageSelectServer(this, type, CamouflageSelectionType.TILE));
				world.markBlockRangeForRenderUpdate(pos, pos);
			}
			MinecraftForge.EVENT_BUS.post(new CamouflageChangeEvent(getMultiblockLogic().getController(), this, this, type));
			return true;
		}
		return false;
	}

	@Override
	public boolean canHandleType(String type) {
		return type.equals(getCamouflageType());
	}

	@Override
	public ItemStack getCamouflageBlock(String type) {
		return camouflageBlock;
	}

	@Override
	public ItemStack getDefaultCamouflageBlock(String type) {
		return ItemStack.EMPTY;
	}

	/* TILEFORESTRY */

	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		super.encodeDescriptionPacket(packetData);
		if (!camouflageBlock.isEmpty()) {
			NBTTagCompound nbtTag = new NBTTagCompound();
			camouflageBlock.writeToNBT(nbtTag);
			packetData.setTag("CamouflageBlock", nbtTag);
		}
	}

	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		if (packetData.hasKey("CamouflageBlock")) {
			setCamouflageBlock(getCamouflageType(), new ItemStack(packetData.getCompoundTag("CamouflageBlock")), true);
		}
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
		return "for.gui.greenhouse.title";
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

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return new GuiGreenhouse(player, this);
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return new ContainerGreenhouse(player.inventory, this);
	}

	@Override
	public String getCamouflageType() {
		if (getBlockType() instanceof BlockGreenhouse && ((BlockGreenhouse) getBlockType()).getGreenhouseType() == BlockGreenhouseType.GLASS) {
			return CamouflageManager.GLASS;
		} else if (((BlockGreenhouse) getBlockType()).getGreenhouseType() == BlockGreenhouseType.DOOR) {
			return CamouflageManager.DOOR;
		}
		return CamouflageManager.BLOCK;
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		return getMultiblockLogic().getController().getInternalInventory();
	}

}
