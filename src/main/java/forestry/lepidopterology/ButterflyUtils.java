package forestry.lepidopterology;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.utils.Log;
import forestry.lepidopterology.entities.EntityButterfly;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ButterflyUtils {
    static boolean attemptButterflySpawn(World world, IButterfly butterfly, BlockPos pos) {
        MobEntity entityLiving = ButterflyManager.butterflyRoot.spawnButterflyInWorld(
                world,
                butterfly.copy(),
                pos.getX(),
                pos.getY() + 0.1f,
                pos.getZ()
        );
        Log.trace(
                "Spawned a butterfly '{}' at {}/{}/{}.",
                butterfly.getDisplayName(),
                pos.getX(),
                pos.getY(),
                pos.getZ()
        );
        return entityLiving != null;
    }

    public static boolean spawnButterfly(IButterfly butterfly, World world, BlockPos pos) {
        if (countButterfly(world) > ModuleLepidopterology.spawnConstraint) {
            return false;
        }

        if (!butterfly.canSpawn(world, pos.getX(), pos.getY(), pos.getZ())) {
            return false;
        }

        if (world.isAirBlock(pos)) {
            return attemptButterflySpawn(world, butterfly, pos);
        }
        return false;
    }

    public static boolean spawnButterflyWithoutCheck(IButterfly butterfly, World world, BlockPos pos) {
        if (countButterfly(world) > ModuleLepidopterology.spawnConstraint) {
            return false;
        }

        if (world.isAirBlock(pos)) {
            return attemptButterflySpawn(world, butterfly, pos);
        }

        return false;
    }

    public static int countButterfly(World world) {
        int number = (int) ((ServerWorld) world).getEntities()
                                                .filter(entity -> entity instanceof EntityButterfly)
                                                .count();

        return number;
    }
}
