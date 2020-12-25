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
package forestry.apiculture.multiblock;

import forestry.api.apiculture.DefaultBeeModifier;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.apiculture.blocks.BlockAlvearyType;
import genetics.api.individual.IGenome;

public class TileAlvearyStabiliser extends TileAlveary implements IAlvearyComponent.BeeModifier {

    private static final IBeeModifier beeModifier = new AlvearyStabiliserBeeModifier();

    public TileAlvearyStabiliser() {
        super(BlockAlvearyType.STABILISER);
    }

    @Override
    public IBeeModifier getBeeModifier() {
        return beeModifier;
    }

    private static class AlvearyStabiliserBeeModifier extends DefaultBeeModifier {
        @Override
        public float getMutationModifier(IGenome genome, IGenome mate, float currentModifier) {
            return 0.0f;
        }
    }
}
