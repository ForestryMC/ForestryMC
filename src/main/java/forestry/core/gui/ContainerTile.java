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

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
public abstract class ContainerTile<T extends BlockEntity> extends ContainerForestry {
	protected final T tile;
	@Nullable
	private ImmutableSet<IErrorState> previousErrorStates;
	private int previousEnergyManagerData = 0;
	private int previousWorkCounter = 0;
	private int previousTicksPerWorkCycle = 0;

	protected ContainerTile(int windowId, MenuType<?> type, Inventory playerInventory, T tile, int xInv, int yInv) {
		super(windowId, type);
		addPlayerInventory(playerInventory, xInv, yInv);
		this.tile = tile;
	}

	protected ContainerTile(int windowId, MenuType<?> type, T tile) {
		super(windowId, type);
		this.tile = tile;
	}

	@Override
	protected final boolean canAccess(Player player) {
		return true;
	}

	@Override
	public final boolean stillValid(Player PlayerEntity) {
		return TileUtil.isUsableByPlayer(PlayerEntity, tile);
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		if (tile instanceof IErrorLogicSource errorLogicSource) {
			ImmutableSet<IErrorState> errorStates = errorLogicSource.getErrorLogic().getErrorStates();

			if (!errorStates.equals(previousErrorStates)) {
				PacketErrorUpdate packet = new PacketErrorUpdate(tile, errorLogicSource);
				sendPacketToListeners(packet);
			}

			previousErrorStates = errorStates;
		}

		if (tile instanceof IPowerHandler) {
			EnergyManager energyManager = ((IPowerHandler) tile).getEnergyManager();
			int energyManagerData = energyManager.getEnergyStored();
			if (energyManagerData != previousEnergyManagerData) {
				PacketGuiEnergy packet = new PacketGuiEnergy(containerId, energyManagerData);
				sendPacketToListeners(packet);

				previousEnergyManagerData = energyManagerData;
			}
		}

		if (tile instanceof TilePowered tilePowered) {
			boolean guiNeedsUpdate = false;

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

	@OnlyIn(Dist.CLIENT)
	public void onGuiEnergy(int energyStored) {
		if (tile instanceof IPowerHandler) {
			EnergyManager energyManager = ((IPowerHandler) tile).getEnergyManager();
			energyManager.setEnergyStored(energyStored);
		}
	}

	public T getTile() {
		return tile;
	}
}
