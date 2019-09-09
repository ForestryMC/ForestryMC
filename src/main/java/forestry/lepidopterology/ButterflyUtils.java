package forestry.lepidopterology;

import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.utils.Log;

public class ButterflyUtils {

	static boolean attemptButterflySpawn(World world, IButterfly butterfly, BlockPos pos) {
		MobEntity entityLiving = ButterflyManager.butterflyRoot.spawnButterflyInWorld(world, butterfly.copy(), pos.getX(), pos.getY() + 0.1f, pos.getZ());
		Log.trace("Spawned a butterfly '{}' at {}/{}/{}.", butterfly.getDisplayName(), pos.getX(), pos.getY(), pos.getZ());
		return entityLiving != null;
	}

	public static boolean spawnButterfly(IButterfly butterfly, World world, BlockPos pos) {
		//TODO needs server world I think
		if (false) {//world.countEntities(EntityButterfly.class) > ModuleLepidopterology.spawnConstraint) {
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
		if (false) {//TODO world.countEntities(EntityButterfly.class) > ModuleLepidopterology.spawnConstraint) {
			return false;
		}
		if (world.isAirBlock(pos)) {
			return attemptButterflySpawn(world, butterfly, pos);
		}
		return false;
	}


}
