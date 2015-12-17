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
import java.util.Collection;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.Optional;

import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.core.access.AccessHandler;
import forestry.core.access.EnumAccess;
import forestry.core.access.IAccessHandler;
import forestry.core.access.IRestrictedAccess;
import forestry.core.errors.ErrorLogic;
import forestry.core.gui.IGuiHandlerTile;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.network.packets.PacketTileStream;
import forestry.core.proxy.Proxies;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerProvider;

@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = "BuildCraftAPI|statements")
public abstract class TileForestry extends TileEntity implements IStreamable, IErrorLogicSource, ITriggerProvider, ISidedInventory, IFilterSlotDelegate, IRestrictedAccess, ITitled, ILocatable, IGuiHandlerTile, ITickable {
	private static final EnumFacing[] facings = EnumFacing.values();
	private static final Random rand = new Random();

	private final AccessHandler accessHandler = new AccessHandler(this);
	private final ErrorLogic errorHandler = new ErrorLogic();
	private final AdjacentTileCache tileCache = new AdjacentTileCache(this);
	private IInventoryAdapter inventory = FakeInventoryAdapter.instance();

	private int tickCount = rand.nextInt(256);
	private boolean needsNetworkUpdate = false;
	private EnumFacing orientation = EnumFacing.WEST;

	protected AdjacentTileCache getTileCache() {
		return tileCache;
	}

	public void onNeighborBlockChange(Block id) {
		tileCache.onNeighborChange();
	}

	@Override
	public void invalidate() {
		tileCache.purge();
		super.invalidate();
	}

	@Override
	public void validate() {
		tileCache.purge();
		super.validate();
	}

	public void rotateAfterPlacement(EntityPlayer player, EnumFacing side) {
		setOrientation(side);
	}

	public boolean rotate(EnumFacing axis) {
		if (axis == EnumFacing.DOWN || axis == EnumFacing.UP) {
			EnumFacing orientation = getOrientation().rotateAround(axis.getAxis());
			setOrientation(orientation);
			return true;
		}
		return false;
	}

	// / UPDATING
	@Override
	public void update() {
		tickCount++;

		if (!worldObj.isRemote) {
			updateServerSide();
		} else {
			updateClientSide();
		}

		if (needsNetworkUpdate) {
			needsNetworkUpdate = false;
			sendNetworkUpdate();
		}
	}

	protected void updateClientSide() {
	}

	protected void updateServerSide() {
	}

	protected final boolean updateOnInterval(int tickInterval) {
		return tickCount % tickInterval == 0;
	}

	// / SAVING & LOADING
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		inventory.readFromNBT(data);
		accessHandler.readFromNBT(data);

		if (data.hasKey("Orientation")) {
			orientation = EnumFacing.values()[data.getInteger("Orientation")];
		} else {
			orientation = EnumFacing.WEST;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		inventory.writeToNBT(data);
		accessHandler.writeToNBT(data);
		data.setInteger("Orientation", orientation.ordinal());
	}

	@Override
	public Packet getDescriptionPacket() {
		PacketTileStream packet = new PacketTileStream(this);
		return packet.getPacket();
	}

	/* INetworkedEntity */
	protected final void sendNetworkUpdate() {
		PacketTileStream packet = new PacketTileStream(this);
		Proxies.net.sendNetworkPacket(packet, worldObj);
	}

	/* IStreamable */
	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeEnum(orientation, facings);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		orientation = data.readEnum(facings);
	}

	public void onRemoval() {
	}

	public World getWorld() {
		return worldObj;
	}

	/* ITriggerProvider */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
		return null;
	}

	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(EnumFacing side, TileEntity tile) {
		return null;
	}

	// / REDSTONE INFO
	protected boolean isRedstoneActivated() {
		return worldObj.isBlockIndirectlyGettingPowered(getPos()) > 0;
	}

	// / ORIENTATION
	public EnumFacing getOrientation() {
		return this.orientation;
	}

	public void setOrientation(EnumFacing orientation) {
		if (orientation == null) {
			throw new NullPointerException("Orientation cannot be null");
		}
		if (this.orientation == orientation) {
			return;
		}
		this.orientation = orientation;
		this.setNeedsNetworkUpdate();
		worldObj.notifyBlockOfStateChange(pos, blockType);
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
	}

	protected final void setNeedsNetworkUpdate() {
		needsNetworkUpdate = true;
	}

	@Override
	public final IErrorLogic getErrorLogic() {
		return errorHandler;
	}

	@Override
	public final IAccessHandler getAccessHandler() {
		return accessHandler;
	}

	@Override
	public void onSwitchAccess(EnumAccess oldAccess, EnumAccess newAccess) {
		if (oldAccess == EnumAccess.SHARED || newAccess == EnumAccess.SHARED) {
			// pipes connected to this need to update
			worldObj.notifyBlockOfStateChange(pos, blockType);
			markDirty();
		}
	}

	/* NAME */

	/**
	 * Gets the tile's unlocalized name, based on the block at the location of this entity (client-only).
	 */
	@Override
	public String getUnlocalizedTitle() {
		String blockUnlocalizedName = getBlockType().getUnlocalizedName();
		return blockUnlocalizedName + '.' + getBlockMetadata() + ".name";
	}

	/* INVENTORY BASICS */
	public IInventoryAdapter getInternalInventory() {
		return inventory;
	}

	protected final void setInternalInventory(IInventoryAdapter inv) {
		if (inv == null) {
			throw new NullPointerException("Inventory cannot be null");
		}
		this.inventory = inv;
	}

	/* ISidedInventory */
	@Override
	public final int getSizeInventory() {
		return getInternalInventory().getSizeInventory();
	}

	@Override
	public final ItemStack getStackInSlot(int slotIndex) {
		return getInternalInventory().getStackInSlot(slotIndex);
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		return getInternalInventory().decrStackSize(slotIndex, amount);
	}

	@Override
	public final ItemStack getStackInSlotOnClosing(int slotIndex) {
		return getInternalInventory().getStackInSlotOnClosing(slotIndex);
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		getInternalInventory().setInventorySlotContents(slotIndex, itemstack);
	}

	@Override
	public final int getInventoryStackLimit() {
		return getInternalInventory().getInventoryStackLimit();
	}

	@Override
	public final void openInventory(EntityPlayer player) {
		getInternalInventory().openInventory(player);
	}

	@Override
	public final void closeInventory(EntityPlayer player) {
		getInternalInventory().closeInventory(player);
	}
	
	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentText(getUnlocalizedTitle());
	}

	@Override
	public final boolean isUseableByPlayer(EntityPlayer player) {
		return getInternalInventory().isUseableByPlayer(player);
	}
	
	@Override
	public String getCommandSenderName() {
		return getInternalInventory().getCommandSenderName();
	}
	
	@Override
	public boolean hasCustomName() {
		return getInternalInventory().hasCustomName();
	}

	@Override
	public final boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		return getInternalInventory().isItemValidForSlot(slotIndex, itemStack);
	}

	@Override
	public final boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return getInternalInventory().canSlotAccept(slotIndex, itemStack);
	}

	@Override
	public boolean isLocked(int slotIndex) {
		return getInternalInventory().isLocked(slotIndex);
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return getInternalInventory().getSlotsForFace(side);
	}

	@Override
	public final boolean canInsertItem(int slotIndex, ItemStack itemStack, EnumFacing side) {
		return getInternalInventory().canInsertItem(slotIndex, itemStack, side);
	}

	@Override
	public final boolean canExtractItem(int slotIndex, ItemStack itemStack, EnumFacing side) {
		return getInternalInventory().canExtractItem(slotIndex, itemStack, side);
	}
	
	@Override
	public int getField(int id) {
		return getInternalInventory().getField(id);
	}

	@Override
	public void setField(int id, int value) {
		setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return getFieldCount();
	}

	@Override
	public void clear() {
		clear();
	}
}
