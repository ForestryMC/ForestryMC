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

import forestry.api.core.IErrorLogicSource;
import forestry.api.farming.IFarmHousing;
import forestry.core.fluids.ITankManager;
import forestry.core.interfaces.IClimatised;
import forestry.core.interfaces.IRestrictedAccessTile;
import forestry.core.interfaces.ISocketable;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.network.IStreamableGui;
import forestry.farming.gui.IFarmLedgerDelegate;

public interface IFarmController extends IFarmHousing, ISocketable, IClimatised, IRestrictedAccessTile, IErrorLogicSource, IStreamableGui {
	IFarmLedgerDelegate getFarmLedgerDelegate();

	IInventoryAdapter getInternalInventory();

	ITankManager getTankManager();
}
