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

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import forestry.api.core.IErrorLogicSource;
import forestry.api.core.IErrorState;
import forestry.core.access.EnumAccess;
import forestry.core.access.FakeAccessHandler;
import forestry.core.access.IAccessHandler;
import forestry.core.access.IRestrictedAccess;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.packets.PacketAccessUpdateEntity;
import forestry.core.network.packets.PacketErrorUpdateEntity;

public class ContainerEntity<T extends Entity & IInventory> extends ContainerForestry {
	protected final T entity;
	private final IAccessHandler accessHandler;

	protected ContainerEntity(T entity) {
		this.entity = entity;

		if (entity instanceof IRestrictedAccess) {
			accessHandler = ((IRestrictedAccess) entity).getAccessHandler();
		} else {
			accessHandler = FakeAccessHandler.getInstance();
		}
	}

	protected ContainerEntity(T entity, InventoryPlayer playerInventory, int xInv, int yInv) {
		this(entity);
		addPlayerInventory(playerInventory, xInv, yInv);
	}

	@Override
	protected final boolean canAccess(EntityPlayer player) {
		return player != null && accessHandler.allowsAlteration(player);
	}

	@Override
	public final boolean canInteractWith(EntityPlayer entityplayer) {
		return entity.isUseableByPlayer(entityplayer) && accessHandler.allowsViewing(entityplayer);
	}

	private ImmutableSet<IErrorState> previousErrorStates;
	private EnumAccess previousAccess;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (entity instanceof IErrorLogicSource) {
			IErrorLogicSource errorLogicSource = (IErrorLogicSource) entity;
			ImmutableSet<IErrorState> errorStates = errorLogicSource.getErrorLogic().getErrorStates();

			if ((previousErrorStates == null) || !errorStates.equals(previousErrorStates)) {
				PacketErrorUpdateEntity packet = new PacketErrorUpdateEntity(entity, errorLogicSource);
				sendPacketToCrafters(packet);
			}

			previousErrorStates = errorStates;
		}

		if (entity instanceof IRestrictedAccess) {
			IRestrictedAccess restrictedAccess = (IRestrictedAccess) entity;
			IAccessHandler accessHandler = restrictedAccess.getAccessHandler();
			EnumAccess access = accessHandler.getAccess();
			if (access != previousAccess) {
				IForestryPacketClient packet = new PacketAccessUpdateEntity(restrictedAccess, entity);
				sendPacketToCrafters(packet);

				previousAccess = access;
			}
		}
	}
}
