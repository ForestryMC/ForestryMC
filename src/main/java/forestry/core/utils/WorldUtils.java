package forestry.core.utils;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraft.world.storage.ServerWorldInfo;

public final class WorldUtils {

    private WorldUtils() {
    }

    public static ClientWorld asClient(World world) {
        if (!(world instanceof ClientWorld)) {
            throw new IllegalStateException("Failed to cast world to its client version.");
        }
        return (ClientWorld) world;
    }

    public static ServerWorld asServer(World world) {
        if (!(world instanceof ServerWorld)) {
            throw new IllegalStateException("Failed to cast world to its server version.");
        }
        return (ServerWorld) world;
    }

    public static IServerWorldInfo getServerInfo(World world) {
        IWorldInfo info = world.getWorldInfo();
        if (!(info instanceof ServerWorldInfo)) {
            throw new IllegalStateException("Failed to cast the world to its server version.");
        }
        return (ServerWorldInfo) info;
    }
}
