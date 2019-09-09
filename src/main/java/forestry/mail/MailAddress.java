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

import com.google.common.base.Preconditions;

import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.nbt.CompoundNBT;

import com.mojang.authlib.GameProfile;

import forestry.api.core.INbtWritable;
import forestry.api.mail.EnumAddressee;
import forestry.api.mail.IMailAddress;
import forestry.core.utils.PlayerUtil;

public class MailAddress implements INbtWritable, IMailAddress {

	private static final GameProfile invalidGameProfile = new GameProfile(new UUID(0, 0), "");

	private final EnumAddressee type;
	private final GameProfile gameProfile; // gameProfile is a fake GameProfile for traders, and real for players

	public MailAddress() {
		this.type = EnumAddressee.PLAYER;
		this.gameProfile = invalidGameProfile;
	}

	public MailAddress(GameProfile gameProfile) {
		Preconditions.checkNotNull(gameProfile, "gameProfile must not be null");

		this.type = EnumAddressee.PLAYER;
		this.gameProfile = gameProfile;
	}

	public MailAddress(String name) {
		Preconditions.checkNotNull(name, "name must not be null");
		Preconditions.checkArgument(StringUtils.isNotBlank(name), "name must not be blank");

		this.type = EnumAddressee.TRADER;
		this.gameProfile = new GameProfile(null, name);
	}

	public MailAddress(CompoundNBT nbt) {
		EnumAddressee type = null;
		GameProfile gameProfile = invalidGameProfile;
		if (nbt.contains("TP")) {
			String typeName = nbt.getString("TP");
			type = EnumAddressee.fromString(typeName);
		}

		if (type == null) {
			type = EnumAddressee.PLAYER;
			gameProfile = invalidGameProfile;
		} else if (nbt.contains("profile")) {
			CompoundNBT profileTag = nbt.getCompound("profile");
			gameProfile = PlayerUtil.readGameProfile(profileTag);
			if (gameProfile == null) {
				gameProfile = invalidGameProfile;
			}
		}

		this.type = type;
		this.gameProfile = gameProfile;
	}

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
	public GameProfile getPlayerProfile() {
		if (this.type != EnumAddressee.PLAYER) {
			return invalidGameProfile;
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
		if (getType() == EnumAddressee.PLAYER) {
			return type + "-" + name + '-' + gameProfile.getId();
		} else {
			return type + "-" + name;
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compoundNBT) {
		compoundNBT.putString("TP", type.toString());

		if (gameProfile != invalidGameProfile) {
			CompoundNBT profileNbt = new CompoundNBT();
			PlayerUtil.writeGameProfile(profileNbt, gameProfile);
			compoundNBT.put("profile", profileNbt);
		}
		return compoundNBT;
	}
}
