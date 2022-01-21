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
package forestry.apiculture.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.common.util.LazyOptional;

import forestry.api.climate.IClimateListener;
import forestry.apiculture.features.ApicultureContainers;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.core.climate.ClimateRoot;
import forestry.core.gui.ContainerTile;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.core.tiles.TileUtil;

public class ContainerAlveary extends ContainerTile<TileAlveary> {

	public static ContainerAlveary fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data) {
		TileAlveary tile = TileUtil.getTile(inv.player.level, data.readBlockPos(), TileAlveary.class);
		return new ContainerAlveary(windowId, inv, tile);    //TODO nullability.
	}

	public ContainerAlveary(int windowid, Inventory playerInv, TileAlveary tile) {
		super(windowid, ApicultureContainers.ALVEARY.containerType(), playerInv, tile, 8, 108);
		ContainerBeeHelper.addSlots(this, tile, false);

		tile.getBeekeepingLogic().clearCachedValues();
		LazyOptional<IClimateListener> listener = ClimateRoot.getInstance().getListener(tile.getLevel(), tile.getBlockPos());
		if (playerInv.player instanceof ServerPlayer) {
			listener.ifPresent(l -> l.syncToClient((ServerPlayer) playerInv.player));
		}
	}

	private int beeProgress = -1;

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		int beeProgress = tile.getBeekeepingLogic().getBeeProgressPercent();
		if (this.beeProgress != beeProgress) {
			this.beeProgress = beeProgress;
			IForestryPacketClient packet = new PacketGuiUpdate(tile);
			sendPacketToListeners(packet);
		}
	}
}
