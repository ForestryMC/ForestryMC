package forestry.core.multiblock;

import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MultiblockClientTickHandler {

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			MultiblockRegistry.tickStart(Minecraft.getMinecraft().theWorld);
		}
	}
}
