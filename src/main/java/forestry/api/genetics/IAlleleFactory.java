package forestry.api.genetics;

/**
 * Creates new alleles with smart localization.
 *
 *
 * UID is created like this:
 * 	modId + '.' + category + WordUtils.capitalize(valueName);
 * For Example:
 * 	modId:forestry, category:height, valueName:smallest => forestry.heightSmallest
 * This is mainly for legacy compatibility and may change in future major versions.
 *
 *
 * The default localization uses:
 * [modId].allele.[valueName]
 *
 * Languages that need category-specific names can override it by defining:
 * [modId].allele.[category].[valueName]
 *
 * For example:
 * en_US
 * 	forestry.allele.smallest=Smallest
 * ru_RU
 * 	forestry.allele.smallest=????? ?????????
 * 	forestry.allele.height.smallest=????? ??????
 */
public interface IAlleleFactory {
	/**
	 * @param modId mod prefix for uid and localization (i.e. "forestry")
	 * @param category allele category for uid and localization (i.e. "height")
	 * @param valueName allele value name for uid and localization (i.e. "smallest")
	 * @param value allele float value
	 * @param isDominant allele dominance
	 * @param types allele chromosome type for registration (i.e. EnumTreeChromosome.HEIGHT)
	 * @return a new IAlleleFloat, registered with the allele registry.
	 * @since Forestry 4.2
	 */
	IAlleleFloat createFloat(String modId, String category, String valueName, float value, boolean isDominant, IChromosomeType... types);

	/**
	 * @param modId mod prefix for uid and localization (i.e. "forestry")
	 * @param category allele category for uid and localization (i.e. "territory")
	 * @param valueName allele value name for uid and localization (i.e. "large")
	 * @param xDimValue allele area X Size
	 * @param yDimValue allele area Y Size
	 * @param zDimValue    allele area Z Size
	 * @param isDominant allele dominance
	 * @param types allele chromosome type for registration (i.e. EnumBeeChromosome.TERRITORY)
	 * @return a new IAlleleArea, registered with the allele registry.
	 * @since Forestry 4.2
	 */
	IAlleleArea createArea(String modId, String category, String valueName, int xDimValue, int yDimValue, int zDimValue, boolean isDominant, IChromosomeType... types);

	/**
	 * @param modId mod prefix for uid and localization (i.e. "forestry")
	 * @param category allele category for uid and localization (i.e. "fertility")
	 * @param valueName allele value name for uid and localization (i.e. "low")
	 * @param value allele int value
	 * @param isDominant allele dominance
	 * @param types allele chromosome type for registration (i.e. EnumBeeChromosome.FERTILITY)
	 * @return a new IAlleleInteger, registered with the allele registry.
	 * @since Forestry 4.2
	 */
	IAlleleInteger createInteger(String modId, String category, String valueName, int value, boolean isDominant, IChromosomeType... types);

	/**
	 * @param modId mod prefix for uid and localization (i.e. "forestry")
	 * @param category allele category for uid and localization (i.e. "fireproof")
	 * @param value allele boolean value
	 * @param isDominant allele dominance
	 * @param types allele chromosome type for registration (i.e. EnumTreeChromosome.FIREPROOF)
	 * @return a new IAlleleBoolean, registered with the allele registry.
	 * Note that valueName will always be "true" or "false"
	 * @since Forestry 4.2
	 */
	IAlleleBoolean createBoolean(String modId, String category, boolean value, boolean isDominant, IChromosomeType... types);

	/**
	 * @param modId mod prefix for uid (i.e. "forestry")
	 * @param category allele category for uid (i.e. "flowers")
	 * @param valueName allele value name for uid (i.e. "vanilla")
	 * @param value allele IFlowerProvider value
	 * @param isDominant allele dominance
	 * @param types allele chromosome type for registration (i.e. EnumBeeChromosome.FLOWER_PROVIDER)
	 * @return a new IAlleleFlowers, registered with the allele registry.
	 * IAlleleFlowers localization is handled by the IFlowerProvider.getDescription(), unlike the other alleles.
	 * @since Forestry 4.2
	 */
	IAlleleFlowers createFlowers(String modId, String category, String valueName, IFlowerProvider value, boolean isDominant, IChromosomeType... types);

	/**
	 * @param modId mod prefix for uid and localization (i.e. "forestry")
	 * @param category allele category for uid and localization (i.e. "height")
	 * @param valueName allele value name for uid and localization (i.e. "smallest")
	 * @param value allele float value
	 * @param isDominant allele dominance
	 * @return a new IAlleleFloat, registered with the allele registry.
	 * @deprecated since Forestry 4.2. Use the IChromosomeType version.
	 */
	@Deprecated
	IAlleleFloat createFloat(String modId, String category, String valueName, float value, boolean isDominant);

	/**
	 * @param modId mod prefix for uid and localization (i.e. "forestry")
	 * @param category allele category for uid and localization (i.e. "territory")
	 * @param valueName allele value name for uid and localization (i.e. "large")
	 * @param xDimValue allele area X Size
	 * @param yDimValue allele area Y Size
	 * @param zDimValue	allele area Z Size
	 * @param isDominant allele dominance
	 * @return a new IAlleleArea, registered with the allele registry.
	 * @deprecated since Forestry 4.2. Use the IChromosomeType version.
	 */
	@Deprecated
	IAlleleArea createArea(String modId, String category, String valueName, int xDimValue, int yDimValue, int zDimValue, boolean isDominant);

	/**
	 * @param modId mod prefix for uid and localization (i.e. "forestry")
	 * @param category allele category for uid and localization (i.e. "fertility")
	 * @param valueName allele value name for uid and localization (i.e. "low")
	 * @param value allele int value
	 * @param isDominant allele dominance
	 * @return a new IAlleleInteger, registered with the allele registry.
	 * @deprecated since Forestry 4.2. Use the IChromosomeType version.
	 */
	@Deprecated
	IAlleleInteger createInteger(String modId, String category, String valueName, int value, boolean isDominant);

	/**
	 * @param modId mod prefix for uid and localization (i.e. "forestry")
	 * @param category allele category for uid and localization (i.e. "fireproof")
	 * @param value allele boolean value
	 * @param isDominant allele dominance
	 * @return a new IAlleleBoolean, registered with the allele registry.
	 * Note that valueName will always be "true" or "false"
	 * @deprecated since Forestry 4.2. Use the IChromosomeType version.
	 */
	@Deprecated
	IAlleleBoolean createBoolean(String modId, String category, boolean value, boolean isDominant);

	/**
	 * @param modId mod prefix for uid (i.e. "forestry")
	 * @param category allele category for uid (i.e. "flowers")
	 * @param valueName allele value name for uid (i.e. "vanilla")
	 * @param value allele IFlowerProvider value
	 * @param isDominant allele dominance
	 * @return a new IAlleleFlowers, registered with the allele registry.
	 * IAlleleFlowers localization is handled by the IFlowerProvider.getDescription(), unlike the other alleles.
	 * @deprecated since Forestry 4.2. Use the IChromosomeType version.
	 */
	@Deprecated
	IAlleleFlowers createFlowers(String modId, String category, String valueName, IFlowerProvider value, boolean isDominant);
}
