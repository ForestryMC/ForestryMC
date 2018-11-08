package forestry.arboriculture.genetics;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodProvider;
import forestry.api.arboriculture.IWoodType;

@Deprecated
public class WoodProviderFactory {
	public static IWoodProvider create(IWoodType woodType) {
		if (woodType instanceof EnumVanillaWoodType) {
			return new WoodProviderVanilla((EnumVanillaWoodType) woodType);
		} else if (woodType instanceof EnumForestryWoodType) {
			return new WoodProvider((EnumForestryWoodType) woodType);
		} else {
			throw new IllegalArgumentException("Unknown wood type: " + woodType);
		}
	}
}
