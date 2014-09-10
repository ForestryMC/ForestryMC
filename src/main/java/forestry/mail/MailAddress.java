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
package forestry.mail;

import java.util.Locale;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;

import com.mojang.authlib.GameProfile;

import forestry.api.core.INBTTagable;
import forestry.core.proxy.Proxies;
import forestry.core.network.EntityNetData;
import forestry.api.mail.EnumAddressee;
import forestry.api.mail.IMailAddress;

public class MailAddress implements INBTTagable, IMailAddress {
	@EntityNetData
	private EnumAddressee type;
	@EntityNetData
	private GameProfile gameProfile; // gameProfile is a fake GameProfile for traders, and real for players

	private static final MailAddress invalidAddress = new MailAddress();

	public MailAddress() {
		this.type = EnumAddressee.INVALID;
		this.gameProfile = new GameProfile(new UUID(0,0), "");
	}

	public MailAddress(GameProfile gameProfile) {
		if (gameProfile == null)
			throw new IllegalArgumentException("gameProfile must not be null");

		this.type = EnumAddressee.PLAYER;
		this.gameProfile = gameProfile;
	}

	public MailAddress(String name) {
		if (name == null)
			throw new IllegalArgumentException("name must not be null");

		this.type = EnumAddressee.TRADER;
		this.gameProfile = new GameProfile(new UUID(0,0), name);
	}

	public MailAddress(IMailAddress address) {
		this.type = address.getType();
		if (type == EnumAddressee.TRADER) {
			String name = address.getName();
			this.gameProfile = new GameProfile(new UUID(0, 0), name);
		} else if (type == EnumAddressee.PLAYER) {
			this.gameProfile = address.getPlayerProfile();
		}
	}

	public EnumAddressee getType() {
		return type;
	}

	public String getName() {
		return gameProfile.getName();
	}

	@Override
	public String toString() {
		String name = getName().toLowerCase(Locale.ENGLISH);
		if (isPlayer()) {
			return type + "-" + name + "-" + gameProfile.getId();
		} else {
			return type + "-" + name;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MailAddress))
			return false;

		MailAddress address = (MailAddress)o;
		return address.gameProfile.equals(gameProfile);
	}

	@Override
	public int hashCode() {
		return gameProfile.hashCode();
	}

	public boolean isPlayer() {
		return type == EnumAddressee.PLAYER;
	}

	public boolean isValid() {
		return type != EnumAddressee.INVALID;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		type = EnumAddressee.INVALID;
		if(nbttagcompound.hasKey("TP")) {
			String typeName = nbttagcompound.getString("TP");
			type = EnumAddressee.fromString(typeName);
		}

		if (!isValid()) {
			gameProfile = invalidAddress.gameProfile;
		} else if(nbttagcompound.hasKey("profile")) {
			NBTTagCompound profileTag = nbttagcompound.getCompoundTag("profile");
			gameProfile = NBTUtil.func_152459_a(profileTag);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setString("TP", type.toString());

		if (gameProfile != null) {
			NBTTagCompound profileNbt = new NBTTagCompound();
			NBTUtil.func_152460_a(profileNbt, gameProfile);
			nbttagcompound.setTag("profile", profileNbt);
		}
	}

	public static MailAddress loadFromNBT(NBTTagCompound nbttagcompound) {
		MailAddress address = new MailAddress();
		address.readFromNBT(nbttagcompound);
		return address;
	}

	public EntityPlayer getPlayer(World world) {
		if (!this.isPlayer())
			return null;

		return world.getPlayerEntityByName(this.gameProfile.getName());
	}

	public boolean isClientPlayer(World world) {
		if (!this.isPlayer())
			return false;

		EntityPlayer addressPlayer = this.getPlayer(world);
		EntityPlayer clientPlayer = Proxies.common.getPlayer();

		return addressPlayer != null && clientPlayer != null && clientPlayer.equals(addressPlayer);
	}

	public GameProfile getPlayerProfile() {
		if (!this.isPlayer())
			return null;
		return gameProfile;
	}
}
