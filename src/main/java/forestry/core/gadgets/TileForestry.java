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

import com.google.common.collect.ImmutableSet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;

import forestry.api.core.ErrorStateRegistry;
import forestry.api.core.IErrorState;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.fluids.TankManager;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IFilterSlotDelegate;
import forestry.core.interfaces.ILiquidTankContainer;
import forestry.core.interfaces.IRestrictedAccess;
import forestry.core.inventory.FakeInventoryAdapter;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketErrorUpdate;
import forestry.core.network.PacketTileStream;
import forestry.core.proxy.Proxies;
import forestry.core.utils.AdjacentTileCache;
import forestry.core.utils.EnumAccess;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.Utils;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerProvider;

@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = "BuildCraftAPI|statements")
public abstract class TileForestry extends TileEntity implements IStreamable, IRestrictedAccess, IErrorSource, ITriggerProvider, ISidedInventory, IFilterSlotDelegate {

	private static final Random rand = new Random();

	private IInventoryAdapter inventory = FakeInventoryAdapter.instance();
	private int tickCount = rand.nextInt(256);
	private boolean needsNetworkUpdate = false;
	private ForgeDirection orientation = ForgeDirection.WEST;

	protected final AdjacentTileCache tileCache = new AdjacentTileCache(this);
	protected boolean isInited = false;

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

	public void openGui(EntityPlayer player) {
	}

	public void rotateAfterPlacement(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {

		int l = MathHelper.floor_double(((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		if (l == 0) {
			setOrientation(ForgeDirection.NORTH);
		}
		if (l == 1) {
			setOrientation(ForgeDirection.EAST);
		}
		if (l == 2) {
			setOrientation(ForgeDirection.SOUTH);
		}
		if (l == 3) {
			setOrientation(ForgeDirection.WEST);
		}

	}

	private ImmutableSet<IErrorState> previousErrorStates;

	// / UPDATING
	@Override
	public final void updateEntity() {
		tickCount++;

		if (!isInited) {
			initialize();
			isInited = true;
		}

		if (Proxies.common.isSimulating(worldObj)) {
			updateServerSide();
		} else {
			updateClientSide();
		}

		ImmutableSet<IErrorState> errorStates = getErrorStates();

		if (needsNetworkUpdate) {
			needsNetworkUpdate = false;
			sendNetworkUpdate();
		} else if (!errorStates.equals(previousErrorStates)) {
			sendErrorUpdate();
		}

		previousErrorStates = errorStates;
	}

	protected void updateClientSide() {
	}

	protected void updateServerSide() {
	}

	protected final boolean updateOnInterval(int tickInterval) {
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
			owner = NBTUtil.func_152459_a(data.getCompoundTag("owner"));
		}

		if (data.hasKey("Orientation")) {
			orientation = ForgeDirection.values()[data.getInteger("Orientation")];
		} else {
			orientation = ForgeDirection.WEST;
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		inventory.writeToNBT(data);
		data.setInteger("Access", access.ordinal());
		if (this.owner != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			NBTUtil.func_152460_a(nbt, owner);
			data.setTag("owner", nbt);
		}
		if (orientation != null) {
			data.setInteger("Orientation", orientation.ordinal());
		}
	}

	@Override
	public final Packet getDescriptionPacket() {
		PacketTileStream packet = new PacketTileStream(this);
		return packet.getPacket();
	}

	/* INetworkedEntity */
	public final void sendNetworkUpdate() {
		PacketTileStream packet = new PacketTileStream(this);
		Proxies.net.sendNetworkPacket(packet);
	}

	public final void sendErrorUpdate() {
		PacketErrorUpdate packet = new PacketErrorUpdate(this);
		Proxies.net.sendNetworkPacket(packet);
	}

	public void writeData(DataOutputStream data) throws IOException {
		data.writeByte(orientation.ordinal());

		writeErrorData(data);

		// TODO: Should this really be sent to the client? Huge network cost.
		// As far as I know, only GUIs need it, and there are better ways to get the information to a GUI.
		// -CovertJaguar
		if (owner == null) {
			data.writeBoolean(false);
		} else {
			data.writeBoolean(true);
			data.writeByte(access.ordinal());
			data.writeLong(owner.getId().getMostSignificantBits());
			data.writeLong(owner.getId().getLeastSignificantBits());
			data.writeUTF(owner.getName());
		}

		if (this instanceof ILiquidTankContainer) {
			TankManager tankManager = ((ILiquidTankContainer) this).getTankManager();
			if (tankManager != null) {
				tankManager.writePacketData(data);
			}
		}
	}

	public void readData(DataInputStream data) throws IOException {
		orientation = ForgeDirection.getOrientation(data.readByte());

		readErrorData(data);

		if (data.readBoolean()) {
			byte accessOrdinal = data.readByte();
			access = EnumAccess.values()[accessOrdinal];
			owner = new GameProfile(new UUID(data.readLong(), data.readLong()), data.readUTF());
		}

		if (this instanceof ILiquidTankContainer) {
			TankManager tankManager = ((ILiquidTankContainer) this).getTankManager();
			if (tankManager != null) {
				tankManager.readPacketData(data);
			}
		}
	}

	public final void writeErrorData(DataOutputStream data) throws IOException {
		data.writeShort(errorStates.size());
		for (IErrorState errorState : errorStates) {
			data.writeShort(errorState.getID());
		}
	}

	public final void readErrorData(DataInputStream data) throws IOException {
		errorStates.clear();

		short errorStateCount = data.readShort();
		for (int i = 0; i < errorStateCount; i++) {
			short errorStateId = data.readShort();
			IErrorState errorState = ErrorStateRegistry.getErrorState(errorStateId);
			errorStates.add(errorState);
		}
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
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		return null;
	}

	// / REDSTONE INFO
	public boolean isRedstoneActivated() {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}

	// / ORIENTATION
	public ForgeDirection getOrientation() {
		return this.orientation;
	}

	public void setOrientation(ForgeDirection orientation) {
		if (this.orientation == orientation) {
			return;
		}
		this.orientation = orientation;
		this.sendNetworkUpdate();
	}

	protected final void setNeedsNetworkUpdate() {
		needsNetworkUpdate = true;
	}

	// / ERROR HANDLING
	private final Set<IErrorState> errorStates = new HashSet<IErrorState>();

	public final boolean setErrorCondition(boolean condition, IErrorState errorState) {
		if (condition) {
			errorStates.add(errorState);
		} else {
			errorStates.remove(errorState);
		}
		return condition;
	}

	@Deprecated
	protected final void addErrorState(IErrorState state) {
		errorStates.add(state);
	}

	@Deprecated
	protected final void removeErrorStates() {
		errorStates.clear();
	}

	public final boolean hasErrorState(IErrorState state) {
		return errorStates.contains(state);
	}

	public final boolean hasErrorState() {
		return errorStates.size() > 0;
	}

	@Override
	public ImmutableSet<IErrorState> getErrorStates() {
		return ImmutableSet.copyOf(errorStates);
	}

	// / OWNERSHIP
	private GameProfile owner = null;
	private EnumAccess access = EnumAccess.SHARED;

	@Override
	public final boolean allowsRemoval(EntityPlayer player) {
		return Config.disablePermissions || getAccess() == EnumAccess.SHARED || !isOwned() || isOwner(player) || Proxies.common.isOp(player);
	}

	@Override
	public final boolean allowsAlteration(EntityPlayer player) {
		return allowsRemoval(player);
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
	public boolean isOwned() {
		return owner != null;
	}

	@Override
	public GameProfile getOwner() {
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
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, blockType);
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
		return blockUnlocalizedName + '.' + getBlockMetadata() + ".name";
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
	public final void openInventory() {
		getInternalInventory().openInventory();
	}

	@Override
	public final void closeInventory() {
		getInternalInventory().closeInventory();
	}

	@Override
	public final String getInventoryName() {
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
	public final boolean hasCustomInventoryName() {
		return getInternalInventory().hasCustomInventoryName();
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
	public final int[] getAccessibleSlotsFromSide(int side) {
		if (!allowsPipeConnections()) {
			return Defaults.SLOTS_NONE;
		}
		return getInternalInventory().getAccessibleSlotsFromSide(side);
	}

	@Override
	public final boolean canInsertItem(int slotIndex, ItemStack itemStack, int side) {
		if (itemStack == null || !allowsPipeConnections()) {
			return false;
		}
		return isItemValidForSlot(slotIndex, itemStack);
	}

	@Override
	public final boolean canExtractItem(int slotIndex, ItemStack itemStack, int side) {
		if (itemStack == null || !allowsPipeConnections()) {
			return false;
		}
		return getInternalInventory().canExtractItem(slotIndex, itemStack, side);
	}
}
