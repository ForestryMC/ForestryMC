/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.pipes.network;

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import forestry.core.network.ILocatedPacket;
import forestry.core.network.IPacketHandler;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketId;
import forestry.core.network.PacketNBT;
import forestry.core.proxy.Proxies;
import forestry.pipes.PipeItemsPropolis;
import forestry.pipes.PipeLogicPropolis;
import forestry.pipes.gui.ContainerPropolisPipe;

import buildcraft.transport.Pipe;
import buildcraft.transport.TileGenericPipe;

public class PacketHandlerPipes implements IPacketHandler {

	private static Pipe getPipe(World world, ILocatedPacket packet) {
		TileEntity tile = packet.getTarget(world);
		if (!(tile instanceof TileGenericPipe)) {
			return null;
		}

		return ((TileGenericPipe) tile).pipe;
	}

	@Override
	public boolean onPacketData(PacketId packetID, DataInputStream data, EntityPlayer player) throws IOException {

		switch (packetID) {
			// CLIENT
			case PROP_SEND_FILTER_SET: {
				PacketNBT packet = new PacketNBT(data);
				onFilterSet(packet);
				return true;
			}

			// SERVER
			case PROP_REQUEST_FILTER_SET: {
				PacketCoordinates packet = new PacketCoordinates(data);
				onRequestFilterSet(player, packet);
				return true;
			}
			case PROP_SEND_FILTER_CHANGE_TYPE: {
				PacketTypeFilterChange packet = new PacketTypeFilterChange(data);
				onTypeFilterChange(player, packet);
				return true;
			}
			case PROP_SEND_FILTER_CHANGE_GENOME: {
				PacketGenomeFilterChange packet = new PacketGenomeFilterChange(data);
				onGenomeFilterChange(player, packet);
				return true;
			}

		}

		return false;
	}

	private static void onFilterSet(PacketNBT packet) {
		Container container = Proxies.common.getClientInstance().thePlayer.openContainer;

		if (container instanceof ContainerPropolisPipe) {
			PipeLogicPropolis pipeLogic = ((ContainerPropolisPipe) container).pipeLogic;
			pipeLogic.handleFilterSet(packet);
		}
	}

	private static void onTypeFilterChange(EntityPlayer player, PacketTypeFilterChange packet) {

		Pipe pipe = getPipe(player.worldObj, packet);
		if (pipe == null) {
			return;
		}

		if (pipe instanceof PipeItemsPropolis) {
			((PipeItemsPropolis) pipe).pipeLogic.handleTypeFilterChange(packet);
		}

	}

	private static void onGenomeFilterChange(EntityPlayer player, PacketGenomeFilterChange packet) {

		Pipe pipe = getPipe(player.worldObj, packet);
		if (pipe == null) {
			return;
		}

		if (pipe instanceof PipeItemsPropolis) {
			((PipeItemsPropolis) pipe).pipeLogic.handleGenomeFilterChange(packet);
		}
	}

	private static void onRequestFilterSet(EntityPlayer player, PacketCoordinates packet) {

		Pipe pipe = getPipe(player.worldObj, packet);
		if (pipe == null) {
			return;
		}

		if (pipe instanceof PipeItemsPropolis) {
			((PipeItemsPropolis) pipe).pipeLogic.sendFilterSet(player);
		}
	}
}
