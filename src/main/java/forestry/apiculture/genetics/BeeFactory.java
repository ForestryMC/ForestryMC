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
package forestry.apiculture.genetics;

import forestry.api.apiculture.genetics.IAlleleBeeSpeciesBuilder;
import forestry.api.apiculture.genetics.IBeeFactory;
import forestry.apiculture.genetics.alleles.AlleleBeeSpecies;
import genetics.api.classification.IClassification;

public class BeeFactory implements IBeeFactory {
    @Override
    public IAlleleBeeSpeciesBuilder createSpecies(String modId, String uid, String speciesIdentifier) {
        return new AlleleBeeSpecies.Builder(modId, uid, speciesIdentifier);
    }

    @Override
    public IClassification createBranch(String uid, String scientific) {
        return new BranchBees(uid, scientific);
    }
}
