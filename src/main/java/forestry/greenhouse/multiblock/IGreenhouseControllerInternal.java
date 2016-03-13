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
package forestry.greenhouse.multiblock;

import javax.annotation.Nullable;

import forestry.api.core.ICamouflageHandler;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.access.IRestrictedAccess;
import forestry.core.fluids.ITankManager;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.network.IStreamableGui;
import forestry.energy.EnergyManager;

public interface IGreenhouseControllerInternal extends IGreenhouseController, IMultiblockControllerInternal, IRestrictedAccess, IStreamableGui, ICamouflageHandler {

	ITankManager getTankManager();
	
	@Nullable
	EnergyManager getEnergyManager();
	
}
