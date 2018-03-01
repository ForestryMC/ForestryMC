
package forestry.core.utils;

import java.util.Random;

public final class TickHelper {
	private static final Random rand = new Random();
	private int tickCount = rand.nextInt(2048);

	public void onTick() {
		tickCount++;
	}

	public boolean updateOnInterval(int tickInterval) {
		return tickCount % tickInterval == 0;
	}
}