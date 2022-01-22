package forestry.core;

import javax.annotation.Nullable;
import java.awt.Color;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;

import forestry.core.config.Constants;
import forestry.core.fluids.ForestryFluids;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class FluidFogEventHandler {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onFogColorRender(EntityViewRenderEvent.FogColors event) {
		// if (event.getCamera().getFluidInCamera().getType() != Fluids.EMPTY) {
		// 	if (isForestryFluid(event.getCamera().getFluidInCamera())) {
		// 		Color color = getForestryFluid(event.getCamera().getFluidInCamera()).getParticleColor();
		// 		event.setRed(color.getRed() / 255f);
		// 		event.setGreen(color.getGreen() / 255f);
		// 		event.setBlue(color.getBlue() / 255f);
		// 	}
		// }
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onFogDenseRender(EntityViewRenderEvent.FogDensity event) {
		// if (event.getCamera().getFluidInCamera().getType() != Fluids.EMPTY) {
		// 	if (isForestryFluid(event.getCamera().getFluidInCamera())) {
		// 		event.setDensity(80);
		// 	}
		// }
	}

	public static boolean isForestryFluid(FluidState fluid) {
		return getForestryFluid(fluid) != null;
	}

	@Nullable
	public static ForestryFluids getForestryFluid(FluidState fluid) {
		for (ForestryFluids ffluid : ForestryFluids.values()) {
			if (fluid.getType() == ffluid.getFluid() || fluid.getType() == ffluid.getFlowing()) {
				return ffluid;
			}
		}
		return null;
	}
}
