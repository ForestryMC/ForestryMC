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

import java.util.Collection;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.Packet;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.common.Optional;

import forestry.api.core.IErrorState;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IFilterSlotDelegate;
import forestry.core.interfaces.IRestrictedAccess;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.ForestryPacket;
import forestry.core.network.INetworkedEntity;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketTileUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.AdjacentTileCache;
import forestry.core.utils.EnumAccess;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.Utils;
import forestry.core.vect.Vect;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerProvider;

@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = "BuildCraftAPI|statements")
public abstract class TileForestry extends TileEntity implements INetworkedEntity, IRestrictedAccess, IErrorSource, ITriggerProvider, IUpdatePlayerListBox, ISidedInventory, IFilterSlotDelegate {

	private static Random rand = new Random();

	protected final AdjacentTileCache tileCache = new AdjacentTileCache(this);
	protected boolean isInited = false;
	private IInventoryAdapter inventory = FakeInventoryAdapter.instance();
	private int tickCount = rand.nextInt(128);

	public AdjacentTileCache getTileCache() {
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

	public Vect Coords() {
		return new Vect(pos.getX(), pos.getY(), pos.getZ());
	}

	public void openGui(EntityPlayer player) {
	}

	public void rotateAfterPlacement(World world, BlockPos pos, EntityLivingBase entityliving, ItemStack stack) {

		int l = MathHelper.floor_double(((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		if (l == 0) {
			setOrientation(EnumFacing.NORTH);
		}
		if (l == 1) {
			setOrientation(EnumFacing.EAST);
		}
		if (l == 2) {
			setOrientation(EnumFacing.SOUTH);
		}
		if (l == 3) {
			setOrientation(EnumFacing.WEST);
		}
	}

	// / UPDATING
	@Override
	public void update() {
		tickCount++;
		if (!isInited) {
			initialize();
			isInited = true;
		}
	}

	public final boolean updateOnInterval(int tickInterval) {
		return tickCount % tickInterval == 0;
	}

	public abstract void initialize();

	// / SAVING & LOADING
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		inventory.readFromNBT(data);

		if (data.hasKey("Access")) {
			access = EnumAccess.values()[data.getInteger("Access")];
		} else {
			access = EnumAccess.SHARED;
		}
		if (data.hasKey("owner")) {
			owner = NBTUtil.readGameProfileFromNBT(data.getCompoundTag("owner"));
		}

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
		data.setInteger("Access", access.ordinal());
		if (this.owner != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			NBTUtil.writeGameProfile(nbt, owner);
			data.setTag("owner", nbt);
		}
		if (orientation != null) {
			data.setInteger("Orientation", orientation.ordinal());
		}
	}

	// / SMP
	@Override
	public void sendNetworkUpdate() {
		PacketTileUpdate packet = new PacketTileUpdate(this);
		Proxies.net.sendNetworkPacket(packet, pos);
	}

	@Override
	public Packet getDescriptionPacket() {
		PacketTileUpdate packet = new PacketTileUpdate(this);
		return packet.getPacket();
	}

	public abstract PacketPayload getPacketPayload();

	public abstract void fromPacketPayload(PacketPayload payload);

	@Override
	public void fromPacket(ForestryPacket packetRaw) {
		PacketTileUpdate packet = (PacketTileUpdate) packetRaw;
		if (orientation != packet.getOrientation()) {
			orientation = packet.getOrientation();
			worldObj.markBlockForUpdate(pos);
		}
		errorState = packet.getErrorState();
		owner = packet.getOwner();
		access = packet.getAccess();
		fromPacketPayload(packet.payload);
	}

	public void onRemoval() {
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

	/**
	 * @return true if tile is activated by redstone current.
	 */
	public boolean isActivated() {
		return worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
	}

	// / ORIENTATION
	private EnumFacing orientation = EnumFacing.WEST;

	public EnumFacing getOrientation() {
		return this.orientation;
	}

	public void setOrientation(EnumFacing orientation) {
		if (this.orientation == orientation) {
			return;
		}
		this.orientation = orientation;
		this.sendNetworkUpdate();
	}

	// / ERROR HANDLING
	public IErrorState errorState = EnumErrorCode.OK;

	public void setErrorState(IErrorState state) {
		if (this.errorState == state) {
			return;
		}
		this.errorState = state;
		this.sendNetworkUpdate();
	}

	@Override
	public boolean throwsErrors() {
		return true;
	}

	@Override
	public IErrorState getErrorState() {
		return errorState;
	}

	// / OWNERSHIP
	private GameProfile owner = null;
	private EnumAccess access = EnumAccess.SHARED;

	@Override
	public final boolean allowsRemoval(EntityPlayer player) {
		return Config.disablePermissions || !isOwnable() || !isOwned() || isOwner(player) || Proxies.common.isOp(player);
	}

	@Override
	public final boolean allowsAlteration(EntityPlayer player) {
		return allowsRemoval(player) || getAccess() == EnumAccess.SHARED;
	}

	@Override
	public final boolean allowsViewing(EntityPlayer player) {
		return allowsAlteration(player) || getAccess() == EnumAccess.VIEWABLE;
	}

	private boolean allowsPipeConnections() {
		return access == EnumAccess.SHARED;
	}

	@Override
	public EnumAccess getAccess() {
		return access;
	}

	@Override
	public boolean isOwnable() {
		return false;
	}

	@Override
	public boolean isOwned() {
		return owner != null;
	}

	@Override
	public GameProfile getOwnerProfile() {
		return owner;
	}

	@Override
	public void setOwner(EntityPlayer player) {
		this.owner = player.getGameProfile();
	}

	@Override
	public boolean isOwner(EntityPlayer player) {
		if (owner != null && player != null) {
			return PlayerUtil.isSameGameProfile(owner, player.getGameProfile());
		}
		return false;
	}

	@Override
	public boolean switchAccessRule(EntityPlayer player) {
		if (!isOwner(player)) {
			return false;
		}

		boolean couldPipesConnect = allowsPipeConnections();

		int ordinal = (access.ordinal() + 1) % EnumAccess.values().length;
		access = EnumAccess.values()[ordinal];
		if (!this.worldObj.isRemote) {
			sendNetworkUpdate();
		}

		boolean canPipesConnect = allowsPipeConnections();
		if (couldPipesConnect != canPipesConnect) {
			// pipes connected to this need to update
			worldObj.notifyNeighborsOfStateChange(pos, blockType);
			markDirty();
		}

		return true;
	}

	/* NAME */

	/**
	 * Gets the tile's unlocalized name, based on the block at the location of this entity (client-only).
	 */
	public String getUnlocalizedName() {
		String blockUnlocalizedName = getBlockType().getUnlocalizedName().replace("tile.for.", "");
		return blockUnlocalizedName + "." + getBlockMetadata() + ".name";
	}

	/* INVENTORY BASICS */
	public IInventoryAdapter getInternalInventory() {
		return inventory;
	}

	public final void setInternalInventory(IInventoryAdapter inv) {
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
	public final ItemStack decrStackSize(int slotIndex, int amount) {
		return getInternalInventory().decrStackSize(slotIndex, amount);
	}

	@Override
	public final ItemStack getStackInSlotOnClosing(int slotIndex) {
		return getInternalInventory().getStackInSlotOnClosing(slotIndex);
	}

	@Override
	public final void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
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
	public String getCommandSenderName() {
		return getUnlocalizedName();
	}

	@Override
	public final boolean isUseableByPlayer(EntityPlayer player) {
		if (!Utils.isUseableByPlayer(player, this) || !allowsViewing(player)) {
			return false;
		}
		return getInternalInventory().isUseableByPlayer(player);
	}
	
	@Override
	public boolean hasCustomName() {
		return getInternalInventory().hasCustomName();
	}

	@Override
	public final boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		if (itemStack == null || !allowsPipeConnections()) {
			return false;
		}

		if (!canSlotAccept(slotIndex, itemStack)) {
			return false;
		}

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
		if (!allowsPipeConnections()) {
			return Defaults.SLOTS_NONE;
		}
		return getInternalInventory().getSlotsForFace(side);
	}

	@Override
	public final boolean canInsertItem(int slotIndex, ItemStack itemStack, EnumFacing side) {
		if (itemStack == null || !allowsPipeConnections()) {
			return false;
		}
		return isItemValidForSlot(slotIndex, itemStack);
	}

	@Override
	public final boolean canExtractItem(int slotIndex, ItemStack itemStack, EnumFacing side) {
		if (itemStack == null || !allowsPipeConnections()) {
			return false;
		}
		return getInternalInventory().canExtractItem(slotIndex, itemStack, side);
	}
	

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		
	}

	@Override
	public IChatComponent getDisplayName() {
		return null;
	}
}
