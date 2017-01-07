/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.mail;


import forestry.mail.gui.GuiMailboxInfo;
import forestry.mail.network.packets.PacketPOBoxInfoRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class EventHandlerMailAlert {
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (event.phase == Phase.END &&
				Minecraft.getMinecraft().theWorld != null &&
				GuiMailboxInfo.instance.hasPOBoxInfo()) {
			GuiMailboxInfo.instance.render();
		}
	}

	@SubscribeEvent
	public void handleWorldLoad(WorldEvent.Load event) {
		World world = event.getWorld();
		if (world.isRemote) {
			PacketPOBoxInfoRequest packet = new PacketPOBoxInfoRequest();
			world.sendPacketToServer(packet.getPacket());
		}
	}
}
