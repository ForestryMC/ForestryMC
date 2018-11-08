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
package forestry.arboriculture.blocks;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;

import forestry.api.arboriculture.EnumVanillaWoodType;

public class PropertyVanillaWoodType extends PropertyWoodType<EnumVanillaWoodType> {

	public static PropertyVanillaWoodType[] create(String name, int variantsPerBlock) {
		final int variantCount = (int) Math.ceil((float) EnumVanillaWoodType.VALUES.length / variantsPerBlock);
		PropertyVanillaWoodType[] variants = new PropertyVanillaWoodType[variantCount];
		for (int variantNumber = 0; variantNumber < variantCount; variantNumber++) {
			WoodTypePredicate filter = new WoodTypePredicate(variantNumber, variantsPerBlock);
			Collection<EnumVanillaWoodType> allowedValues = Collections2.filter(Lists.newArrayList(EnumVanillaWoodType.class.getEnumConstants()), filter);
			variants[variantNumber] = new PropertyVanillaWoodType(name, EnumVanillaWoodType.class, allowedValues);
		}
		return variants;
	}

	protected PropertyVanillaWoodType(String name, Class<EnumVanillaWoodType> valueClass, Collection<EnumVanillaWoodType> allowedValues) {
		super(name, valueClass, allowedValues);
	}
}
