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

import javax.annotation.Nullable;
import java.io.IOException;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.IClimateContainer;
import forestry.api.climate.ImmutableClimateState;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.greenhouse.IGreenhouseHousing;
import forestry.api.greenhouse.IGreenhouseLimits;
import forestry.api.greenhouse.IGreenhouseProvider;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.tiles.ITitled;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;
import forestry.greenhouse.camouflage.CamouflageHandlerType;
import forestry.greenhouse.gui.ContainerGreenhouse;
import forestry.greenhouse.gui.GuiGreenhouse;
import forestry.greenhouse.multiblock.GreenhouseController;
import forestry.greenhouse.multiblock.MultiblockLogicGreenhouse;
import forestry.greenhouse.network.packets.PacketCamouflageSelectionServer;

public abstract class TileGreenhouse extends MultiblockTileEntityForestry<MultiblockLogicGreenhouse> implements IGreenhouseComponent, IGreenhouseHousing, IStreamableGui, IErrorLogicSource, IOwnedTile, ITitled, ICamouflageHandler, ICamouflagedTile {
	protected ItemStack camouflageBlock;

	public TileGreenhouse() {
		super(new MultiblockLogicGreenhouse());
		camouflageBlock = getDefaultCamouflageBlock();
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

	/* TILEFORESTRY */
	@Override
	protected void encodeDescriptionPacket(NBTTagCompound packetData) {
		super.encodeDescriptionPacket(packetData);
		if (!camouflageBlock.isEmpty()) {
			NBTTagCompound nbtTag = new NBTTagCompound();
			camouflageBlock.writeToNBT(nbtTag);
			packetData.setTag("Camouflage", nbtTag);
		}
	}

	@Override
	protected void decodeDescriptionPacket(NBTTagCompound packetData) {
		super.decodeDescriptionPacket(packetData);
		if (packetData.hasKey("Camouflage")) {
			setCamouflageBlock(new ItemStack(packetData.getCompoundTag("Camouflage")), true);
		}
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		if (data.hasKey("Camouflage")) {
			camouflageBlock = new ItemStack(data.getCompoundTag("Camouflage"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);
		if (!camouflageBlock.isEmpty()) {
			NBTTagCompound nbtTag = new NBTTagCompound();
			camouflageBlock.writeToNBT(nbtTag);
			data.setTag("Camouflage", nbtTag);
		}
		return data;
	}

	@Override
	public ItemStack getCamouflageBlock() {
		return camouflageBlock;
	}

	@Override
	public ItemStack getDefaultCamouflageBlock() {
		return GreenhouseController.createDefaultCamouflageBlock();
	}

	/* ICamouflageHandler */
	@Override
	public boolean setCamouflageBlock(ItemStack camouflageBlock, boolean sendClientUpdate) {
		if (!ItemStackUtil.isIdenticalItem(camouflageBlock, this.camouflageBlock)) {
			this.camouflageBlock = camouflageBlock;

			if (sendClientUpdate && world != null && world.isRemote) {
				NetworkUtil.sendToServer(new PacketCamouflageSelectionServer(this, CamouflageHandlerType.TILE));
				world.markBlockRangeForRenderUpdate(pos, pos);
			}
			return true;
		}
		return false;
	}

	/* IErrorLogicSource */
	@Override
	public IErrorLogic getErrorLogic() {
		return getMultiblockLogic().getController().getErrorLogic();
	}

	/* IOwnedTile */
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
	@SideOnly(Side.CLIENT)
	public void readGuiData(PacketBufferForestry data) throws IOException {
		getMultiblockLogic().getController().readGuiData(data);
	}

	/* IGuiHandlerTile */
	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, int data) {
		return new GuiGreenhouse(player, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
		return new ContainerGreenhouse(player.inventory, this);
	}
	
	/* IGreenhouseHousing */

	@Override
	public IClimateContainer getClimateContainer() {
		return getMultiblockLogic().getController().getClimateContainer();
	}

	@Override
	public int getSize() {
		return getMultiblockLogic().getController().getSize();
	}

	@Override
	public void onUpdateClimate() {
		getMultiblockLogic().getController().onUpdateClimate();
	}

	@Override
	public ImmutableClimateState getDefaultClimate() {
		return getMultiblockLogic().getController().getDefaultClimate();
	}

	@Override
	public EnumTemperature getTemperature() {
		return getMultiblockLogic().getController().getTemperature();
	}

	@Override
	public EnumHumidity getHumidity() {
		return getMultiblockLogic().getController().getHumidity();
	}

	@Override
	public float getExactTemperature() {
		return getMultiblockLogic().getController().getExactTemperature();
	}

	@Override
	public float getExactHumidity() {
		return getMultiblockLogic().getController().getExactHumidity();
	}

	@Override
	public IGreenhouseProvider getProvider() {
		return getMultiblockLogic().getController().getProvider();
	}

	@Nullable
	@Override
	public IGreenhouseLimits getLimits() {
		return getMultiblockLogic().getController().getLimits();
	}
}
