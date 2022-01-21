package forestry.core.utils;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.PrimaryLevelData;

public final class WorldUtils {

	private WorldUtils() {
	}

	@Nullable
	public static ClientLevel clientSafe() {
		return Minecraft.getInstance().level;
	}

	public static ClientLevel client() {
		ClientLevel world = clientSafe();
		if (world == null) {
			throw new IllegalStateException("Failed to get client side world.");
		}
		return world;
	}

	public static ClientLevel asClient(LevelAccessor world) {
		if (!(world instanceof ClientLevel)) {
			throw new IllegalStateException("Failed to cast world to its client version.");
		}
		return (ClientLevel) world;
	}

	public static ServerLevel asServer(LevelAccessor world) {
		if (!(world instanceof ServerLevel)) {
			throw new IllegalStateException("Failed to cast world to its server version.");
		}
		return (ServerLevel) world;
	}

	public static ServerLevelData getServerInfo(Level world) {
		LevelData info = world.getLevelData();
		if (!(info instanceof PrimaryLevelData)) {
			throw new IllegalStateException("Failed to cast the world to its server version.");
		}
		return (PrimaryLevelData) info;
	}

}
