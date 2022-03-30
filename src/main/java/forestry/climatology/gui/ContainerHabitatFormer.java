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
package forestry.climatology.gui;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.IFluidTank;

import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateTransformer;
import forestry.climatology.features.ClimatologyContainers;
import forestry.climatology.inventory.InventoryHabitatFormer;
import forestry.climatology.tiles.TileHabitatFormer;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.gui.ContainerLiquidTanksHelper;
import forestry.core.gui.ContainerTile;
import forestry.core.gui.IContainerLiquidTanks;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.core.tiles.TileUtil;

public class ContainerHabitatFormer extends ContainerTile<TileHabitatFormer> implements IContainerLiquidTanks, IGuiSelectable {

	//Selection Request Ids
	static final int REQUEST_ID_CIRCLE = 0;
	static final int REQUEST_ID_RANGE = 1;

	//Gui Update
	private IClimateState previousState = ClimateStateHelper.INSTANCE.absent();
	private IClimateState previousTarget = ClimateStateHelper.INSTANCE.absent();
	private IClimateState previousDefault = ClimateStateHelper.INSTANCE.absent();
	private int previousRange;
	private boolean previousCircular;

	//Container Helper
	private final ContainerLiquidTanksHelper<TileHabitatFormer> helper;

	//TODO dedupe
	public static ContainerHabitatFormer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf extraData) {
		TileHabitatFormer tile = TileUtil.getTile(inv.player.level, extraData.readBlockPos(), TileHabitatFormer.class);
		return new ContainerHabitatFormer(windowId, inv, tile);
	}

	public ContainerHabitatFormer(int windowId, Inventory playerInventory, TileHabitatFormer tile) {
		super(windowId, ClimatologyContainers.HABITAT_FORMER.containerType(), playerInventory, tile, 8, 151);

		this.helper = new ContainerLiquidTanksHelper<>(tile);
		this.addSlot(new SlotLiquidIn(tile, InventoryHabitatFormer.SLOT_INPUT, 129, 38));
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		boolean guiNeedsUpdate = false;
		IClimateTransformer transformer = tile.getTransformer();

		IClimateState state = transformer.getCurrent();
		if (!previousState.equals(state)) {
			previousState = state;
			guiNeedsUpdate = true;
		}

		IClimateState target = transformer.getTarget();
		if (!previousTarget.equals(target)) {
			previousTarget = target;
			guiNeedsUpdate = true;
		}

		IClimateState defaultState = transformer.getDefault();
		if (!previousDefault.equals(defaultState)) {
			previousDefault = defaultState;
			guiNeedsUpdate = true;
		}

		int range = transformer.getRange();
		if (range != previousRange) {
			previousRange = range;
			guiNeedsUpdate = true;
		}

		boolean circular = transformer.isCircular();
		if (circular != previousCircular) {
			previousCircular = circular;
			guiNeedsUpdate = true;
		}

		if (guiNeedsUpdate) {
			PacketGuiUpdate packet = new PacketGuiUpdate(tile);
			sendPacketToListeners(packet);
		}

		tile.getTankManager().sendTankUpdate(this, containerListeners);
	}

	@Override
	public void handleSelectionRequest(ServerPlayer player, int primary, int secondary) {
		IClimateTransformer transformer = tile.getTransformer();
		switch (primary) {
			case REQUEST_ID_CIRCLE -> transformer.setCircular(secondary == 1);
			case REQUEST_ID_RANGE -> transformer.setRange(secondary);
			default -> {
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handlePipetteClickClient(int slot, Player player) {
		helper.handlePipetteClickClient(slot, player);
	}

	@Override
	public void handlePipetteClick(int slot, ServerPlayer player) {
		helper.handlePipetteClick(slot, player);
	}

	@Override
	public void addSlotListener(ContainerListener crafting) {
		super.addSlotListener(crafting);
		tile.getTankManager().containerAdded(this, crafting);
	}

	@Override
	public void removed(Player PlayerEntity) {
		super.removed(PlayerEntity);
		tile.getTankManager().containerRemoved(this);
	}

	@Override
	public IFluidTank getTank(int slot) {
		return tile.getTankManager().getTank(slot);
	}
}
