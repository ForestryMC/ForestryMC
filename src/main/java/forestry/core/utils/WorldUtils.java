package forestry.core.utils;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraft.world.storage.ServerWorldInfo;

public final class WorldUtils {

	private WorldUtils() {
	}

	@Nullable
	public static ClientWorld clientSafe() {
		return Minecraft.getInstance().level;
	}

	public static ClientWorld client() {
		ClientWorld world = clientSafe();
		if (world == null) {
			throw new IllegalStateException("Failed to get client side world.");
		}
		return world;
	}

	public static ClientWorld asClient(IWorld world) {
		if (!(world instanceof ClientWorld)) {
			throw new IllegalStateException("Failed to cast world to its client version.");
		}
		return (ClientWorld) world;
	}

	public static ServerWorld asServer(IWorld world) {
		if (!(world instanceof ServerWorld)) {
			throw new IllegalStateException("Failed to cast world to its server version.");
		}
		return (ServerWorld) world;
	}

	public static IServerWorldInfo getServerInfo(World world) {
		IWorldInfo info = world.getLevelData();
		if (!(info instanceof ServerWorldInfo)) {
			throw new IllegalStateException("Failed to cast the world to its server version.");
		}
		return (ServerWorldInfo) info;
	}

}
