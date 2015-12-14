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
package forestry.farming.multiblock;

import forestry.api.multiblock.IFarmController;
import forestry.core.access.IRestrictedAccess;
import forestry.core.circuits.ISocketable;
import forestry.core.fluids.ITankManager;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.network.IStreamableGui;
import forestry.core.tiles.IClimatised;
import forestry.farming.gui.IFarmLedgerDelegate;

public interface IFarmControllerInternal extends IFarmController, IMultiblockControllerInternal, ISocketable, IClimatised, IRestrictedAccess, IStreamableGui {
	IFarmLedgerDelegate getFarmLedgerDelegate();

	IInventoryAdapter getInternalInventory();

	ITankManager getTankManager();
}
