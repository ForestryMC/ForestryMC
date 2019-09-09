package genetics.api.alleles;

import net.minecraft.util.ResourceLocation;

public class AlleleCategorized extends Allele {

	public AlleleCategorized(String modId, String category, String valueName, boolean dominant) {
		super(getUnlocalizedName(modId, category, valueName), dominant);
		setRegistryName(createRegistryName(modId, category, valueName));
	}

	private static ResourceLocation createRegistryName(String modId, String category, String valueName) {
		return new ResourceLocation(modId, category + "_" + valueName);
	}

	//TODO: Find a way to lazy load the unlocalized name so we can use the custom name again
	private static String getUnlocalizedName(String modId, String category, String valueName) {
		return modId + '.' + "allele." + valueName;
		/*String customName = modId + '.' + "allele." + category + '.' + valueName;
		if (I18n.hasKey(customName)) {
			return customName;
		} else {
			return modId + '.' + "allele." + valueName;
		}*/
	}
}
