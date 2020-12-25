/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.genetics.gatgets;

import genetics.api.individual.IIndividual;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Map;

/**
 * An ISpeciesPlugin provides methods that are used in the alyzer and database to display information about an
 * individual.
 */
@OnlyIn(Dist.CLIENT)
public interface IDatabasePlugin<I extends IIndividual> {

    /* ALYZER */
    List<String> getHints();

    IDatabaseTab[] getTabs();

    Map<String, ItemStack> getIndividualStacks();
}
