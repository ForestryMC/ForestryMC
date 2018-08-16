package forestry.climatology;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import forestry.api.climate.ClimateManager;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IWorldClimateHolder;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.network.packets.PacketClimatePlayer;
import forestry.core.utils.NetworkUtil;

public class ClimateHandlerServer {

	private static IClimateState previousState = ClimateStateHelper.INSTANCE.absent();

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.side != Side.SERVER) {
			return;
		}
		EntityPlayer player = event.player;
		World world = player.world;
		BlockPos pos = player.getPosition();
		IWorldClimateHolder worldClimateHolder = ClimateManager.climateRoot.getWorldClimate(world);
		IClimateState climateState = worldClimateHolder.getState(pos);
		if (world.getTotalWorldTime() % 100 == 0
			&& !climateState.equals(previousState)) {
			ClimateHandlerServer.previousState = climateState;
			NetworkUtil.sendToPlayer(new PacketClimatePlayer(climateState), player);
		}
	}
}
