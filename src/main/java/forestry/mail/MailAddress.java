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

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

import com.mojang.authlib.GameProfile;

import forestry.api.core.INbtWritable;
import forestry.api.mail.EnumAddressee;
import forestry.api.mail.IMailAddress;
import forestry.core.utils.PlayerUtil;

public class MailAddress implements INbtWritable, IMailAddress {

	private static final GameProfile invalidGameProfile = new GameProfile(new UUID(0, 0), "");

	@Nonnull
	private final EnumAddressee type;
	@Nonnull
	private final GameProfile gameProfile; // gameProfile is a fake GameProfile for traders, and real for players

	public MailAddress() {
		this.type = EnumAddressee.PLAYER;
		this.gameProfile = invalidGameProfile;
	}

	public MailAddress(@Nonnull GameProfile gameProfile) {
		if (gameProfile == null) {
			throw new IllegalArgumentException("gameProfile must not be null");
		}

		this.type = EnumAddressee.PLAYER;
		this.gameProfile = gameProfile;
	}

	public MailAddress(@Nonnull String name) {
		if (name == null) {
			throw new IllegalArgumentException("name must not be null");
		}

		this.type = EnumAddressee.TRADER;
		this.gameProfile = new GameProfile(null, name);
	}

	public MailAddress(@Nonnull NBTTagCompound nbt) {
		EnumAddressee type = null;
		GameProfile gameProfile = null;
		if (nbt.hasKey("TP")) {
			String typeName = nbt.getString("TP");
			type = EnumAddressee.fromString(typeName);
		}

		if (type == null) {
			type = EnumAddressee.PLAYER;
			gameProfile = invalidGameProfile;
		} else if (nbt.hasKey("profile")) {
			NBTTagCompound profileTag = nbt.getCompoundTag("profile");
			gameProfile = NBTUtil.readGameProfileFromNBT(profileTag);
		}

		this.type = type;
		this.gameProfile = gameProfile;
	}

	@Nonnull
	@Override
	public EnumAddressee getType() {
		return type;
	}

	@Override
	public String getName() {
		return gameProfile.getName();
	}

	@Override
	public boolean isValid() {
		return gameProfile.getName() != null && !PlayerUtil.isSameGameProfile(gameProfile, invalidGameProfile);
	}

	@Override
	public boolean isPlayer() {
		return type == EnumAddressee.PLAYER;
	}

	@Override
	public boolean isTrader() {
		return type == EnumAddressee.TRADER;
	}

	@Override
	public GameProfile getPlayerProfile() {
		if (!this.isPlayer()) {
			return null;
		}
		return gameProfile;
	}

	@Override
	public int hashCode() {
		return gameProfile.getName().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MailAddress)) {
			return false;
		}

		MailAddress address = (MailAddress) o;
		return PlayerUtil.isSameGameProfile(address.gameProfile, gameProfile);
	}

	@Override
	public String toString() {
		String name = getName().toLowerCase(Locale.ENGLISH);
		if (isPlayer()) {
			return type + "-" + name + '-' + gameProfile.getId();
		} else {
			return type + "-" + name;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setString("TP", type.toString());

		if (gameProfile != null) {
			NBTTagCompound profileNbt = new NBTTagCompound();
			NBTUtil.writeGameProfile(profileNbt, gameProfile);
			nbttagcompound.setTag("profile", profileNbt);
		}
	}
}
