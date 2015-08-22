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
package forestry.farming;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.core.GuiHandlerBase;
import forestry.core.network.GuiId;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketSocketUpdate;
import forestry.core.proxy.Proxies;
import forestry.farming.gadgets.TileFarmPlain;
import forestry.farming.gui.ContainerFarm;
import forestry.farming.gui.GuiFarm;

public class GuiHandlerFarming extends GuiHandlerBase {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {
			case MultiFarmGUI:
				TileFarmPlain tile = (TileFarmPlain) getTileForestry(world, new BlockPos(x, y, z));
				Proxies.net.sendToPlayer(new PacketSocketUpdate(PacketIds.SOCKET_UPDATE, x, y, z, tile), player);
				return new ContainerFarm(player.inventory, tile);
			default:
				return null;

		}
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id >= GuiId.values().length) {
			return null;
		}

		switch (GuiId.values()[id]) {
			case MultiFarmGUI:
				return new GuiFarm(player, (TileFarmPlain) getTileForestry(world, new BlockPos(x, y, z)));
			default:
				return null;

		}
	}

}
