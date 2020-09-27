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

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.PostManager;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.WorldUtils;
import forestry.mail.gui.GuiMailboxInfo;
import forestry.mail.network.packets.PacketPOBoxInfoResponse;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandlerMailAlert {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END &&
            Minecraft.getInstance().world != null &&
            GuiMailboxInfo.instance.hasPOBoxInfo()) {
            //TODO: Test / Find a valid matrix stack
            GuiMailboxInfo.instance.render(new MatrixStack());
        }
    }

    @SubscribeEvent
    public void handlePlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();

        IMailAddress address = PostManager.postRegistry.getMailAddress(player.getGameProfile());
        POBox pobox = PostRegistry.getOrCreatePOBox(WorldUtils.asServer(player.world), address);
        PacketPOBoxInfoResponse packet = new PacketPOBoxInfoResponse(pobox.getPOBoxInfo());
        NetworkUtil.sendToPlayer(packet, player);
    }
}
