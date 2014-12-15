package forestry.core.utils;

import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class PlayerUtil {

	//TODO: use null everywhere instead of an emptyUUID
	private static final UUID emptyUUID = new UUID(0,0);

	public static boolean isSameGameProfile(GameProfile player1, GameProfile player2) {
		if (player1 == null || player2 == null)
			return false;

		UUID id1 = player1.getId();
		UUID id2 = player2.getId();
		if (id1 != null && id2 != null && !id1.equals(emptyUUID) && !id2.equals(emptyUUID))
			return id1.equals(id2);

		return player1.getName() != null && player1.getName().equals(player2.getName());
	}

}
