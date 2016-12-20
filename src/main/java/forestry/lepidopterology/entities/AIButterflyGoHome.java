package forestry.lepidopterology.entities;

import net.minecraft.util.math.Vec3d;

public class AIButterflyGoHome extends AIButterflyMovement {

	public AIButterflyGoHome(EntityButterfly entity) {
		super(entity);
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (entity.isWithinHomeDistanceCurrentPosition()) {
			return false;
		}

		flightTarget = new Vec3d(entity.getHomePosition());
		if (flightTarget.equals(Vec3d.ZERO)) {
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
