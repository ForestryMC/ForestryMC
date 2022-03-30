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

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.util.FakePlayerFactory;

public abstract class PlayerUtil {

	//TODO: use null everywhere instead of an emptyUUID
	private static final UUID emptyUUID = new UUID(0, 0);

	public static boolean isSameGameProfile(GameProfile player1, GameProfile player2) {
		UUID id1 = player1.getId();
		UUID id2 = player2.getId();
		if (id1 != null && id2 != null && !id1.equals(emptyUUID) && !id2.equals(emptyUUID)) {
			return id1.equals(id2);
		}

		return player1.getName() != null && player1.getName().equals(player2.getName());
	}

	public static String getOwnerName(@Nullable GameProfile profile) {
		if (profile == null) {
			return Translator.translateToLocal("for.gui.derelict");
		} else {
			return profile.getName();
		}
	}

	public static boolean actOnServer(Player player, Consumer<ServerPlayer> action) {
		if (player.level.isClientSide || !(player instanceof ServerPlayer)) {
			return false;
		}
		action.accept(asServer(player));
		return true;
	}

	public static LocalPlayer asClient(Player player) {
		if (!(player instanceof LocalPlayer)) {
			throw new IllegalStateException("Failed to cast player to its client version.");
		}
		return (LocalPlayer) player;
	}

	public static ServerPlayer asServer(Player player) {
		if (!(player instanceof ServerPlayer)) {
			throw new IllegalStateException("Failed to cast player to its server version.");
		}
		return (ServerPlayer) player;
	}

	/**
	 * Get a player for a given World and GameProfile.
	 * If they are not in the World, returns a FakePlayer.
	 * Do not store references to the return value, to prevent worlds staying in memory.
	 */
	@Nullable
	public static Player getPlayer(Level world, @Nullable GameProfile profile) {
		if (profile == null || profile.getName() == null) {
			if (world instanceof ServerLevel) {
				return FakePlayerFactory.getMinecraft((ServerLevel) world);
			} else {
				return null;
			}
		}

		Player player = world.getPlayerByUUID(profile.getId());
		if (player == null && world instanceof ServerLevel) {
			player = FakePlayerFactory.get((ServerLevel) world, profile);
		}
		return player;
	}

	/**
	 * Get a fake player for a given World and GameProfile.
	 * Do not store references to the return value, to prevent worlds staying in memory.
	 */
	@Nullable
	public static Player getFakePlayer(Level world, @Nullable GameProfile profile) {
		if (profile == null || profile.getName() == null) {
			if (world instanceof ServerLevel) {
				return FakePlayerFactory.getMinecraft((ServerLevel) world);
			} else {
				return null;
			}
		}

		if (world instanceof ServerLevel) {
			return FakePlayerFactory.get((ServerLevel) world, profile);
		}
		return null;
	}
}
