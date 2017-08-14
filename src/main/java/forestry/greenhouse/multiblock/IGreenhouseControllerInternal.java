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
import java.util.Set;

import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.network.IStreamableGui;
import forestry.core.owner.IOwnedTile;
import forestry.energy.EnergyManager;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.greenhouse.IGreenhouseLimits;
import forestry.greenhouse.api.greenhouse.IGreenhouseProvider;

public interface IGreenhouseControllerInternal extends IGreenhouseController, IMultiblockControllerInternal, IOwnedTile, IStreamableGui {

	/**
	 * @return The energy manager of the controller.
	 */
	EnergyManager getEnergyManager();

	/**
	 * @return True if the greenhouse can work.
	 */
	boolean canWork();

	/**
	 * @return A Set with listener componets.
	 */
	Set<IGreenhouseComponent.Listener> getListenerComponents();

	//TODO: Move back to api
	/***************************************/
	/* IClimateHousing */

	/**
	 * @return The climate container of this region.
	 */
	IClimateContainer getClimateContainer();

	/* IGreenhouseHousing */
	/**
	 * @return the manager of this housing.
	 */
	IGreenhouseProvider getProvider();

	/**
	 * @return the maximal and minimal settings of this housing.
	 */
	@Nullable
	IGreenhouseLimits getLimits();
	/**************************************/
}
