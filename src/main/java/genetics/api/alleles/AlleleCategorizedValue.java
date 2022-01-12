package genetics.api.alleles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import forestry.core.utils.Translator;


/**
 * Alleles that have a category with several values inherit from this class.
 * For example, temperature tolerances or speeds are categories with several values.
 * <p>
 * This class helps localization by allowing specific names like
 * forestry.allele.speed.fast
 * and can fall back on generic names like
 * forestry.allele.fast
 */
public class AlleleCategorizedValue<V> extends AlleleValue<V> {

	private final String modId;
	private final String category;
	private final String valueName;

	public AlleleCategorizedValue(String modId, String category, String valueName, V value, boolean dominant) {
		super(getUnlocalizedName(modId, category, valueName), dominant, value);
		setRegistryName(createRegistryName(modId, category, valueName));
		this.modId = modId;
		this.category = category;
		this.valueName = valueName;
	}

	private static ResourceLocation createRegistryName(String modId, String category, String valueName) {
		return new ResourceLocation(modId, category + "_" + valueName);
	}

	private static String getUnlocalizedName(String modId, String category, String valueName) {
		return modId + '.' + "allele." + valueName;
	}

	@Override
	public ITextComponent getDisplayName() {
		String customName = modId + '.' + "allele." + category + '.' + valueName;
		return Translator.tryTranslate(customName, modId + '.' + "allele." + valueName);
	}
}
