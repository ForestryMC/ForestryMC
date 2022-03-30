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

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;

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
import forestry.mail.features.MailContainers;
import forestry.mail.network.packets.PacketLetterInfoResponse;

public class ContainerCatalogue extends AbstractContainerMenu implements IGuiSelectable, ILetterInfoReceiver {

	private final Player player;
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

	public static ContainerCatalogue fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data) {
		return new ContainerCatalogue(windowId, inv);
	}

	public ContainerCatalogue(int windowId, Inventory inv) {
		super(MailContainers.CATALOGUE.containerType(), windowId);
		this.player = inv.player;

		if (!player.level.isClientSide) {
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
		if (!player.level.isClientSide) {
			return;
		}

		stations.clear();

		IPostOffice postOffice = PostManager.postRegistry.getPostOffice((ServerLevel) player.level);
		Map<IMailAddress, ITradeStation> tradeStations = postOffice.getActiveTradeStations(player.level);

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
		if (player.level.isClientSide) {
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
	public void broadcastChanges() {
		super.broadcastChanges();

		if (needsSync) {
			for (ContainerListener crafter : containerListeners) {
				crafter.dataChanged(this, 0, stationIndex);
				crafter.dataChanged(this, 1, stations.size());
				crafter.dataChanged(this, 2, currentFilter);
			}

			NetworkUtil.sendToPlayer(new PacketLetterInfoResponse(EnumAddressee.TRADER, currentTrade, null), player);
			needsSync = false;
		}
	}

	@Override
	public void setData(int i, int j) {
		switch (i) {
			case 0 -> stationIndex = j;
			case 1 -> stationCount = j;
			case 2 -> currentFilter = j;
		}
	}

	@Override
	public boolean stillValid(Player p_75145_1_) {
		return true;
	}

	@Override
	public void handleSelectionRequest(ServerPlayer player, int primary, int secondary) {

		switch (primary) {
			case 0 -> nextPage();
			case 1 -> previousPage();
			case 2 -> cycleFilter();
		}

		needsSync = true;
	}
}
