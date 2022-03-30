package forestry.core;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import forestry.api.climate.IClimateState;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.config.Constants;
import forestry.core.render.ParticleRender;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ClimateHandlerClient {

	//The current climate state at the position of the player.
	private static IClimateState currentState = ClimateStateHelper.INSTANCE.absent();

	public static void setCurrentState(IClimateState currentState) {
		ClimateHandlerClient.currentState = currentState;
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.side != LogicalSide.CLIENT) {
			return;
		}
		Player player = event.player;
		Level world = player.level;
		BlockPos pos = player.blockPosition();
		if (currentState.isPresent()) {
			int x = world.random.nextInt(11) - 5;
			int y = world.random.nextInt(5) - 1;
			int z = world.random.nextInt(11) - 5;
			ParticleRender.addClimateParticles(world, pos.offset(x, y, z), world.random, currentState.getTemperatureEnum(), currentState.getHumidityEnum());
		}
	}
}
