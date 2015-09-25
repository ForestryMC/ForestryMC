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
package forestry.core.utils;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.mojang.authlib.GameProfile;

import forestry.core.access.IOwnable;

import buildcraft.api.tools.IToolWrench;

public abstract class PlayerUtil {

	//TODO: use null everywhere instead of an emptyUUID
	private static final UUID emptyUUID = new UUID(0, 0);

	public static boolean isSameGameProfile(GameProfile player1, GameProfile player2) {
		if (player1 == null || player2 == null) {
			return false;
		}

		UUID id1 = player1.getId();
		UUID id2 = player2.getId();
		if (id1 != null && id2 != null && !id1.equals(emptyUUID) && !id2.equals(emptyUUID)) {
			return id1.equals(id2);
		}

		return player1.getName() != null && player1.getName().equals(player2.getName());
	}

	public static String getOwnerName(IOwnable ownable) {
		GameProfile profile = ownable.getOwner();
		if (profile == null) {
			return StringUtil.localize("gui.derelict");
		} else {
			return profile.getName();
		}
	}

	public static boolean canWrench(EntityPlayer player, int x, int y, int z) {
		ItemStack itemstack = player.getCurrentEquippedItem();
		if (itemstack == null) {
			return false;
		}

		if (!(itemstack.getItem() instanceof IToolWrench)) {
			return false;
		}

		IToolWrench wrench = (IToolWrench) itemstack.getItem();
		return wrench.canWrench(player, x, y, z);
	}

	public static void useWrench(EntityPlayer player, int x, int y, int z) {
		ItemStack itemstack = player.getCurrentEquippedItem();

		if (itemstack == null) {
			return;
		}

		if (!(itemstack.getItem() instanceof IToolWrench)) {
			return;
		}

		IToolWrench wrench = (IToolWrench) itemstack.getItem();
		wrench.wrenchUsed(player, x, y, z);
	}
}
