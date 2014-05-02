/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.genetics;
 
/*
 * Interface to be implemented by the enums representing the various chromosomes
 */
public interface IChromosomeType {
       
        /*
         * Get class which all alleles on this chromosome must interface
         */
        Class<? extends IAllele> getAlleleClass();
       
        String getName();
       
        ISpeciesRoot getSpeciesRoot();
       
        int ordinal();
 
}
