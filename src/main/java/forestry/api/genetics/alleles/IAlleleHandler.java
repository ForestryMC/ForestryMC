/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.genetics.alleles;

import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IGeneticRegistry;
import genetics.api.alleles.IAllele;
import genetics.api.classification.IClassification;

/**
 * @author Alex Binnie
 * <p>
 * Handler for events that occur in IAlleleRegistry, such as registering alleles, branches etc. Useful for handling plugin specific behavior (i.e.
 * creating a list of all bee species etc.)
 */
public interface IAlleleHandler {

    /**
     * Called when an allele is registered with {@link IGeneticRegistry}.
     *
     * @param allele Allele which was registered.
     */
    void onRegisterAllele(IAllele allele);

    /**
     * Called when a classification is registered with {@link IGeneticRegistry}.
     *
     * @param classification Classification which was registered.
     */
    void onRegisterClassification(IClassification classification);

    /**
     * Called when a fruit family is registered with {@link IGeneticRegistry}.
     *
     * @param family Fruit family which was registered.
     */
    void onRegisterFruitFamily(IFruitFamily family);
}
