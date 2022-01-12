package genetics.api.alleles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import forestry.core.utils.Translator;

public class AlleleCategorized extends Allele {

	private final String modId;
	private final String category;
	private final String valueName;

	public AlleleCategorized(String modId, String category, String valueName, boolean dominant) {
		super(getUnlocalizedName(modId, category, valueName), dominant);
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
		return Translator.tryTranslate(customName, getLocalisationKey());
	}
}
