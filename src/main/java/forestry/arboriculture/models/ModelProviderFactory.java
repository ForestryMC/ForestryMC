package forestry.arboriculture.models;

import javax.annotation.Nonnull;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.arboriculture.IWoodType;

public class ModelProviderFactory {
	public static IGermlingModelProvider create(@Nonnull IWoodType woodType, String modelUid) {
		if (woodType instanceof EnumVanillaWoodType) {
			return new ModelProviderGermlingVanilla((EnumVanillaWoodType) woodType);
		} else if (woodType instanceof EnumForestryWoodType) {
			return new ModelProviderGermling(modelUid);
		} else {
			throw new IllegalArgumentException("Unknown wood type: " + woodType);
		}
	}
}
