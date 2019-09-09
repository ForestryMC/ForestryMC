package forestry.core.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

public class RenderUtil {

	private RenderUtil() {
	}

	public static void markForUpdate(BlockPos pos) {
		Minecraft.getInstance().worldRenderer.markForRerender(pos.getX(), pos.getY(), pos.getZ());
	}
}
