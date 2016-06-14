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
package forestry.factory.multiblock;

import javax.annotation.Nonnull;

import forestry.api.multiblock.IDistillVatController;
import forestry.core.access.IRestrictedAccess;
import forestry.core.fluids.ITankManager;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.network.IStreamableGui;
import forestry.core.tiles.ILiquidTankTile;
import forestry.energy.EnergyManager;

public interface IDistillVatControllerInternal extends IDistillVatController, IMultiblockControllerInternal, ILiquidTankTile, IRestrictedAccess, IStreamableGui {
	IInventoryAdapter getInternalInventory();

	@Nonnull
	ITankManager getTankManager();
	@Nonnull
	EnergyManager getEnergyManager();

	int getWorkCounter();
	int getProgressScaled(int i);
}
