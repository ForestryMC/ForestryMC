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
package forestry.apiculture.genetics.alleles;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import genetics.api.individual.IGenome;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class AlleleEffectExploration extends AlleleEffectThrottled {

    public AlleleEffectExploration() {
        super("exploration", false, 80, true, false);
    }

    @Override
    public IEffectData doEffectThrottled(IGenome genome, IEffectData storedData, IBeeHousing housing) {
        List<PlayerEntity> players = getEntitiesInRange(genome, housing, PlayerEntity.class);
        for (PlayerEntity player : players) {
            player.giveExperiencePoints(2);
        }

        return storedData;
    }

}
