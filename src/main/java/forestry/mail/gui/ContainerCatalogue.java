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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.EnumTradeStationState;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IPostOffice;
import forestry.api.mail.IPostalState;
import forestry.api.mail.ITradeStation;
import forestry.api.mail.ITradeStationInfo;
import forestry.api.mail.PostManager;
import forestry.core.gui.IGuiSelectable;
import forestry.core.utils.NetworkUtil;
import forestry.mail.network.packets.PacketLetterInfoResponse;

public class ContainerCatalogue extends Container implements IGuiSelectable, ILetterInfoReceiver {

	private final EntityPlayer player;
	private final List<ITradeStation> stations = new ArrayList<>();

	@Nullable
	private ITradeStationInfo currentTrade = null;

	private int stationIndex = 0;

	// for display on client
	private int stationCount;

	private boolean needsSync = true;
	private int currentFilter = 1;

	private static final String[] FILTER_NAMES = new String[]{"all", "online", "offline"};
	private static final List<Set<IPostalState>> FILTERS = new ArrayList<>();

	static {
		EnumSet<EnumTradeStationState> all = EnumSet.allOf(EnumTradeStationState.class);
		EnumSet<EnumTradeStationState> online = EnumSet.of(EnumTradeStationState.OK);
		EnumSet<EnumTradeStationState> offline = EnumSet.copyOf(all);
		offline.removeAll(online);

		FILTERS.add(Collections.unmodifiableSet(all));
		FILTERS.add(Collections.unmodifiableSet(online));
		FILTERS.add(Collections.unmodifiableSet(offline));
	}

	public ContainerCatalogue(EntityPlayer player) {
		this.player = player;

		if (!player.world.isRemote) {
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

		IPostOffice postOffice = PostManager.postRegistry.getPostOffice(player.world);
		Map<IMailAddress, ITradeStation> tradeStations = postOffice.getActiveTradeStations(player.world);

		for (ITradeStation station : tradeStations.values()) {
			ITradeStationInfo info = station.getTradeInfo();

			// Filter out any trade stations which do not actually offer anything.
			if (FILTERS.get(currentFilter).contains(info.getState())) {
				stations.add(station);
			}
		}

		stationIndex = 0;
		updateTradeInfo();
	}

	public void nextPage() {
		if (!stations.isEmpty()) {
			stationIndex = (stationIndex + 1) % stations.size();
			updateTradeInfo();
		}
	}

	public void previousPage() {
		if (!stations.isEmpty()) {
			stationIndex = (stationIndex - 1 + stations.size()) % stations.size();
			updateTradeInfo();
		}
	}

	public void cycleFilter() {
		currentFilter = (currentFilter + 1) % FILTERS.size();
		rebuildStationsList();
	}

	/* Managing Trade info */
	private void updateTradeInfo() {
		// Updating is done by the server.
		if (player.world.isRemote) {
			return;
		}

		if (!stations.isEmpty()) {
			ITradeStation station = stations.get(stationIndex);
			setTradeInfo(station.getTradeInfo());
		} else {
			setTradeInfo(null);
		}
		needsSync = true;
	}

	@Override
	public void handleLetterInfoUpdate(EnumAddressee type, @Nullable IMailAddress address, @Nullable ITradeStationInfo tradeInfo) {
		setTradeInfo(tradeInfo);
	}

	@Nullable
	public ITradeStationInfo getTradeInfo() {
		return currentTrade;
	}

	private void setTradeInfo(@Nullable ITradeStationInfo info) {
		currentTrade = info;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (needsSync) {
			for (IContainerListener crafter : listeners) {
				crafter.sendWindowProperty(this, 0, stationIndex);
				crafter.sendWindowProperty(this, 1, stations.size());
				crafter.sendWindowProperty(this, 2, currentFilter);
			}

			NetworkUtil.sendToPlayer(new PacketLetterInfoResponse(EnumAddressee.TRADER, currentTrade, null), player);
			needsSync = false;
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
	public void handleSelectionRequest(EntityPlayerMP player, int primary, int secondary) {

		switch (primary) {
			case 0:
				nextPage();
				break;
			case 1:
				previousPage();
				break;
			case 2:
				cycleFilter();
				break;
		}

		needsSync = true;
	}
}
