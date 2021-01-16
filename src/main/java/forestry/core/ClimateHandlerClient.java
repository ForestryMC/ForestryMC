package forestry.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.LogicalSide;

import forestry.api.climate.IClimateState;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.render.ParticleRender;

@OnlyIn(Dist.CLIENT)
public class ClimateHandlerClient {

	//The current climate state at the position of the player.
	private static IClimateState currentState = ClimateStateHelper.INSTANCE.absent();

	public static void setCurrentState(IClimateState currentState) {
		ClimateHandlerClient.currentState = currentState;
	}

	//TODO - register event handler
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.side != LogicalSide.CLIENT) {
			return;
		}
		PlayerEntity player = event.player;
		World world = player.world;
		BlockPos pos = player.getPosition();
		if (currentState.isPresent()) {
			int x = world.rand.nextInt(11) - 5;
			int y = world.rand.nextInt(5) - 1;
			int z = world.rand.nextInt(11) - 5;
			ParticleRender.addClimateParticles(world, pos.add(x, y, z), world.rand, currentState.getTemperatureEnum(), currentState.getHumidityEnum());
		}
	}
}
