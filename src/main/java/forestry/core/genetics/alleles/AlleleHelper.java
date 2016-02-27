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
package forestry.core.genetics.alleles;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import forestry.api.apiculture.BeeChromosome;
import forestry.api.arboriculture.TreeChromosome;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IChromosomeType;
import forestry.api.lepidopterology.ButterflyChromosome;
import forestry.apiculture.flowers.FlowerProvider;
import forestry.core.config.Constants;
import forestry.core.utils.vect.IVect;
import forestry.plugins.ForestryPluginUids;

public class AlleleHelper implements IAlleleHelper {

	private static final String modId = Constants.ID;
	public static AlleleHelper instance;

	private final Map<Class, Map<?, ? extends IAllele>> alleleMaps = new HashMap<>();

	public void init() {
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE)) {
			createAlleles(EnumAllele.Fertility.class, BeeChromosome.FERTILITY);
			createAlleles(EnumAllele.Flowering.class, BeeChromosome.FLOWERING);
		}

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE) || ForestryAPI.enabledPlugins.contains(ForestryPluginUids.ARBORICULTURE)) {
			createAlleles(EnumAllele.Territory.class,
					BeeChromosome.TERRITORY,
					TreeChromosome.TERRITORY
			);
		}

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE) || ForestryAPI.enabledPlugins.contains(ForestryPluginUids.LEPIDOPTEROLOGY)) {
			createAlleles(EnumAllele.Speed.class,
					BeeChromosome.SPEED,
					ButterflyChromosome.SPEED
			);
			createAlleles(EnumAllele.Lifespan.class,
					BeeChromosome.LIFESPAN,
					ButterflyChromosome.LIFESPAN
			);
			createAlleles(EnumAllele.Tolerance.class,
					BeeChromosome.TEMPERATURE_TOLERANCE,
					BeeChromosome.HUMIDITY_TOLERANCE,
					ButterflyChromosome.TEMPERATURE_TOLERANCE,
					ButterflyChromosome.HUMIDITY_TOLERANCE
			);
			createAlleles(EnumAllele.Flowers.class,
					BeeChromosome.FLOWER_PROVIDER,
					ButterflyChromosome.FLOWER_PROVIDER
			);
		}

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.ARBORICULTURE)) {
			createAlleles(EnumAllele.Height.class, TreeChromosome.HEIGHT);
			createAlleles(EnumAllele.Saplings.class, TreeChromosome.FERTILITY);
			createAlleles(EnumAllele.Yield.class, TreeChromosome.YIELD);
			createAlleles(EnumAllele.Fireproof.class, TreeChromosome.FIREPROOF);
			createAlleles(EnumAllele.Maturation.class, TreeChromosome.MATURATION);
			createAlleles(EnumAllele.Sappiness.class, TreeChromosome.SAPPINESS);
		}

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.LEPIDOPTEROLOGY)) {
			createAlleles(EnumAllele.Size.class, ButterflyChromosome.SIZE);
		}

		Map<Integer, IAlleleInteger> integers = new HashMap<>();
		for (int i = 1; i <= 10; i++) {
			IAlleleInteger alleleInteger = new AlleleInteger(modId, "i", i + "d", i, true);
			AlleleManager.alleleRegistry.registerAllele(alleleInteger,
					TreeChromosome.GIRTH,
					ButterflyChromosome.METABOLISM,
					ButterflyChromosome.FERTILITY
			);
			integers.put(i, alleleInteger);
		}
		alleleMaps.put(Integer.class, integers);

		Map<Boolean, IAlleleBoolean> booleans = new HashMap<>();
		booleans.put(true, new AlleleBoolean(modId, "bool", true, false));
		booleans.put(false, new AlleleBoolean(modId, "bool", false, false));
		for (IAlleleBoolean alleleBoolean : booleans.values()) {
			AlleleManager.alleleRegistry.registerAllele(alleleBoolean,
					BeeChromosome.NEVER_SLEEPS,
					BeeChromosome.TOLERANT_FLYER,
					BeeChromosome.CAVE_DWELLING,
					ButterflyChromosome.NOCTURNAL,
					ButterflyChromosome.TOLERANT_FLYER,
					ButterflyChromosome.FIRE_RESIST
			);
		}
		alleleMaps.put(Boolean.class, booleans);
	}

	private IAllele get(Object value) {
		Class<?> valueClass = value.getClass();
		Map<?, ? extends IAllele> map = alleleMaps.get(valueClass);
		if (map == null) {
			throw new IllegalArgumentException("There is no IAllele type for: " + valueClass + ' ' + value);
		}
		IAllele allele = map.get(value);
		if (allele == null) {
			throw new IllegalArgumentException("There is no IAllele for: " + valueClass + ' ' + value);
		}

		return allele;
	}

	@Override
	public <C extends Enum<C> & IChromosomeType<C>> void set(Map<C, IAllele> alleles, C chromosomeType, IAllele allele) {
		if (allele == null) {
			throw new NullPointerException("Allele must not be null");
		}
		if (!chromosomeType.getAlleleClass().isInstance(allele)) {
			throw new IllegalArgumentException("Allele is the wrong type. Expected: " + chromosomeType + " Got: " + allele);
		}

		Collection<IChromosomeType<?>> validTypes = AlleleManager.alleleRegistry.getChromosomeTypes(allele);
		if (validTypes.size() > 0 && !validTypes.contains(chromosomeType)) {
			throw new IllegalArgumentException("Allele can't applied to this Chromosome type. Expected: " + validTypes + " Got: " + chromosomeType);
		}

		alleles.put(chromosomeType, allele);
	}

	@Override
	public <C extends Enum<C> & IChromosomeType<C>> void set(Map<C, IAllele> alleles, C chromosomeType, IAlleleValue value) {
		set(alleles, chromosomeType, get(value));
	}

	@Override
	public <C extends Enum<C> & IChromosomeType<C>> void set(Map<C, IAllele> alleles, C chromosomeType, boolean value) {
		set(alleles, chromosomeType, get(value));
	}

	@Override
	public <C extends Enum<C> & IChromosomeType<C>> void set(Map<C, IAllele> alleles, C chromosomeType, int value) {
		set(alleles, chromosomeType, get(value));
	}

	private <K extends Enum<K> & IAlleleValue<V>, V> void createAlleles(Class<K> enumClass, IChromosomeType... types) {
		String category = enumClass.getSimpleName().toLowerCase(Locale.ENGLISH);
		EnumMap<K, IAllele> map = new EnumMap<>(enumClass);
		for (K enumValue : enumClass.getEnumConstants()) {
			IAllele allele = createAllele(category, enumValue, types);
			map.put(enumValue, allele);
		}
		alleleMaps.put(enumClass, map);
	}

	private static <K extends IAlleleValue<V>, V> IAllele createAllele(String category, K enumValue, IChromosomeType... types) {
		V value = enumValue.getValue();
		boolean isDominant = enumValue.isDominant();
		String name = enumValue.toString().toLowerCase(Locale.ENGLISH);

		Class<?> valueClass = value.getClass();
		if (Float.class.isAssignableFrom(valueClass)) {
			return AlleleManager.alleleFactory.createFloat(modId, category, name, (Float) value, isDominant, types);
		} else if (Integer.class.isAssignableFrom(valueClass)) {
			return AlleleManager.alleleFactory.createInteger(modId, category, name, (Integer) value, isDominant, types);
		} else if (IVect.class.isAssignableFrom(valueClass)) {
			IVect area = (IVect) value;
			return AlleleManager.alleleFactory.createArea(modId, category, name, area.getX(), area.getY(), area.getZ(), isDominant, types);
		} else if (Boolean.class.isAssignableFrom(valueClass)) {
			return AlleleManager.alleleFactory.createBoolean(modId, category, (Boolean) value, isDominant, types);
		} else if (EnumTolerance.class.isAssignableFrom(valueClass)) {
			IAlleleTolerance alleleTolerance = new AlleleTolerance(modId, category, name, (EnumTolerance) value, isDominant);
			AlleleManager.alleleRegistry.registerAllele(alleleTolerance, types);
			return alleleTolerance;
		} else if (FlowerProvider.class.isAssignableFrom(valueClass)) {
			return AlleleManager.alleleFactory.createFlowers(modId, category, name, (FlowerProvider) value, isDominant, types);
		}
		throw new RuntimeException("could not create allele for category: " + category + " and value " + valueClass);
	}
}
