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
package forestry.mail.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IPostOffice;
import forestry.api.mail.IPostalState;
import forestry.api.mail.ITradeStation;
import forestry.api.mail.PostManager;
import forestry.api.mail.TradeStationInfo;
import forestry.core.gui.IGuiSelectable;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.mail.EnumStationState;
import forestry.mail.network.PacketLetterInfo;

public class ContainerCatalogue extends Container implements IGuiSelectable {

	private final EntityPlayer player;
	private final List<ITradeStation> stations = new ArrayList<ITradeStation>();

	private TradeStationInfo currentTrade = null;

	private int stationIndex = 0;

	// for display on client
	private int stationCount;

	private boolean needsSynch = true;
	private int currentFilter = 1;

	private static final String[] FILTER_NAMES = new String[]{"all", "online", "offline"};
	private static final List<Set<IPostalState>> FILTERS = new ArrayList<Set<IPostalState>>();

	static {
		EnumSet<EnumStationState> all = EnumSet.allOf(EnumStationState.class);
		EnumSet<EnumStationState> online = EnumSet.of(EnumStationState.OK);
		EnumSet<EnumStationState> offline = EnumSet.copyOf(all);
		offline.removeAll(online);

		FILTERS.add(Collections.<IPostalState>unmodifiableSet(all));
		FILTERS.add(Collections.<IPostalState>unmodifiableSet(online));
		FILTERS.add(Collections.<IPostalState>unmodifiableSet(offline));
	}

	public ContainerCatalogue(EntityPlayer player) {
		this.player = player;

		if (Proxies.common.isSimulating(player.worldObj)) {
			rebuildStationsList();
		}
	}

	public int getPageCount() {
		return Math.max(stationCount, 1);
	}

	public int getPageNumber() {
		return stationIndex + 1;
	}

	public String getFilterIdent() {
		return FILTER_NAMES[currentFilter];
	}

	private void rebuildStationsList() {
		stations.clear();

		IPostOffice postOffice = PostManager.postRegistry.getPostOffice(player.worldObj);
		Map<IMailAddress, ITradeStation> tradeStations = postOffice.getActiveTradeStations(player.worldObj);

		for (ITradeStation station : tradeStations.values()) {
			TradeStationInfo info = station.getTradeInfo();

			// Filter out any trade stations which do not actually offer anything.
			if (info.tradegood != null && FILTERS.get(currentFilter).contains(info.state)) {
				stations.add(station);
			}
		}

		stationIndex = 0;
		updateTradeInfo();
	}

	public void nextPage() {
		if (!Proxies.common.isSimulating(player.worldObj)) {
			sendSelection(true);
			return;
		}

		stationIndex = (stationIndex + 1) % stations.size();
		updateTradeInfo();
	}

	public void previousPage() {
		if (!Proxies.common.isSimulating(player.worldObj)) {
			sendSelection(false);
			return;
		}

		stationIndex = (stationIndex - 1 + stations.size()) % stations.size();
		updateTradeInfo();
	}

	public void cycleFilter() {
		if (!Proxies.common.isSimulating(player.worldObj)) {
			PacketPayload payload = new PacketPayload(1, 0, 0);
			payload.intPayload[0] = 2;
			PacketUpdate packet = new PacketUpdate(PacketIds.GUI_SELECTION_CHANGE, payload);
			Proxies.net.sendToServer(packet);
			return;
		}

		currentFilter = (currentFilter + 1) % FILTERS.size();

		rebuildStationsList();
	}

	private void sendSelection(boolean advance) {
		PacketPayload payload = new PacketPayload(1, 0, 0);
		payload.intPayload[0] = advance ? 0 : 1;
		PacketUpdate packet = new PacketUpdate(PacketIds.GUI_SELECTION_CHANGE, payload);
		Proxies.net.sendToServer(packet);
	}

	/* Managing Trade info */
	private void updateTradeInfo() {
		// Updating is done by the server.
		if (!Proxies.common.isSimulating(player.worldObj)) {
			return;
		}

		if (!stations.isEmpty()) {
			ITradeStation station = stations.get(stationIndex);
			setTradeInfo(station.getTradeInfo());
		} else {
			setTradeInfo(null);
		}
		needsSynch = true;
	}

	public void handleTradeInfoUpdate(PacketLetterInfo packet) {
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

		if (needsSynch) {
			for (Object crafter1 : crafters) {
				ICrafting crafter = (ICrafting) crafter1;
				crafter.sendProgressBarUpdate(this, 0, stationIndex);
				crafter.sendProgressBarUpdate(this, 1, stations.size());
				crafter.sendProgressBarUpdate(this, 2, currentFilter);
			}

			Proxies.net.sendToPlayer(new PacketLetterInfo(PacketIds.LETTER_INFO, EnumAddressee.TRADER, currentTrade, null), player);
			needsSynch = false;
		}
	}

	@Override
	public void updateProgressBar(int i, int j) {
		switch (i) {
			case 0:
				stationIndex = j;
				break;
			case 1:
				stationCount = j;
				break;
			case 2:
				currentFilter = j;
				break;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}

	@Override
	public void handleSelectionChange(EntityPlayer player, PacketUpdate packet) {

		if (packet.payload.intPayload[0] == 0) {
			nextPage();
		} else if (packet.payload.intPayload[0] == 1) {
			previousPage();
		} else if (packet.payload.intPayload[0] == 2) {
			cycleFilter();
		}

		needsSynch = true;
	}

	@Override
	public void setSelection(PacketUpdate packet) {
	}

}
