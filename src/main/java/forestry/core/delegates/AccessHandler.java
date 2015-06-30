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
package forestry.core.delegates;

import java.io.IOException;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

import com.mojang.authlib.GameProfile;

import forestry.api.core.INBTTagable;
import forestry.core.config.Config;
import forestry.core.interfaces.IAccessHandler;
import forestry.core.interfaces.IRestrictedAccessTile;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EnumAccess;
import forestry.core.utils.PlayerUtil;

public final class AccessHandler implements IAccessHandler, IStreamable, INBTTagable {
	private final IRestrictedAccessTile tile;

	private GameProfile owner = null;
	private EnumAccess access = EnumAccess.SHARED;

	public AccessHandler(IRestrictedAccessTile tile) {
		this.tile = tile;
	}

	@Override
	public final boolean allowsRemoval(EntityPlayer player) {
		return !Config.enablePermissions || getAccessType() == EnumAccess.SHARED || !isOwned() || isOwner(player) || Proxies.common.isOp(player);
	}

	@Override
	public final boolean allowsAlteration(EntityPlayer player) {
		return allowsRemoval(player);
	}

	@Override
	public final boolean allowsViewing(EntityPlayer player) {
		return allowsAlteration(player) || getAccessType() == EnumAccess.VIEWABLE;
	}

	@Override
	public final boolean allowsPipeConnections() {
		return access == EnumAccess.SHARED;
	}

	@Override
	public EnumAccess getAccessType() {
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
	public void setOwner(GameProfile owner) {
		this.owner = owner;
	}

	@Override
	public boolean isOwner(EntityPlayer player) {
		return PlayerUtil.isSameGameProfile(owner, player.getGameProfile());
	}

	@Override
	public boolean switchAccessRule(EntityPlayer player) {
		if (!isOwner(player)) {
			return false;
		}

		EnumAccess oldAccess = access;
		int ordinal = (access.ordinal() + 1) % EnumAccess.values().length;
		access = EnumAccess.values()[ordinal];

		if (player.worldObj.isRemote) {
			Proxies.net.sendToServer(new PacketCoordinates(PacketId.ACCESS_SWITCH, tile.getCoordinates()));
		} else {
			tile.onSwitchAccess(oldAccess, access);
		}

		return true;
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		if (owner == null) {
			data.writeByte(-1);
		} else {
			data.writeByte(access.ordinal());
			data.writeLong(owner.getId().getMostSignificantBits());
			data.writeLong(owner.getId().getLeastSignificantBits());
			data.writeUTF(owner.getName());
		}
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		byte accessOrdinal = data.readByte();
		if (accessOrdinal >= 0) {
			access = EnumAccess.values()[accessOrdinal];
			owner = new GameProfile(new UUID(data.readLong(), data.readLong()), data.readUTF());
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		access = EnumAccess.values()[data.getInteger("Access")];

		if (data.hasKey("owner")) {
			owner = NBTUtil.func_152459_a(data.getCompoundTag("owner"));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		data.setInteger("Access", access.ordinal());

		if (this.owner != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			NBTUtil.func_152460_a(nbt, owner);
			data.setTag("owner", nbt);
		}
	}
}
