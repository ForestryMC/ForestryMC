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
package forestry.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.utils.GeneticsUtil;

@SideOnly(Side.CLIENT)
public class TickHandlerCoreClient {

	private boolean hasNaturalistEye;

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == Phase.END) {
			Minecraft minecraft = Minecraft.getMinecraft();
			if (minecraft != null) {
				EntityPlayer player = minecraft.player;
				if (player != null) {
					boolean hasNaturalistEye = GeneticsUtil.hasNaturalistEye(player);
					if (this.hasNaturalistEye != hasNaturalistEye) {
						this.hasNaturalistEye = hasNaturalistEye;
						RenderGlobal renderGlobal = minecraft.renderGlobal;
						if (renderGlobal != null) {
							renderGlobal.markBlockRangeForRenderUpdate(
								(int) player.posX - 32, (int) player.posY - 32, (int) player.posZ - 32,
								(int) player.posX + 32, (int) player.posY + 32, (int) player.posZ + 32);
						}
					}
				}
			}
		}
	}
}
