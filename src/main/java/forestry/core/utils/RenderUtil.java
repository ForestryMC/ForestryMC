package forestry.core.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.fml.DistExecutor;

/**
 * Util methods used at the runtime of the game based around the rendering.
 */
public class RenderUtil {

	private RenderUtil() {
	}

	public static void markForUpdate(BlockPos pos) {
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().levelRenderer.setBlocksDirty(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ()));
	}
}
