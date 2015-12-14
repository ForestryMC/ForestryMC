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
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.util.FakePlayerFactory;

import forestry.core.access.IOwnable;

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

	/**
	 * Get a player for a given World and GameProfile.
	 * If they are not in the World, returns a FakePlayer.
	 * Do not store references to the return value, to prevent worlds staying in memory.
	 */
	public static EntityPlayer getPlayer(World world, GameProfile profile) {
		if (world == null) {
			throw new IllegalArgumentException("World cannot be null");
		}

		if (profile == null || profile.getName() == null) {
			if (world instanceof WorldServer) {
				return FakePlayerFactory.getMinecraft((WorldServer) world);
			} else {
				return null;
			}
		}
		
		EntityPlayer player = world.getPlayerEntityByName(profile.getName());
		if (player == null && world instanceof WorldServer) {
			player = FakePlayerFactory.get((WorldServer) world, profile);
		}
		return player;
	}
}
