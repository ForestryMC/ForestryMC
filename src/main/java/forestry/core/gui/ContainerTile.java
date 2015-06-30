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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

import forestry.api.core.IErrorLogicSource;
import forestry.api.core.IErrorState;
import forestry.core.delegates.FakeAccessHandler;
import forestry.core.interfaces.IAccessHandler;
import forestry.core.interfaces.IPowerHandler;
import forestry.core.interfaces.IRestrictedAccessTile;
import forestry.core.network.IStreamableGui;
import forestry.core.network.PacketErrorUpdate;
import forestry.core.network.PacketGuiEnergy;
import forestry.core.network.PacketGuiUpdate;
import forestry.core.utils.EnumAccess;
import forestry.core.utils.Utils;
import forestry.energy.EnergyManager;

public class ContainerTile<T extends TileEntity & IStreamableGui> extends ContainerForestry {

	protected final T tile;
	protected final IAccessHandler accessHandler;

	public ContainerTile(T tile) {
		this.tile = tile;

		if (tile instanceof IRestrictedAccessTile) {
			accessHandler = ((IRestrictedAccessTile) tile).getAccessHandler();
		} else {
			accessHandler = FakeAccessHandler.getInstance();
		}
	}

	public ContainerTile(T tileForestry, InventoryPlayer playerInventory, int xInv, int yInv) {
		this(tileForestry);

		addPlayerInventory(playerInventory, xInv, yInv);
	}

	@Override
	protected final boolean canAccess(EntityPlayer player) {
		return player != null && accessHandler.allowsAlteration(player);
	}

	@Override
	public final boolean canInteractWith(EntityPlayer entityplayer) {
		return Utils.isUseableByPlayer(entityplayer, tile) && accessHandler.allowsViewing(entityplayer);
	}

	private ImmutableSet<IErrorState> previousErrorStates;
	private int previousEnergyManagerData = 0;
	private EnumAccess previousAccess;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (tile instanceof IErrorLogicSource) {
			IErrorLogicSource errorLogicSource = (IErrorLogicSource) tile;
			ImmutableSet<IErrorState> errorStates = errorLogicSource.getErrorLogic().getErrorStates();

			if ((previousErrorStates != null) && !errorStates.equals(previousErrorStates)) {
				PacketErrorUpdate packet = new PacketErrorUpdate(tile, errorLogicSource);
				sendPacketToCrafters(packet);
			}

			previousErrorStates = errorStates;
		}

		if (tile instanceof IPowerHandler) {
			EnergyManager energyManager = ((IPowerHandler) tile).getEnergyManager();
			int energyManagerData = energyManager.toGuiInt();
			if (energyManagerData != previousEnergyManagerData) {
				PacketGuiEnergy packet = new PacketGuiEnergy(windowId, energyManagerData);
				sendPacketToCrafters(packet);

				previousEnergyManagerData = energyManagerData;
			}
		}

		if (tile instanceof IRestrictedAccessTile) {
			IRestrictedAccessTile restrictedAccessTile = (IRestrictedAccessTile) tile;
			IAccessHandler accessHandler = restrictedAccessTile.getAccessHandler();
			EnumAccess access = accessHandler.getAccessType();
			if (access != previousAccess) {
				PacketGuiUpdate packet = new PacketGuiUpdate(tile);
				sendPacketToCrafters(packet);

				previousAccess = access;
			}
		}
	}

	public void onGuiEnergy(int energyStored) {
		if (tile instanceof IPowerHandler) {
			EnergyManager energyManager = ((IPowerHandler) tile).getEnergyManager();
			energyManager.fromGuiInt(energyStored);
		}
	}
}
