package forestry.lepidopterology.entities;

import forestry.lepidopterology.PluginLepidopterology;
import net.minecraft.util.math.Vec3d;

public class AIButterflyGoHome extends AIButterflyMovement {

	public AIButterflyGoHome(EntityButterfly entity) {
		super(entity);
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (PluginLepidopterology.maxDistance > entity.getDistanceSqToCenter(entity.getHomePosition())) {
			return false;
		}

		flightTarget = new Vec3d(entity.getHomePosition());
		if (flightTarget == null) {
			if (entity.getState().doesMovement) {
				entity.setState(EnumButterflyState.HOVER);
			}
			return false;
		}

		entity.setDestination(flightTarget);
		entity.setState(EnumButterflyState.FLYING);
		return true;
	}
	
}
