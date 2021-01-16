/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture.genetics;

import net.minecraftforge.common.PlantType;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IChromosomeAllele;
import genetics.api.individual.IChromosomeList;
import genetics.api.individual.IChromosomeValue;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.genetics.IFruitFamily;

public class TreeChromosomes {
	public static final IChromosomeList TYPES = GeneticsAPI.apiInstance.getChromosomeList("rootTrees");
	/**
	 * Determines the following: - WorldGen, including the used wood blocks - {@link IFruitFamily}s supported. Limits which {@link IFruitProvider}
	 * will actually yield fruit with this species. - Native {@link PlantType} for this tree. Combines with the PLANT chromosome.
	 */
	public static final IChromosomeAllele<IAlleleTreeSpecies> SPECIES = TYPES.builder().name("species").asAllele(IAlleleTreeSpecies.class);
	/**
	 * A float modifying the height of the tree. Taken into account at worldgen.
	 */
	public static final IChromosomeValue<Float> HEIGHT = TYPES.builder().name("height").asValue(Float.class);
	/**
	 * Chance for saplings.
	 */
	public static final IChromosomeValue<Float> FERTILITY = TYPES.builder().name("fertility").asValue(Float.class);
	/**
	 * {@link IFruitProvider}, determines if and what fruits are grown on the tree. Limited by the {@link IFruitFamily}s the species supports.
	 */
	public static final IChromosomeAllele<IAlleleFruit> FRUITS = TYPES.builder().name("fruits").asAllele(IAlleleFruit.class);
	/**
	 * Chance for fruit leaves and/or drops.
	 */
	public static final IChromosomeValue<Float> YIELD = TYPES.builder().name("yield").asValue(Float.class);
	/**
	 * Determines the speed at which fruit will ripen on this tree.
	 */
	public static final IChromosomeValue<Float> SAPPINESS = TYPES.builder().name("sappiness").asValue(Float.class);
	/**
	 * Leaf effect. Unused.
	 */
	public static final IChromosomeAllele<IAlleleLeafEffect> EFFECT = TYPES.builder().name("effect").asAllele(IAlleleLeafEffect.class);
	/**
	 * Amount of random ticks which need to elapse before a sapling will grow into a tree.
	 */
	public static final IChromosomeValue<Integer> MATURATION = TYPES.builder().name("maturation").asValue(Integer.class);

	public static final IChromosomeValue<Integer> GIRTH = TYPES.builder().name("girth").asValue(Integer.class);
	/**
	 * Determines if the tree can burn.
	 */
	public static final IChromosomeValue<Boolean> FIREPROOF = TYPES.builder().name("fireproof").asValue(Boolean.class);

	private TreeChromosomes() {
	}
}
