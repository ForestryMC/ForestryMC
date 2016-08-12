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
package forestry.core.multiblock;

import forestry.api.multiblock.IMultiblockLogicAlveary;
import forestry.api.multiblock.IMultiblockLogicCharcoalPile;
import forestry.api.multiblock.IMultiblockLogicFactory;
import forestry.api.multiblock.IMultiblockLogicFarm;
import forestry.api.multiblock.IMultiblockLogicGreenhouse;
import forestry.apiculture.multiblock.MultiblockLogicAlveary;
import forestry.arboriculture.multiblock.MultiblockLogicCharcoalPile;
import forestry.farming.multiblock.MultiblockLogicFarm;
import forestry.greenhouse.multiblock.MultiblockLogicGreenhouse;

public class MultiblockLogicFactory implements IMultiblockLogicFactory {

	@Override
	public IMultiblockLogicAlveary createAlvearyLogic() {
		return new MultiblockLogicAlveary();
	}

	@Override
	public IMultiblockLogicFarm createFarmLogic() {
		return new MultiblockLogicFarm();
	}

	@Override
	public IMultiblockLogicGreenhouse createGreenhouseLogic() {
		return new MultiblockLogicGreenhouse();
	}
	
	@Override
	public IMultiblockLogicCharcoalPile createCharcoalPileLogic() {
		return new MultiblockLogicCharcoalPile();
	}
}
