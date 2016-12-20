package forestry.arboriculture.models;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.IWoodType;

public class ModelProviderFactory {
	public static IGermlingModelProvider create(IWoodType woodType, String modelUid, ILeafSpriteProvider leafSpriteProvider) {
		if (woodType instanceof EnumVanillaWoodType) {
			return new ModelProviderGermlingVanilla((EnumVanillaWoodType) woodType, leafSpriteProvider);
		} else if (woodType instanceof EnumForestryWoodType) {
			return new ModelProviderGermling(modelUid, leafSpriteProvider);
		} else {
			throw new IllegalArgumentException("Unknown wood type: " + woodType);
		}
	}
}
