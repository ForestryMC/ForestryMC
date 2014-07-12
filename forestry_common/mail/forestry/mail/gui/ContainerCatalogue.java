/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail.gui;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;

import com.mojang.authlib.GameProfile;

import forestry.api.mail.ITradeStation;
import forestry.api.mail.PostManager;
import forestry.api.mail.TradeStationInfo;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.IGuiSelectable;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.mail.EnumStationState;
import forestry.mail.network.PacketTradeInfo;

public class ContainerCatalogue extends ContainerForestry implements IGuiSelectable {

	private final EntityPlayer player;
	private final LinkedHashMap<GameProfile, ITradeStation> stations;
	private TradeStationInfo currentTrade = null;
	private Iterator<ITradeStation> iterator = null;

	private int maxItPos = 0;
	private int currentItPos = 0;

	private boolean needsSynch = true;
	private int currentFilter = 1;

	private static final String[] FILTER_NAMES = new String[] { "all", "online", "offline" };
	@SuppressWarnings("unchecked")
	private static final Set<EnumStationState>[] FILTERS = new EnumSet[] {
		EnumSet.noneOf(EnumStationState.class),
		EnumSet.of(EnumStationState.OK),
		EnumSet.of(EnumStationState.INSUFFICIENT_OFFER, EnumStationState.INSUFFICIENT_TRADE_GOOD, EnumStationState.INSUFFICIENT_BUFFER, EnumStationState.INSUFFICIENT_PAPER, EnumStationState.INSUFFICIENT_STAMPS)
	};

	public ContainerCatalogue(EntityPlayer player) {
		super(player.inventory);
		this.player = player;

		// Filter out any trade stations which do not actually offer anything.
		stations = new LinkedHashMap<GameProfile, ITradeStation>();
		rebuildStationsList();
	}

	public int getMaxCount() { return maxItPos; }
	public int getCurrentPos() { return currentItPos; }

	public String getFilterIdent() { return FILTER_NAMES[currentFilter]; }

	private void rebuildStationsList() {
		stations.clear();
		for(ITradeStation station : PostManager.postRegistry.getPostOffice(player.worldObj).getActiveTradeStations(player.worldObj).values()) {
			TradeStationInfo info = station.getTradeInfo();
			if(info.tradegood == null)
				continue;

			if(!FILTERS[currentFilter].isEmpty()) {
				if(!FILTERS[currentFilter].contains(info.state))
					continue;
			}

			stations.put(station.getMoniker(), station);
		}
		maxItPos = stations.size();
		resetIteration();

	}

	private void resetIteration() {
		if(!stations.isEmpty()) {
			iterator = stations.values().iterator();
			updateTradeInfo(iterator.next());
		} else
			updateTradeInfo(null);

		currentItPos = 1;
	}

	public void advanceIteration() {

		if(!Proxies.common.isSimulating(player.worldObj)) {
			sendSelection(true);
			return;
		}

		if(stations.isEmpty())
			return;

		if(iterator.hasNext()) {
			currentItPos++;
			updateTradeInfo(iterator.next());
		} else
			resetIteration();
	}

	public void regressIteration() {

		if(!Proxies.common.isSimulating(player.worldObj)) {
			sendSelection(false);
			return;
		}

		if(stations.isEmpty())
			return;

		iterator = stations.values().iterator();
		ITradeStation previous = null;
		currentItPos = 0;

		while(iterator.hasNext()) {
			ITradeStation current = iterator.next();
			if(!current.getMoniker().equals(currentTrade.moniker)) {
				currentItPos++;
				previous = current;
				continue;
			}

			if(previous == null) {
				Iterator<ITradeStation> it = stations.values().iterator();
				currentItPos = stations.size();
				while(it.hasNext())
					previous = it.next();

			}
			updateTradeInfo(previous);
			break;
		}
	}

	public void cycleFilter() {
		if(!Proxies.common.isSimulating(player.worldObj)) {
			PacketPayload payload = new PacketPayload(1, 0, 0);
			payload.intPayload[0] = 2;
			PacketUpdate packet = new PacketUpdate(PacketIds.GUI_SELECTION_CHANGE, payload);
			Proxies.net.sendToServer(packet);
			return;
		}

		if(currentFilter < FILTERS.length -1)
			currentFilter++;
		else
			currentFilter = 0;

		rebuildStationsList();
	}

	private void sendSelection(boolean advance) {
		PacketPayload payload = new PacketPayload(1, 0, 0);
		payload.intPayload[0] = advance ? 0 : 1;
		PacketUpdate packet = new PacketUpdate(PacketIds.GUI_SELECTION_CHANGE, payload);
		Proxies.net.sendToServer(packet);
	}

	/* Managing Trade info */
	public void updateTradeInfo(ITradeStation station) {
		// Updating is done by the server.
		if (!Proxies.common.isSimulating(player.worldObj))
			return;

		if(station != null)
			setTradeInfo(station.getTradeInfo());
		else
			setTradeInfo(null);
		needsSynch = true;
	}

	public void handleTradeInfoUpdate(PacketTradeInfo packet) {
		setTradeInfo(packet.tradeInfo);
	}

	public TradeStationInfo getTradeInfo() {
		return currentTrade;
	}

	private void setTradeInfo(TradeStationInfo info) {
		currentTrade = info;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if(needsSynch) {
			for (int i = 0; i < crafters.size(); i++) {
				ICrafting crafter = (ICrafting)crafters.get(i);
				crafter.sendProgressBarUpdate(this, 0, currentItPos);
				crafter.sendProgressBarUpdate(this, 1, maxItPos);
				crafter.sendProgressBarUpdate(this, 2, currentFilter);
			}

			Proxies.net.sendToPlayer(new PacketTradeInfo(PacketIds.TRADING_INFO, currentTrade), player);
			needsSynch = false;
		}
	}

	@Override
	public void updateProgressBar(int i, int j) {
		switch(i) {
		case 0:
			currentItPos = j;
			break;
		case 1:
			maxItPos = j;
			break;
		case 2:
			currentFilter = j;
			break;
		}
	}

	@Override
	public void handleSelectionChange(EntityPlayer player, PacketUpdate packet) {

		if (packet.payload.intPayload[0] == 0) {
			advanceIteration();
		} else if(packet.payload.intPayload[0] == 1)
			regressIteration();
		else if(packet.payload.intPayload[0] == 2)
			cycleFilter();

		needsSynch = true;
	}

	@Override
	public void setSelection(PacketUpdate packet) {
	}

}
