/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.apiculture;

public interface IBeeModifier {

	/**
	 * @param genome Genome of the bee this modifier is called for.
	 * @param currentModifier Current modifier.
	 * @return Float used to modify the base territory.
	 */
	float getTerritoryModifier(IBeeGenome genome, float currentModifier);

	/**
	 * @param genome Genome of the bee this modifier is called for.
	 * @param mate
	 * @param currentModifier Current modifier.
	 * @return Float used to modify the base mutation chance.
	 */
	float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier);

	/**
	 * @param genome Genome of the bee this modifier is called for.
	 * @param currentModifier Current modifier.
	 * @return Float used to modify the life span of queens.
	 */
	float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier);

	/**
	 * @param genome Genome of the bee this modifier is called for.
	 * @param currentModifier Current modifier.
	 * @return Float modifying the production speed of queens.
	 */
	float getProductionModifier(IBeeGenome genome, float currentModifier);

	/**
	 * @param genome Genome of the bee this modifier is called for.
	 * @return Float modifying the flowering of queens.
	 */
	float getFloweringModifier(IBeeGenome genome, float currentModifier);

	/**
	 * @param genome Genome of the bee this modifier is called for.
	 * @return Float modifying the chance for a swarmer queen to die off.
	 */
	float getGeneticDecay(IBeeGenome genome, float currentModifier);
	
	/**
	 * @return Boolean indicating if housing can ignore rain
	 */
	boolean isSealed();

	/**
	 * @return Boolean indicating if housing can ignore darkness/night
	 */
	boolean isSelfLighted();

	/**
	 * @return Boolean indicating if housing can ignore not seeing the sky
	 */
	boolean isSunlightSimulated();

	/**
	 * @return Boolean indicating whether this housing simulates the nether
	 */
	boolean isHellish();

}
