/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.lepidopterology;

import forestry.api.climate.IClimateProvider;
import forestry.api.genetics.IHousing;
import forestry.api.lepidopterology.genetics.IButterfly;
import genetics.api.individual.IIndividual;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IButterflyNursery extends IHousing, IClimateProvider {
    @Nullable
    IButterfly getCaterpillar();

    void setCaterpillar(@Nullable IButterfly caterpillar);

    @Nullable
    IIndividual getNanny();

    boolean canNurse(IButterfly caterpillar);

    World getWorldObj();

}
