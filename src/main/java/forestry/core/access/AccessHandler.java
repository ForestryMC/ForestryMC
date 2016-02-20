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
package forestry.core.access;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.core.config.Config;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.packets.PacketAccessSwitch;
import forestry.core.network.packets.PacketAccessSwitchEntity;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.ILocatable;
import forestry.core.utils.PlayerUtil;

public final class AccessHandler implements IAccessHandler {
	private final IRestrictedAccess accessListener;
	private final List<IAccessOwnerListener> accessOwnerListeners = new ArrayList<>();
	private final Object target;

	private GameProfile owner = null;
	private EnumAccess access = EnumAccess.SHARED;

	public <T extends IRestrictedAccess & ILocatable> AccessHandler(T tile) {
		this.accessListener = tile;
		this.target = tile;
	}

	public <T extends Entity & IRestrictedAccess> AccessHandler(T entity) {
		this.accessListener = entity;
		this.target = entity;
	}

	public void addOwnerListener(IAccessOwnerListener accessListener) {
		accessOwnerListeners.add(accessListener);
	}

	public void removeOwnerListener(IAccessOwnerListener accessListener) {
		accessOwnerListeners.remove(accessListener);
	}

	@Override
	public final boolean allowsRemoval(EntityPlayer player) {
		return !Config.enablePermissions || getAccess() == EnumAccess.SHARED || !isOwned() || isOwner(player) || Proxies.common.isOp(player);
	}

	@Override
	public final boolean allowsAlteration(EntityPlayer player) {
		return allowsRemoval(player);
	}

	@Override
	public final boolean allowsViewing(EntityPlayer player) {
		return allowsAlteration(player) || getAccess() == EnumAccess.VIEWABLE;
	}

	@Override
	public final boolean allowsPipeConnections() {
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
	public void setOwner(GameProfile owner) {
		this.owner = owner;
		for (IAccessOwnerListener listener : accessOwnerListeners) {
			listener.onOwnerSet(owner);
		}
	}

	@Override
	public boolean isOwner(EntityPlayer player) {
		return PlayerUtil.isSameGameProfile(owner, player.getGameProfile());
	}

	@Override
	public boolean switchAccess(EntityPlayer player) {
		if (!isOwner(player)) {
			return false;
		}

		int ordinal = (access.ordinal() + 1) % EnumAccess.values().length;
		EnumAccess newAccess = EnumAccess.values()[ordinal];
		setAccess(player.worldObj, newAccess);

		return true;
	}

	private void setAccess(World world, EnumAccess access) {
		EnumAccess oldAccess = this.access;
		if (oldAccess == access) {
			return;
		}

		this.access = access;

		if (world.isRemote) {
			if (target instanceof ILocatable) {
				Proxies.net.sendToServer(new PacketAccessSwitch((ILocatable) target));
			} else if (target instanceof Entity) {
				Proxies.net.sendToServer(new PacketAccessSwitchEntity((Entity) target));
			}
		} else {
			accessListener.onSwitchAccess(oldAccess, access);
		}
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
			GameProfile owner = new GameProfile(new UUID(data.readLong(), data.readLong()), data.readUTF());
			setOwner(owner);
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
			nbt.removeTag("Properties");
			data.setTag("owner", nbt);
		}
	}
}
