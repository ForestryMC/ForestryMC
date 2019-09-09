package forestry.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.LogicalSide;

import forestry.api.climate.ClimateManager;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IWorldClimateHolder;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.network.packets.PacketClimatePlayer;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.TickHelper;

//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClimateHandlerServer {

	private static final TickHelper tickHelper = new TickHelper();
	private static IClimateState previousState = ClimateStateHelper.INSTANCE.absent();

	//TODO - register event handler
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.side != LogicalSide.SERVER) {
			return;
		}
		PlayerEntity player = event.player;
		World world = player.world;
		BlockPos pos = player.getPosition();
		IWorldClimateHolder worldClimateHolder = ClimateManager.climateRoot.getWorldClimate(world);
		IClimateState climateState = worldClimateHolder.getState(pos);
		tickHelper.onTick();
		if (tickHelper.updateOnInterval(100) && !climateState.equals(previousState)) {
			ClimateHandlerServer.previousState = climateState;
			NetworkUtil.sendToPlayer(new PacketClimatePlayer(climateState), player);
		}
	}
}
