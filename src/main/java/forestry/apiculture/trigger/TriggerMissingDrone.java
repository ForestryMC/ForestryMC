/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.apiculture.trigger;

import forestry.core.triggers.Trigger;

//import buildcraft.api.statements.IStatementContainer;
//import buildcraft.api.statements.IStatementParameter;

public class TriggerMissingDrone extends Trigger {

    public TriggerMissingDrone() {
        super("missingDrone", "missing_drone");
    }

    /**
     * Return true if the tile given in parameter activates the trigger, given the parameters.
     */
    //	@Override
    //	public boolean isTriggerActive(TileEntity tile, Direction side, IStatementContainer source, IStatementParameter[] parameters) {
    //
    //		if (!(tile instanceof IErrorLogicSource)) {
    //			return false;
    //		}
    //
    //		IErrorLogicSource apiary = (IErrorLogicSource) tile;
    //		return apiary.getErrorLogic().contains(EnumErrorCode.NO_DRONE);
    //	}

}
