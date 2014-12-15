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

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerProvider;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.Optional;
import forestry.api.core.IErrorState;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IOwnable;
import forestry.core.network.ForestryPacket;
import forestry.core.network.INetworkedEntity;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketTileUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumAccess;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.utils.PlayerUtil;
import forestry.core.utils.Vect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Collection;

@Optional.Interface(iface = "buildcraft.api.statements.ITriggerProvider", modid = "BuildCraftAPI|statements")
public abstract class TileForestry extends TileEntity implements INetworkedEntity, IOwnable, IErrorSource, ITriggerProvider {

	protected boolean isInited = false;
	private TileInventoryAdapter inventory;

	public Vect Coords() {
		return new Vect(xCoord, yCoord, zCoord);
	}

	public World getWorld() {
		return this.getWorldObj();
	}

	public void openGui(EntityPlayer player) {
	}

	public void rotateAfterPlacement(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {

		int l = MathHelper.floor_double(((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		if (l == 0)
			setOrientation(ForgeDirection.NORTH);
		if (l == 1)
			setOrientation(ForgeDirection.EAST);
		if (l == 2)
			setOrientation(ForgeDirection.SOUTH);
		if (l == 3)
			setOrientation(ForgeDirection.WEST);

	}

	// / UPDATING
	@Override
	public void updateEntity() {
		if (!isInited) {
			initialize();
			isInited = true;
		}
	}

	public abstract void initialize();

	// / SAVING & LOADING
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);

		if (inventory != null)
			inventory.readFromNBT(data);

		if (data.hasKey("Access"))
			access = EnumAccess.values()[data.getInteger("Access")];
		else
			access = EnumAccess.SHARED;
		if (data.hasKey("owner"))
			owner = NBTUtil.func_152459_a(data.getCompoundTag("owner"));

		if (data.hasKey("Orientation"))
			orientation = ForgeDirection.values()[data.getInteger("Orientation")];
		else
			orientation = ForgeDirection.WEST;

	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		if (inventory != null)
			inventory.writeToNBT(data);
		data.setInteger("Access", access.ordinal());
		if (this.owner != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			NBTUtil.func_152460_a(nbt, owner);
			data.setTag("owner", nbt);
		}
		if (orientation != null)
			data.setInteger("Orientation", orientation.ordinal());
	}

	// / SMP
	@Override
	public void sendNetworkUpdate() {
		PacketTileUpdate packet = new PacketTileUpdate(this);
		Proxies.net.sendNetworkPacket(packet, xCoord, yCoord, zCoord);
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
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		return null;
	}

	// / REDSTONE INFO
	/**
	 * @return true if tile is activated by redstone current.
	 */
	public boolean isActivated() {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}
	// / ORIENTATION
	private ForgeDirection orientation = ForgeDirection.WEST;

	public ForgeDirection getOrientation() {
		return this.orientation;
	}

	public void setOrientation(ForgeDirection orientation) {
		if (this.orientation == orientation)
			return;
		this.orientation = orientation;
		this.sendNetworkUpdate();
	}
	// / ERROR HANDLING
	public IErrorState errorState = EnumErrorCode.OK;

	public void setErrorState(IErrorState state) {
		if (this.errorState == state)
			return;
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
	public boolean allowsRemoval(EntityPlayer player) {
		if (!isOwnable())
			return true;
		if (!isOwned())
			return true;
		if (isOwner(player))
			return true;
		if (Proxies.common.isOp(player))
			return true;

		return getAccess() == EnumAccess.SHARED;
	}

	@Override
	public boolean allowsInteraction(EntityPlayer player) {
		if (Config.disablePermissions)
			return true;
		if (!isOwnable())
			return true;
		if (!isOwned())
			return true;
		if (isOwner(player))
			return true;
		if (Proxies.common.isOp(player))
			return true;

		return getAccess() == EnumAccess.SHARED || getAccess() == EnumAccess.VIEWABLE;
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
		if (owner != null && player != null)
			return PlayerUtil.isSameGameProfile(owner, player.getGameProfile());
		return false;
	}

	@Override
	public boolean switchAccessRule(EntityPlayer player) {
		if (!isOwner(player))
			return false;

		if (access.ordinal() < EnumAccess.values().length - 1)
			access = EnumAccess.values()[access.ordinal() + 1];
		else
			access = EnumAccess.values()[0];

		return true;
	}

	/* NAME */
	/**
	 * Gets the tile's unlocalized name, based on the block at the location of this entity (client-only).
	 * @return 
	 */
	public String getUnlocalizedName() {
		String blockUnlocalizedName = getBlockType().getUnlocalizedName().replace("tile.for.", "");
		return blockUnlocalizedName + "." + getBlockMetadata() + ".name";
	}

	/* INVENTORY BASICS */
	public final TileInventoryAdapter getInternalInventory() {
		return inventory;
	}

	public final void setInternalInventory(TileInventoryAdapter inv) {
		this.inventory = inv;
	}
}
