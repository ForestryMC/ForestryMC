package forestry.core.utils;

import com.mojang.authlib.GameProfile;

import java.util.UUID;

public class PlayerUtil {

	public static boolean isSameGameProfile(GameProfile player1, GameProfile player2) {
		if (player1 == null || player2 == null)
			return false;

		UUID id1 = player1.getId();
		UUID id2 = player2.getId();
		if (id1 != null && id2 != null)
			return id1.equals(id2);

		return player1.getName() != null && player1.getName().equals(player2.getName());
	}

}
