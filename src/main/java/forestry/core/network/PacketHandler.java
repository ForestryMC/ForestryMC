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
package forestry.core.network;

import java.io.DataInputStream;
import java.io.InputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import forestry.api.core.ForestryEvent;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.gadgets.TileForestry;
import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.IGuiSelectable;
import forestry.core.interfaces.ISocketable;
import forestry.core.proxy.Proxies;
import forestry.plugins.PluginManager;

import io.netty.buffer.ByteBufInputStream;

public class PacketHandler {
	public PacketHandler() {
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(ForestryPacket.channel);
		channel.register(this);
	}

	@SubscribeEvent
	public void onPacket(ServerCustomPacketEvent event) {
		onPacketData(new ByteBufInputStream(event.packet.payload()),
				((NetHandlerPlayServer) event.handler).playerEntity);
	}

	@SubscribeEvent
	public void onPacket(ClientCustomPacketEvent event) {
		onPacketData(new ByteBufInputStream(event.packet.payload()),
				null);
	}

	public void onPacketData(InputStream is, EntityPlayerMP player) {
		DataInputStream data = new DataInputStream(is);
		PacketUpdate packetU;

		try {

			int packetId = data.readByte();

			switch (packetId) {

				case PacketIds.TILE_FORESTRY_UPDATE:
					PacketTileUpdate packetT = new PacketTileUpdate();
					packetT.readData(data);
					onTileUpdate(packetT);
					break;
				case PacketIds.TILE_UPDATE:
					PacketUpdate packetUpdate = new PacketUpdate();
					packetUpdate.readData(data);
					onTileUpdate(packetUpdate);
					break;
				case PacketIds.TILE_NBT:
					PacketTileNBT packetN = new PacketTileNBT();
					packetN.readData(data);
					onTileUpdate(packetN);
					break;
				case PacketIds.SOCKET_UPDATE:
					PacketSocketUpdate packetS = new PacketSocketUpdate();
					packetS.readData(data);
					onSocketUpdate(packetS);
					break;
				case PacketIds.IINVENTORY_STACK:
					PacketInventoryStack packetQ = new PacketInventoryStack();
					packetQ.readData(data);
					onInventoryStack(packetQ);
					break;
				case PacketIds.FX_SIGNAL:
					PacketFXSignal packetF = new PacketFXSignal();
					packetF.readData(data);
					packetF.executeFX();
					break;

				case PacketIds.PIPETTE_CLICK:
					packetU = new PacketUpdate();
					packetU.readData(data);
					onPipetteClick(packetU, player);
					break;
				case PacketIds.SOLDERING_IRON_CLICK:
					packetU = new PacketUpdate();
					packetU.readData(data);
					onSolderingIronClick(packetU, player);
					break;
				case PacketIds.CHIPSET_CLICK:
					packetU = new PacketUpdate();
					packetU.readData(data);
					onChipsetClick(packetU, player);
					break;
				case PacketIds.ACCESS_SWITCH:
					PacketCoordinates packetC = new PacketCoordinates();
					packetC.readData(data);
					onAccessSwitch(packetC, player);
					break;
				case PacketIds.GUI_SELECTION:
					PacketUpdate packetI = new PacketUpdate();
					packetI.readData(data);
					onGuiSelection(packetI);
					break;
				case PacketIds.GUI_SELECTION_CHANGE:
					PacketUpdate packetZ = new PacketUpdate();
					packetZ.readData(data);
					onGuiChange(player, packetZ);
					break;
				case PacketIds.GENOME_TRACKER_UPDATE:
					PacketNBT packetTR = new PacketNBT();
					packetTR.readData(data);
					onGenomeTrackerUpdate(packetTR);
					break;
				case PacketIds.GUI_INTEGER:
					PacketGuiInteger packet = new PacketGuiInteger();
					packet.readData(data);
					break;
				default:
					for (forestry.core.interfaces.IPacketHandler handler : PluginManager.packetHandlers) {
						handler.onPacketData(packetId, data, player);
					}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void sendPacket(FMLProxyPacket packet) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			channel.sendToServer(packet);
		} else {
			channel.sendToAll(packet);
		}
	}

	public void sendPacket(FMLProxyPacket packet, EntityPlayerMP player) {
		channel.sendTo(packet, player);
	}

	private void onGenomeTrackerUpdate(PacketNBT packet) {
		assert FMLCommonHandler.instance().getEffectiveSide().isClient();

		EntityPlayer player = Proxies.common.getPlayer();
		IBreedingTracker tracker = null;
		String type = packet.getTagCompound().getString("TYPE");

		ISpeciesRoot root = AlleleManager.alleleRegistry.getSpeciesRoot(type);
		if (root != null) {
			tracker = root.getBreedingTracker(Proxies.common.getRenderWorld(), player.getGameProfile());
		}
		if (tracker != null) {
			tracker.decodeFromNBT(packet.getTagCompound());
			MinecraftForge.EVENT_BUS.post(new ForestryEvent.SyncedBreedingTracker(tracker, player));
		}
	}

	private void onGuiChange(EntityPlayer player, PacketUpdate packet) {
		assert FMLCommonHandler.instance().getEffectiveSide().isServer();

		if (!(player.openContainer instanceof IGuiSelectable)) {
			return;
		}

		((IGuiSelectable) player.openContainer).handleSelectionChange(player, packet);
	}

	private void onGuiSelection(PacketUpdate packet) {
		assert FMLCommonHandler.instance().getEffectiveSide().isClient();

		EntityPlayer player = Proxies.common.getPlayer();

		Container container = player.openContainer;
		if (!(container instanceof IGuiSelectable)) {
			return;
		}

		((IGuiSelectable) container).setSelection(packet);

	}

	private void onSocketUpdate(PacketSocketUpdate packet) {
		assert FMLCommonHandler.instance().getEffectiveSide().isClient();

		TileEntity tile = Proxies.common.getRenderWorld().getTileEntity(packet.pos);
		if (!(tile instanceof ISocketable)) {
			return;
		}

		ISocketable socketable = (ISocketable) tile;
		for (int i = 0; i < packet.itemstacks.length; i++) {
			socketable.setSocket(i, packet.itemstacks[i]);
		}
	}

	private void onTileUpdate(ForestryPacket packet) {

		TileEntity tile = ((ILocatedPacket) packet).getTarget(Proxies.common.getRenderWorld());
		if (tile instanceof INetworkedEntity) {
			((INetworkedEntity) tile).fromPacket(packet);
		}

	}

	private void onInventoryStack(PacketInventoryStack packet) {

		TileEntity tile = Proxies.common.getRenderWorld().getTileEntity(packet.pos);
		if (tile == null) {
			return;
		}

		if (tile instanceof IInventory) {
			((IInventory) tile).setInventorySlotContents(packet.slotIndex, packet.itemstack);
		}
	}

	private void onChipsetClick(PacketUpdate packet, EntityPlayer player) {
		assert FMLCommonHandler.instance().getEffectiveSide().isServer();

		if (!(player.openContainer instanceof ContainerSocketed)) {
			return;
		}
		ItemStack itemstack = player.inventory.getItemStack();
		if (!(itemstack.getItem() instanceof ItemCircuitBoard)) {
			return;
		}

		((ContainerSocketed) player.openContainer).handleChipsetClick(packet.payload.intPayload[0], player, itemstack);

	}

	private void onSolderingIronClick(PacketUpdate packet, EntityPlayer player) {
		assert FMLCommonHandler.instance().getEffectiveSide().isServer();

		if (!(player.openContainer instanceof ContainerSocketed)) {
			return;
		}
		ItemStack itemstack = player.inventory.getItemStack();

		((ContainerSocketed) player.openContainer).handleSolderingIronClick(packet.payload.intPayload[0], player, itemstack);
	}

	private void onAccessSwitch(PacketCoordinates packet, EntityPlayer playerEntity) {
		assert FMLCommonHandler.instance().getEffectiveSide().isServer();

		TileForestry tile = (TileForestry) playerEntity.worldObj.getTileEntity(packet.pos);
		if (tile == null) {
			return;
		}

		tile.switchAccessRule(playerEntity);
	}

	private void onPipetteClick(PacketUpdate packet, EntityPlayerMP player) {
		assert FMLCommonHandler.instance().getEffectiveSide().isServer();

		if (!(player.openContainer instanceof ContainerLiquidTanks)) {
			return;
		}

		((ContainerLiquidTanks) player.openContainer).handlePipetteClick(packet.payload.intPayload[0], player);
	}

	private final FMLEventChannel channel;
}
