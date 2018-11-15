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
package forestry.core.gui;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IErrorLogicSource;
import forestry.api.core.IErrorState;
import forestry.core.network.packets.PacketErrorUpdate;
import forestry.core.network.packets.PacketGuiEnergy;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.core.tiles.IPowerHandler;
import forestry.core.tiles.TilePowered;
import forestry.core.tiles.TileUtil;
import forestry.energy.EnergyManager;

//TODO: Add needsGuiUpdate() method, so we only send one gui update packet.
public abstract class ContainerTile<T extends TileEntity> extends ContainerForestry {
	protected final T tile;
	@Nullable
	private ImmutableSet<IErrorState> previousErrorStates;
	private int previousEnergyManagerData = 0;
	private int previousWorkCounter = 0;
	private int previousTicksPerWorkCycle = 0;

	protected ContainerTile(T tile) {
		this.tile = tile;
	}

	protected ContainerTile(T tileForestry, InventoryPlayer playerInventory, int xInv, int yInv) {
		this(tileForestry);

		addPlayerInventory(playerInventory, xInv, yInv);
	}

	@Override
	protected final boolean canAccess(EntityPlayer player) {
		return true;
	}

	@Override
	public final boolean canInteractWith(EntityPlayer entityplayer) {
		return TileUtil.isUsableByPlayer(entityplayer, tile);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (tile instanceof IErrorLogicSource) {
			IErrorLogicSource errorLogicSource = (IErrorLogicSource) tile;
			ImmutableSet<IErrorState> errorStates = errorLogicSource.getErrorLogic().getErrorStates();

			if (previousErrorStates == null || !errorStates.equals(previousErrorStates)) {
				PacketErrorUpdate packet = new PacketErrorUpdate(tile, errorLogicSource);
				sendPacketToListeners(packet);
			}

			previousErrorStates = errorStates;
		}

		if (tile instanceof IPowerHandler) {
			EnergyManager energyManager = ((IPowerHandler) tile).getEnergyManager();
			int energyManagerData = energyManager.getEnergyStored();
			if (energyManagerData != previousEnergyManagerData) {
				PacketGuiEnergy packet = new PacketGuiEnergy(windowId, energyManagerData);
				sendPacketToListeners(packet);

				previousEnergyManagerData = energyManagerData;
			}
		}

		if (tile instanceof TilePowered) {
			boolean guiNeedsUpdate = false;

			TilePowered tilePowered = (TilePowered) tile;

			int workCounter = tilePowered.getWorkCounter();
			if (workCounter != previousWorkCounter) {
				guiNeedsUpdate = true;
				previousWorkCounter = workCounter;
			}

			int ticksPerWorkCycle = tilePowered.getTicksPerWorkCycle();
			if (ticksPerWorkCycle != previousTicksPerWorkCycle) {
				guiNeedsUpdate = true;
				previousTicksPerWorkCycle = ticksPerWorkCycle;
			}

			if (guiNeedsUpdate) {
				PacketGuiUpdate packet = new PacketGuiUpdate(tilePowered);
				sendPacketToListeners(packet);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void onGuiEnergy(int energyStored) {
		if (tile instanceof IPowerHandler) {
			EnergyManager energyManager = ((IPowerHandler) tile).getEnergyManager();
			energyManager.setEnergyStored(energyStored);
		}
	}
}
