package forestry.arboriculture.blocks;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;

import forestry.api.arboriculture.EnumForestryWoodType;

public class PropertyForestryWoodType extends PropertyWoodType<EnumForestryWoodType> {
	public static PropertyForestryWoodType[] create(String name, int variantsPerBlock) {
		final int variantCount = (int) Math.ceil((float) EnumForestryWoodType.VALUES.length / variantsPerBlock);
		PropertyForestryWoodType[] variants = new PropertyForestryWoodType[variantCount];
		for (int variantNumber = 0; variantNumber < variantCount; variantNumber++) {
			WoodTypePredicate filter = new WoodTypePredicate(variantNumber, variantsPerBlock);
			Collection<EnumForestryWoodType> allowedValues = Collections2.filter(Lists.newArrayList(EnumForestryWoodType.class.getEnumConstants()), filter);
			variants[variantNumber] = new PropertyForestryWoodType(name, EnumForestryWoodType.class, allowedValues);
		}
		return variants;
	}

	protected PropertyForestryWoodType(String name, Class<EnumForestryWoodType> valueClass, Collection<EnumForestryWoodType> allowedValues) {
		super(name, valueClass, allowedValues);
	}
}
