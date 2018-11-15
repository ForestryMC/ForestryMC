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

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.util.math.Vec3i;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IChromosomeType;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.apiculture.flowers.FlowerProvider;
import forestry.core.config.Constants;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

public class AlleleHelper implements IAlleleHelper {

	private static final String modId = Constants.MOD_ID;
	@Nullable
	private static AlleleHelper instance;

	private final Map<Class, Map<?, ? extends IAllele>> alleleMaps = new HashMap<>();

	public static AlleleHelper getInstance() {
		if (instance == null) {
			instance = new AlleleHelper();
			instance.init();
		}
		return instance;
	}

	private void init() {
		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			createAlleles(EnumAllele.Fertility.class, EnumBeeChromosome.FERTILITY);
			createAlleles(EnumAllele.Flowering.class, EnumBeeChromosome.FLOWERING);
			createAlleles(EnumAllele.Territory.class, EnumBeeChromosome.TERRITORY);
		}

		if (ModuleHelper.anyEnabled(ForestryModuleUids.APICULTURE, ForestryModuleUids.LEPIDOPTEROLOGY)) {
			createAlleles(EnumAllele.Speed.class,
				EnumBeeChromosome.SPEED,
				EnumButterflyChromosome.SPEED
			);
			createAlleles(EnumAllele.Lifespan.class,
				EnumBeeChromosome.LIFESPAN,
				EnumButterflyChromosome.LIFESPAN
			);
			createAlleles(EnumAllele.Tolerance.class,
				EnumBeeChromosome.TEMPERATURE_TOLERANCE,
				EnumBeeChromosome.HUMIDITY_TOLERANCE,
				EnumButterflyChromosome.TEMPERATURE_TOLERANCE,
				EnumButterflyChromosome.HUMIDITY_TOLERANCE
			);
			createAlleles(EnumAllele.Flowers.class,
				EnumBeeChromosome.FLOWER_PROVIDER,
				EnumButterflyChromosome.FLOWER_PROVIDER
			);
		}

		if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			createAlleles(EnumAllele.Height.class, EnumTreeChromosome.HEIGHT);
			createAlleles(EnumAllele.Saplings.class, EnumTreeChromosome.FERTILITY);
			createAlleles(EnumAllele.Yield.class, EnumTreeChromosome.YIELD);
			createAlleles(EnumAllele.Fireproof.class, EnumTreeChromosome.FIREPROOF);
			createAlleles(EnumAllele.Maturation.class, EnumTreeChromosome.MATURATION);
			createAlleles(EnumAllele.Sappiness.class, EnumTreeChromosome.SAPPINESS);
		}

		if (ModuleHelper.isEnabled(ForestryModuleUids.LEPIDOPTEROLOGY)) {
			createAlleles(EnumAllele.Size.class, EnumButterflyChromosome.SIZE);
		}

		Map<Integer, IAlleleInteger> integers = new HashMap<>();
		for (int i = 1; i <= 10; i++) {
			IAlleleInteger alleleInteger = new AlleleInteger(modId, "i", i + "d", i, true);
			AlleleManager.alleleRegistry.registerAllele(alleleInteger,
				EnumTreeChromosome.GIRTH,
				EnumButterflyChromosome.METABOLISM,
				EnumButterflyChromosome.FERTILITY
			);
			integers.put(i, alleleInteger);
		}
		alleleMaps.put(Integer.class, integers);

		Map<Boolean, IAlleleBoolean> booleans = new HashMap<>();
		booleans.put(true, new AlleleBoolean(modId, "bool", true, false));
		booleans.put(false, new AlleleBoolean(modId, "bool", false, false));
		for (IAlleleBoolean alleleBoolean : booleans.values()) {
			AlleleManager.alleleRegistry.registerAllele(alleleBoolean,
				EnumBeeChromosome.NEVER_SLEEPS,
				EnumBeeChromosome.TOLERATES_RAIN,
				EnumBeeChromosome.CAVE_DWELLING,
				EnumButterflyChromosome.NOCTURNAL,
				EnumButterflyChromosome.TOLERANT_FLYER,
				EnumButterflyChromosome.FIRE_RESIST
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
	public <T extends Enum<T> & IChromosomeType> void set(IAllele[] alleles, T chromosomeType, IAllele allele) {
		if (!chromosomeType.getAlleleClass().isInstance(allele)) {
			throw new IllegalArgumentException("Allele is the wrong type. Expected: " + chromosomeType + " Got: " + allele);
		}

		Collection<IChromosomeType> validTypes = AlleleManager.alleleRegistry.getChromosomeTypes(allele);
		if (validTypes.size() > 0 && !validTypes.contains(chromosomeType)) {
			throw new IllegalArgumentException("Allele can't applied to this Chromosome type. Expected: " + validTypes + " Got: " + chromosomeType);
		}

		alleles[chromosomeType.ordinal()] = allele;
	}

	@Override
	public <T extends Enum<T> & IChromosomeType> void set(IAllele[] alleles, T chromosomeType, IAlleleValue value) {
		set(alleles, chromosomeType, get(value));
	}

	@Override
	public <T extends Enum<T> & IChromosomeType> void set(IAllele[] alleles, T chromosomeType, boolean value) {
		set(alleles, chromosomeType, get(value));
	}

	@Override
	public <T extends Enum<T> & IChromosomeType> void set(IAllele[] alleles, T chromosomeType, int value) {
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
		} else if (Vec3i.class.isAssignableFrom(valueClass)) {
			Vec3i area = (Vec3i) value;
			return AlleleManager.alleleFactory.createArea(modId, category, name, area, isDominant, types);
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
