/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import genetics.api.individual.IChromosomeType;

/**
 * Creates new alleles with smart localization.
 * <p>
 * <p>
 * UID is created like this:
 * modId + '.' + category + WordUtils.capitalize(valueName);
 * For Example:
 * modId:forestry, category:height, valueName:smallest => forestry.heightSmallest
 * This is mainly for legacy compatibility and may change in future major versions.
 * <p>
 * <p>
 * The default localization uses:
 * [modId].allele.[valueName]
 * <p>
 * Languages that need category-specific names can override it by defining:
 * [modId].allele.[category].[valueName]
 * <p>
 * For example:
 * en_US
 * forestry.allele.smallest=Smallest
 * ru_RU
 * forestry.allele.smallest=????? ?????????
 * forestry.allele.height.smallest=????? ??????
 */
public interface IAlleleFactory {
	/**
	 * @param modId      mod prefix for uid (i.e. "forestry")
	 * @param category   allele category for uid (i.e. "flowers")
	 * @param valueName  allele value name for uid (i.e. "vanilla")
	 * @param value      allele IFlowerProvider value
	 * @param isDominant allele dominance
	 * @param types      allele chromosome type for registration (i.e. EnumBeeChromosome.FLOWER_PROVIDER)
	 * @return a new IAlleleFlowers, registered with the allele registry.
	 * IAlleleFlowers localization is handled by the IFlowerProvider.getDescription(), unlike the other alleles.
	 * @since Forestry 4.2
	 */
	IAlleleFlowers createFlowers(String modId, String category, String valueName, IFlowerProvider value, boolean isDominant, IChromosomeType... types);
}
