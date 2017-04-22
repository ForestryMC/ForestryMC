package forestry.core.genetics.alleles;

import forestry.core.utils.Translator;
import org.apache.commons.lang3.text.WordUtils;

/**
 * Alleles that have a category with several values inherit from this class.
 * For example, temperature tolerances or speeds are categories with several values.
 * <p>
 * This class helps localization by allowing specific names like
 * forestry.allele.speed.fast
 * and can fall back on generic names like
 * forestry.allele.fast
 */
public abstract class AlleleCategorized extends Allele {
	protected AlleleCategorized(String modId, String category, String valueName, boolean isDominant) {
		super(getUid(modId, category, valueName), getUnlocalizedName(modId, category, valueName), isDominant);
	}

	private static String getUid(String modId, String category, String valueName) {
		return modId + '.' + category + WordUtils.capitalize(valueName);
	}

	private static String getUnlocalizedName(String modId, String category, String valueName) {
		String customName = modId + '.' + "allele." + category + '.' + valueName;
		if (Translator.canTranslateToLocal(customName)) {
			return customName;
		} else {
			return modId + '.' + "allele." + valueName;
		}
	}
}
