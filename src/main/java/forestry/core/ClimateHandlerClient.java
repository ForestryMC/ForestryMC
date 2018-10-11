package forestry.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.IClimateState;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.render.ParticleRender;

@SideOnly(Side.CLIENT)
public class ClimateHandlerClient {

	//The current climate state at the position of the player.
	private static IClimateState currentState = ClimateStateHelper.INSTANCE.absent();

	public static void setCurrentState(IClimateState currentState) {
		ClimateHandlerClient.currentState = currentState;
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.side != Side.CLIENT) {
			return;
		}
		EntityPlayer player = event.player;
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
