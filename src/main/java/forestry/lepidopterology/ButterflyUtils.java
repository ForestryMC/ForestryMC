package forestry.lepidopterology;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyCocoon;
import forestry.core.utils.Log;
import forestry.lepidopterology.entities.EntityButterfly;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ButterflyUtils {
	
	private static boolean attemptButterflySpawn(World world, IButterfly butterfly, BlockPos pos) {
		EntityLiving entityLiving = ButterflyManager.butterflyRoot.spawnButterflyInWorld(world, butterfly.copy(), pos.getX(), pos.getY() + 0.1f, pos.getZ());
		Log.trace("Spawned a butterfly '%s' at %s/%s/%s.", butterfly.getDisplayName(), pos.getX(), pos.getY(), pos.getZ());
		return entityLiving != null;
	}
	
	public static boolean spawnButterfly(IButterfly butterfly, World world, BlockPos pos){
		if (world.countEntities(EntityButterfly.class) > PluginLepidopterology.spawnConstraint) {
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
	
	public static boolean spawnButterfly(IButterflyCocoon cocoon, World world, BlockPos pos){
		IButterfly butterfly = cocoon.getCaterpillar();
		if (world.countEntities(EntityButterfly.class) > PluginLepidopterology.spawnConstraint) {
			return false;
		}

		if (world.isAirBlock(pos)) {
			return attemptButterflySpawn(world, butterfly, pos);
		}
		return false;
	}

	
}
