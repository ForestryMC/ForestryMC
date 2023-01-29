/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v3 which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to: SirSengir (original work), CovertJaguar, Player, Binnie,
 * MysteriousAges
 ******************************************************************************/
package forestry.farming.logic;

import forestry.api.farming.Farmables;
import forestry.api.farming.IFarmHousing;

// TODO: Make special Logic for Crops, maybe a virtual map of references to iterate faster?
public class FarmLogicIC2Crops extends FarmLogicOrchard {

    public FarmLogicIC2Crops(IFarmHousing housing) {
        super(housing);
        this.setFarmables(Farmables.farmables.get(FarmableReference.IC2Crops.get()));
    }
}
