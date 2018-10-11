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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;

import net.minecraftforge.fluids.IFluidTank;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateTransformer;
import forestry.climatology.inventory.InventoryHabitatFormer;
import forestry.climatology.tiles.TileHabitatFormer;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.gui.ContainerLiquidTanksHelper;
import forestry.core.gui.ContainerTile;
import forestry.core.gui.IContainerLiquidTanks;
import forestry.core.gui.IGuiSelectable;
import forestry.core.gui.slots.SlotLiquidIn;
import forestry.core.network.packets.PacketGuiUpdate;

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

	public ContainerHabitatFormer(InventoryPlayer playerInventory, TileHabitatFormer tile) {
		super(tile, playerInventory, 8, 151);

		this.helper = new ContainerLiquidTanksHelper<>(tile);
		this.addSlotToContainer(new SlotLiquidIn(tile, InventoryHabitatFormer.SLOT_INPUT, 129, 38));
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
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

		tile.getTankManager().sendTankUpdate(this, listeners);
	}

	@Override
	public void handleSelectionRequest(EntityPlayerMP player, int primary, int secondary) {
		IClimateTransformer transformer = tile.getTransformer();
		switch (primary) {
			case REQUEST_ID_CIRCLE:
				transformer.setCircular(secondary == 1);
				break;
			case REQUEST_ID_RANGE:
				transformer.setRange(secondary);
				break;
			default:
				break;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handlePipetteClickClient(int slot, EntityPlayer player) {
		helper.handlePipetteClickClient(slot, player);
	}

	@Override
	public void handlePipetteClick(int slot, EntityPlayerMP player) {
		helper.handlePipetteClick(slot, player);
	}

	@Override
	public void addListener(IContainerListener crafting) {
		super.addListener(crafting);
		tile.getTankManager().containerAdded(this, crafting);
	}

	@Override
	public void onContainerClosed(EntityPlayer entityPlayer) {
		super.onContainerClosed(entityPlayer);
		tile.getTankManager().containerRemoved(this);
	}

	@Override
	public IFluidTank getTank(int slot) {
		return tile.getTankManager().getTank(slot);
	}
}
