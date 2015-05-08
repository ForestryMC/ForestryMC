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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IChromosomeType;
import forestry.apiculture.flowers.FlowerProvider;
import forestry.apiculture.genetics.AlleleFlowers;
import forestry.core.vect.IVect;
import forestry.plugins.PluginManager;

public class AlleleHelper implements IAlleleHelper {

	private final Map<Class, Map<?, ? extends IAllele>> alleleMaps = new HashMap<Class, Map<?, ? extends IAllele>>();

	public void init() {
		if (PluginManager.Module.APICULTURE.isEnabled()) {
			createAlleles(EnumAllele.Fertility.class);
			createAlleles(EnumAllele.Flowering.class);
		}

		if (PluginManager.Module.APICULTURE.isEnabled() || PluginManager.Module.ARBORICULTURE.isEnabled()) {
			createAlleles(EnumAllele.Territory.class);

			AlleleManager.alleleRegistry.registerDeprecatedAlleleReplacement("forestry.territoryDefault", get(EnumAllele.Territory.AVERAGE));
		}

		if (PluginManager.Module.APICULTURE.isEnabled() || PluginManager.Module.LEPIDOPTEROLOGY.isEnabled()) {
			createAlleles(EnumAllele.Speed.class);
			createAlleles(EnumAllele.Lifespan.class);
			createAlleles(EnumAllele.Tolerance.class);
			createAlleles(EnumAllele.Flowers.class);

			AlleleManager.alleleRegistry.registerDeprecatedAlleleReplacement("forestry.speedNorm", get(EnumAllele.Speed.NORMAL));
		}

		if (PluginManager.Module.ARBORICULTURE.isEnabled()) {
			createAlleles(EnumAllele.Height.class);
			createAlleles(EnumAllele.Saplings.class);
			createAlleles(EnumAllele.Yield.class);
			createAlleles(EnumAllele.Fireproof.class);
			createAlleles(EnumAllele.Maturation.class);
			createAlleles(EnumAllele.Sappiness.class);

			AlleleManager.alleleRegistry.registerDeprecatedAlleleReplacement("forestry.heightMax10", get(EnumAllele.Height.AVERAGE));
			AlleleManager.alleleRegistry.registerDeprecatedAlleleReplacement("forestry.saplingsDefault", get(EnumAllele.Saplings.AVERAGE));
			AlleleManager.alleleRegistry.registerDeprecatedAlleleReplacement("forestry.saplingsDouble", get(EnumAllele.Saplings.HIGH));
			AlleleManager.alleleRegistry.registerDeprecatedAlleleReplacement("forestry.saplingsTriple", get(EnumAllele.Saplings.HIGHER));
			AlleleManager.alleleRegistry.registerDeprecatedAlleleReplacement("forestry.yieldDefault", get(EnumAllele.Yield.AVERAGE));
			AlleleManager.alleleRegistry.registerDeprecatedAlleleReplacement("forestry.maturitySlowest", get(EnumAllele.Maturation.SLOWEST));
			AlleleManager.alleleRegistry.registerDeprecatedAlleleReplacement("forestry.maturitySlower", get(EnumAllele.Maturation.SLOWER));
			AlleleManager.alleleRegistry.registerDeprecatedAlleleReplacement("forestry.maturitySlow", get(EnumAllele.Maturation.SLOW));
			AlleleManager.alleleRegistry.registerDeprecatedAlleleReplacement("forestry.maturityAverage", get(EnumAllele.Maturation.AVERAGE));
			AlleleManager.alleleRegistry.registerDeprecatedAlleleReplacement("forestry.maturityFast", get(EnumAllele.Maturation.FAST));
			AlleleManager.alleleRegistry.registerDeprecatedAlleleReplacement("forestry.maturityFaster", get(EnumAllele.Maturation.FASTER));
			AlleleManager.alleleRegistry.registerDeprecatedAlleleReplacement("forestry.maturityFastest", get(EnumAllele.Maturation.FASTEST));
		}

		if (PluginManager.Module.LEPIDOPTEROLOGY.isEnabled()) {
			createAlleles(EnumAllele.Size.class);
		}

		Map<Integer, IAlleleInteger> integers = new HashMap<Integer, IAlleleInteger>();
		for (int i = 1; i <= 10; i++) {
			IAlleleInteger alleleInteger = new AlleleInteger("i", i + "d", i, true);
			integers.put(i, alleleInteger);
		}
		alleleMaps.put(Integer.class, integers);

		Map<Boolean, IAlleleBoolean> booleans = new HashMap<Boolean, IAlleleBoolean>();
		booleans.put(true, new AlleleBoolean("bool", true, false));
		booleans.put(false, new AlleleBoolean("bool", false, false));
		alleleMaps.put(Boolean.class, booleans);
	}

	private <T extends Enum<T> & IChromosomeType> IAllele get(Object value) {
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
		if (allele == null || !chromosomeType.getAlleleClass().isInstance(allele)) {
			throw new IllegalArgumentException("Allele is the wrong type. Expected: " + chromosomeType + " Got: " + allele);
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

	private <K extends Enum<K> & IAlleleValue<V>, V> void createAlleles(Class<K> enumClass) {
		String category = enumClass.getSimpleName().toLowerCase(Locale.ENGLISH);
		EnumMap<K, IAllele> map = new EnumMap<K, IAllele>(enumClass);
		for (K enumValue : enumClass.getEnumConstants()) {
			IAllele allele = createAllele(category, enumValue);
			map.put(enumValue, allele);
		}
		alleleMaps.put(enumClass, map);
	}

	private static <K extends IAlleleValue<V>, V> IAllele createAllele(String category, K enumValue) {
		V value = enumValue.getValue();
		boolean isDominant = enumValue.isDominant();
		String name = enumValue.toString().toLowerCase(Locale.ENGLISH);

		Class<?> valueClass = value.getClass();
		if (Float.class.isAssignableFrom(valueClass)) {
			return new AlleleFloat(category, name, (Float) value, isDominant);
		} else if (Integer.class.isAssignableFrom(valueClass)) {
			return new AlleleInteger(category, name, (Integer) value, isDominant);
		} else if (IVect.class.isAssignableFrom(valueClass)) {
			int[] area = ((IVect) value).toArray();
			return new AlleleArea(category, name, area, isDominant);
		} else if (Boolean.class.isAssignableFrom(valueClass)) {
			return new AlleleBoolean(category, (Boolean) value, isDominant);
		} else if (EnumTolerance.class.isAssignableFrom(valueClass)) {
			return new AlleleTolerance(category, name, (EnumTolerance) value, isDominant);
		} else if (FlowerProvider.class.isAssignableFrom(valueClass)) {
			return new AlleleFlowers(category, name, (FlowerProvider) value, isDominant);
		}
		throw new RuntimeException("could not create allele for category: " + category + " and value " + valueClass);
	}
}
