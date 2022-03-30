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

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuType;

import forestry.api.core.IErrorLogicSource;
import forestry.api.core.IErrorState;
import forestry.core.network.packets.PacketErrorUpdateEntity;

public class ContainerEntity<T extends Entity & Container> extends ContainerForestry {
	protected final T entity;
	@Nullable
	private ImmutableSet<IErrorState> previousErrorStates;

	protected ContainerEntity(int windowId, MenuType<?> type, T entity) {
		super(windowId, type);
		this.entity = entity;
	}

	protected ContainerEntity(int windowId, MenuType<?> type, T entity, Inventory playerInventory, int xInv, int yInv) {
		this(windowId, type, entity);
		addPlayerInventory(playerInventory, xInv, yInv);
	}

	@Override
	protected final boolean canAccess(Player player) {
		return true;
	}

	@Override
	public final boolean stillValid(Player PlayerEntity) {
		return entity.stillValid(PlayerEntity);
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		if (entity instanceof IErrorLogicSource errorLogicSource) {
			ImmutableSet<IErrorState> errorStates = errorLogicSource.getErrorLogic().getErrorStates();

			if (previousErrorStates == null || !errorStates.equals(previousErrorStates)) {
				PacketErrorUpdateEntity packet = new PacketErrorUpdateEntity(entity, errorLogicSource);
				sendPacketToListeners(packet);
			}

			previousErrorStates = errorStates;
		}
	}
}
