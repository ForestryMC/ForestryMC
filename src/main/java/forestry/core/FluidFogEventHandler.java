package forestry.core;

import forestry.core.config.Constants;
import forestry.core.fluids.ForestryFluids;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class FluidFogEventHandler {
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onFogColorRender(EntityViewRenderEvent.FogColors event) {
		if (event.getInfo().getFluidInCamera().getType() != Fluids.EMPTY)
			for (ForestryFluids fluid : ForestryFluids.values()){
				if (event.getInfo().getFluidInCamera().getType() == fluid.getFluid()){
					Color color = fluid.getParticleColor();
					event.setRed(color.getRed()/255f);
					event.setGreen(color.getGreen()/255f);
					event.setBlue(color.getBlue()/255f);
				}
				else if (event.getInfo().getFluidInCamera().getType() == fluid.getFlowing()){
					Color color = fluid.getParticleColor();
					event.setRed(color.getRed()/255f);
					event.setGreen(color.getGreen()/255f);
					event.setBlue(color.getBlue()/255f);
				}
			}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onFogDenseRender(EntityViewRenderEvent.FogDensity event) {
		if (event.getInfo().getFluidInCamera().getType() != Fluids.EMPTY)
			for (ForestryFluids fluid : ForestryFluids.values()){
				if (event.getInfo().getFluidInCamera().getType() == fluid.getFluid()){
					event.setDensity(80);
				}
				else if (event.getInfo().getFluidInCamera().getType() == fluid.getFlowing()){
					event.setDensity(80);
				}
			}
	}
}
