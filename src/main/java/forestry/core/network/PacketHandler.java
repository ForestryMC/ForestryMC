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

import java.io.InputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

import forestry.api.core.ForestryEvent;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.ISpeciesRoot;
import forestry.apiculture.multiblock.IAlvearyController;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.apiculture.network.PacketActiveUpdate;
import forestry.core.circuits.ContainerSolderingIron;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.genetics.BreedingTracker;
import forestry.core.gui.ContainerLiquidTanks;
import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.IGuiSelectable;
import forestry.core.interfaces.IRestrictedAccessTile;
import forestry.core.interfaces.ISocketable;
import forestry.core.proxy.Proxies;
import forestry.farming.multiblock.IFarmController;
import forestry.farming.multiblock.TileFarm;
import forestry.plugins.PluginManager;

import io.netty.buffer.ByteBufInputStream;

public class PacketHandler {
	private final FMLEventChannel channel;

	public PacketHandler() {
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(ForestryPacket.channel);
		channel.register(this);
	}

	@SubscribeEvent
	public void onPacket(ServerCustomPacketEvent event) {
		onPacketData(event.packet, ((NetHandlerPlayServer) event.handler).playerEntity);
	}

	@SubscribeEvent
	public void onPacket(ClientCustomPacketEvent event) {
		onPacketData(event.packet, Proxies.common.getPlayer());
	}

	/** Returns true if the packet has been handled */
	private static boolean onPacketData(FMLProxyPacket fmlPacket, EntityPlayer player) {
		InputStream is = new ByteBufInputStream(fmlPacket.payload());
		DataInputStreamForestry data = new DataInputStreamForestry(is);

		try {
			int packetIdOrdinal = data.readByte();
			if (packetIdOrdinal >= PacketId.VALUES.length) {
				return false;
			}
			PacketId packetId = PacketId.VALUES[packetIdOrdinal];

			switch (packetId) {

				case TILE_FORESTRY_UPDATE: {
					PacketTileStream.onPacketData(data);
					return true;
				}
				case TILE_FORESTRY_ERROR_UPDATE: {
					PacketErrorUpdate.onPacketData(data);
					return true;
				}
				case TILE_FORESTRY_GUI_OPENED: {
					PacketGuiUpdate.onPacketData(data);
					return true;
				}
				case TILE_FORESTRY_ACTIVE: {
					PacketActiveUpdate.onPacketData(data);
					return true;
				}
				case SOCKET_UPDATE: {
					PacketSocketUpdate packetS = new PacketSocketUpdate(data);
					onSocketUpdate(packetS);
					return true;
				}
				case FX_SIGNAL: {
					PacketFXSignal packet = new PacketFXSignal(data);
					packet.executeFX();
					return true;
				}

				case PIPETTE_CLICK: {
					PacketSlotClick packet = new PacketSlotClick(data);
					onPipetteClick(packet, (EntityPlayerMP) player);
					return true;
				}
				case SOLDERING_IRON_CLICK: {
					PacketSlotClick packet = new PacketSlotClick(data);
					onSolderingIronClick(packet, player);
					return true;
				}
				case CHIPSET_CLICK: {
					PacketSlotClick packet = new PacketSlotClick(data);
					onChipsetClick(packet, player);
					return true;
				}
				case ACCESS_SWITCH: {
					PacketCoordinates packet = new PacketCoordinates(data);
					onAccessSwitch(packet, player);
					return true;
				}
				case GUI_SELECTION_SET: {
					PacketGuiSelect packet = new PacketGuiSelect(data);
					onGuiSelection(packet);
					return true;
				}
				case GUI_SELECTION_CHANGE: {
					PacketGuiSelect packet = new PacketGuiSelect(data);
					onGuiChange(player, packet);
					return true;
				}
				case GUI_LAYOUT_SELECT: {
					PacketString packet = new PacketString(data);
					onGuiLayoutSelect(packet);
					return true;
				}
				case GENOME_TRACKER_UPDATE: {
					PacketNBT packet = new PacketNBT(data);
					onGenomeTrackerUpdate(packet);
					return true;
				}
				case GUI_PROGRESS_BAR: {
					PacketProgressBarUpdate.onPacketData(data);
					return true;
				}
				case GUI_ITEMSTACK: {
					PacketItemStackDisplay.onPacketData(data);
					return true;
				}
				case GUI_ENERGY: {
					PacketGuiEnergy.onPacketData(data);
					return true;
				}
				default: {
					for (IPacketHandler handler : PluginManager.packetHandlers) {
						if (handler.onPacketData(packetId, data, player)) {
							return true;
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return false;
	}

	public void sendPacket(FMLProxyPacket packet, EntityPlayerMP player) {
		channel.sendTo(packet, player);
	}

	private static void onGenomeTrackerUpdate(PacketNBT packet) {
		assert FMLCommonHandler.instance().getEffectiveSide().isClient();

		EntityPlayer player = Proxies.common.getPlayer();
		IBreedingTracker tracker = null;
		String type = packet.getTagCompound().getString(BreedingTracker.TYPE_KEY);

		ISpeciesRoot root = AlleleManager.alleleRegistry.getSpeciesRoot(type);
		if (root != null) {
			tracker = root.getBreedingTracker(Proxies.common.getRenderWorld(), player.getGameProfile());
		}
		if (tracker != null) {
			tracker.decodeFromNBT(packet.getTagCompound());
			MinecraftForge.EVENT_BUS.post(new ForestryEvent.SyncedBreedingTracker(tracker, player));
		}
	}

	private static void onGuiChange(EntityPlayer player, PacketGuiSelect packet) {
		assert FMLCommonHandler.instance().getEffectiveSide().isServer();

		if (!(player.openContainer instanceof IGuiSelectable)) {
			return;
		}

		((IGuiSelectable) player.openContainer).handleSelectionChange(player, packet);
	}

	private static void onGuiSelection(PacketGuiSelect packet) {
		assert FMLCommonHandler.instance().getEffectiveSide().isClient();

		EntityPlayer player = Proxies.common.getPlayer();

		Container container = player.openContainer;
		if (!(container instanceof IGuiSelectable)) {
			return;
		}

		((IGuiSelectable) container).setSelection(packet);
	}

	private static void onGuiLayoutSelect(PacketString packet) {
		assert FMLCommonHandler.instance().getEffectiveSide().isClient();

		EntityPlayer player = Proxies.common.getPlayer();

		Container container = player.openContainer;
		if (!(container instanceof ContainerSolderingIron)) {
			return;
		}

		((ContainerSolderingIron) container).setLayout(packet.getString());
	}

	private static void onSocketUpdate(PacketSocketUpdate packet) {
		assert FMLCommonHandler.instance().getEffectiveSide().isClient();

		TileEntity tile = packet.getTarget(Proxies.common.getRenderWorld());
		if (!(tile instanceof ISocketable)) {
			return;
		}

		ISocketable socketable = (ISocketable) tile;
		for (int i = 0; i < packet.itemStacks.length; i++) {
			socketable.setSocket(i, packet.itemStacks[i]);
		}
	}

	private static void onChipsetClick(PacketSlotClick packet, EntityPlayer player) {
		assert FMLCommonHandler.instance().getEffectiveSide().isServer();

		if (!(player.openContainer instanceof ContainerSocketed)) {
			return;
		}
		ItemStack itemstack = player.inventory.getItemStack();
		if (!(itemstack.getItem() instanceof ItemCircuitBoard)) {
			return;
		}

		((ContainerSocketed) player.openContainer).handleChipsetClick(packet.getSlot(), player, itemstack);
	}

	private static void onSolderingIronClick(PacketSlotClick packet, EntityPlayer player) {
		assert FMLCommonHandler.instance().getEffectiveSide().isServer();

		if (!(player.openContainer instanceof ContainerSocketed)) {
			return;
		}
		ItemStack itemstack = player.inventory.getItemStack();

		((ContainerSocketed) player.openContainer).handleSolderingIronClick(packet.getSlot(), player, itemstack);
	}

	private static void onAccessSwitch(PacketCoordinates packet, EntityPlayer playerEntity) {
		assert FMLCommonHandler.instance().getEffectiveSide().isServer();

		TileEntity tile = packet.getTarget(playerEntity.worldObj);

		if (tile instanceof TileAlveary) {
			TileAlveary tileAlveary = (TileAlveary) tile;
			IAlvearyController alvearyController = tileAlveary.getAlvearyController();
			alvearyController.getAccessHandler().switchAccessRule(playerEntity);
		} else if (tile instanceof TileFarm) {
			TileFarm tileFarm = (TileFarm) tile;
			IFarmController farmController = tileFarm.getFarmController();
			farmController.getAccessHandler().switchAccessRule(playerEntity);
		} else if (tile instanceof IRestrictedAccessTile) {
			IRestrictedAccessTile restrictedAccessTile = (IRestrictedAccessTile) tile;

			restrictedAccessTile.getAccessHandler().switchAccessRule(playerEntity);
		}
	}

	private static void onPipetteClick(PacketSlotClick packet, EntityPlayerMP player) {
		assert FMLCommonHandler.instance().getEffectiveSide().isServer();

		if (!(player.openContainer instanceof ContainerLiquidTanks)) {
			return;
		}

		((ContainerLiquidTanks) player.openContainer).handlePipetteClick(packet.getSlot(), player);
	}
}
