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

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitSocketType;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.apiculture.network.packets.PacketActiveUpdate;
import forestry.core.circuits.ISocketable;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketBufferForestry;
import forestry.core.tiles.IActivatable;
import forestry.core.tiles.TileForestry;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;
import forestry.greenhouse.api.climate.IClimateSource;
import forestry.greenhouse.api.climate.IClimateSourceOwner;
import forestry.greenhouse.blocks.BlockClimatiser;
import forestry.greenhouse.camouflage.CamouflageHandlerType;
import forestry.greenhouse.climate.ClimateSource;
import forestry.greenhouse.climate.ClimateSourceClimatiser;
import forestry.greenhouse.climate.ClimateSourceType;
import forestry.greenhouse.gui.ContainerClimatiser;
import forestry.greenhouse.gui.GuiClimatiser;
import forestry.greenhouse.multiblock.GreenhouseController;
import forestry.greenhouse.network.packets.PacketCamouflageSelectionServer;

public class TileClimatiser extends TileForestry implements IActivatable, IStreamableGui, IClimateSourceOwner, ICamouflagedTile, ICamouflageHandler, ISocketable {

	public static final ClimatiserDefinition HEATER = new ClimatiserDefinition(0.075F, 5F, ClimateSourceType.TEMPERATURE);
	public static final ClimatiserDefinition FAN = new ClimatiserDefinition(-0.075F, 5F, ClimateSourceType.TEMPERATURE);
	public static final ClimatiserDefinition HUMIDIFIER = new ClimatiserDefinition(0.075F, 5F, ClimateSourceType.HUMIDITY);
	public static final ClimatiserDefinition DEHUMIDIFIER = new ClimatiserDefinition(-0.075F, 5F, ClimateSourceType.HUMIDITY);

	private final InventoryAdapter sockets = new InventoryAdapter(1, "sockets");
	private final ClimateSource source;
	private ItemStack camouflageBlock;
	private boolean active;

	public TileClimatiser(ClimatiserDefinition definition) {
		this(definition.change, definition.range, definition.type);
	}

	protected TileClimatiser(float change, float range, ClimateSourceType type) {
		this(new ClimateSourceClimatiser(type, change, range));
	}

	protected TileClimatiser(ClimateSource source) {
		this.camouflageBlock = getDefaultCamouflageBlock();
		this.source = source;
		this.source.setOwner(this);
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
				markDirty();
			}
			return true;
		}
		return false;
	}

	/* IActivatable */
	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		if (this.active == active) {
			return;
		}

		this.active = active;

		if (world != null) {
			if (world.isRemote) {
				world.markBlockRangeForRenderUpdate(getCoordinates(), getCoordinates());
			} else {
				NetworkUtil.sendNetworkPacket(new PacketActiveUpdate(this), getCoordinates(), world);
			}
		}
	}

	@Override
	public void onChunkUnload() {
		source.onChunkUnload();

		super.onChunkUnload();
	}

	@Override
	public void invalidate() {
		source.invalidate();

		super.invalidate();
	}

	@Override
	protected void updateServerSide() {
		source.update();

		super.updateServerSide();
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		if (data.hasKey("Camouflage")) {
			camouflageBlock = new ItemStack(data.getCompoundTag("Camouflage"));
		}
		setActive(data.getBoolean("Active"));

		sockets.readFromNBT(data);

		ItemStack chip = sockets.getStackInSlot(0);
		if (!chip.isEmpty()) {
			ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitBoard(chip);
			if (chipset != null) {
				chipset.onLoad(this);
			}
		}

		source.readFromNBT(data);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);
		if (!camouflageBlock.isEmpty()) {
			NBTTagCompound nbtTag = new NBTTagCompound();
			camouflageBlock.writeToNBT(nbtTag);
			data.setTag("Camouflage", nbtTag);
		}
		data.setBoolean("Active", active);

		sockets.writeToNBT(data);

		source.writeToNBT(data);
		return data;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public String getUnlocalizedTitle() {
		Block block = getBlockType();
		String blockUnlocalizedName = block.getUnlocalizedName();
		if (block instanceof BlockClimatiser) {
			blockUnlocalizedName += '.' + ((BlockClimatiser) block).getNameFromMeta(getBlockMetadata());
		}
		return blockUnlocalizedName + ".name";
	}

	@Override
	public IClimateSource getClimateSource() {
		return source;
	}

	@Override
	public boolean isCircuitable() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getGui(EntityPlayer player, int data) {
		return new GuiClimatiser(player, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
		return new ContainerClimatiser(player.inventory, this);
	}

	@Override
	public void writeGuiData(PacketBufferForestry data) {
		sockets.writeData(data);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readGuiData(PacketBufferForestry data) throws IOException {
		sockets.readData(data);
	}

	/* ISocketable */
	@Override
	public int getSocketCount() {
		return sockets.getSizeInventory();
	}

	@Override
	public ItemStack getSocket(int slot) {
		return sockets.getStackInSlot(slot);
	}

	@Override
	public void setSocket(int slot, ItemStack stack) {

		if (!stack.isEmpty() && !ChipsetManager.circuitRegistry.isChipset(stack)) {
			return;
		}

		// Dispose correctly of old chipsets
		if (!sockets.getStackInSlot(slot).isEmpty()) {
			if (ChipsetManager.circuitRegistry.isChipset(sockets.getStackInSlot(slot))) {
				ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitBoard(sockets.getStackInSlot(slot));
				if (chipset != null) {
					chipset.onRemoval(this);
				}
			}
		}

		sockets.setInventorySlotContents(slot, stack);
		if (stack.isEmpty()) {
			return;
		}

		ICircuitBoard chipset = ChipsetManager.circuitRegistry.getCircuitBoard(stack);
		if (chipset != null) {
			chipset.onInsertion(this);
		}
	}

	@Override
	public ICircuitSocketType getSocketType() {
		return CircuitSocketType.GREENHOUSE_CLIMATISER;
	}

	public static class ClimatiserDefinition {
		float change, range;
		ClimateSourceType type;

		public ClimatiserDefinition(float change, float range, ClimateSourceType type) {
			this.change = change;
			this.range = range;
			this.type = type;
		}
	}
}
