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
package forestry.apiculture.genetics.alleles;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.core.utils.DamageSourceForestry;
import genetics.api.individual.IGenome;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;

import java.util.List;

public class AlleleEffectMisanthrope extends AlleleEffectThrottled {

    private static final DamageSource damageSourceBeeEnd = new DamageSourceForestry("bee.end");

    public AlleleEffectMisanthrope() {
        super("misanthrope", true, 20, false, false);
    }

    @Override
    public IEffectData doEffectThrottled(IGenome genome, IEffectData storedData, IBeeHousing housing) {
        List<PlayerEntity> players = getEntitiesInRange(genome, housing, PlayerEntity.class);
        for (PlayerEntity player : players) {
            int damage = 4;

            // Entities are not attacked if they wear a full set of apiarist's armor.
            int count = BeeManager.armorApiaristHelper.wearsItems(player, getRegistryName(), true);
            damage -= count;
            if (damage <= 0) {
                continue;
            }

            player.attackEntityFrom(damageSourceBeeEnd, damage);
        }

        return storedData;
    }

}
