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
package forestry.arboriculture.blocks.property;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.Collection;

import net.minecraft.block.properties.PropertyEnum;

import forestry.api.arboriculture.EnumWoodType;
import forestry.arboriculture.blocks.WoodTypePredicate;

public class PropertyWoodType extends PropertyEnum<EnumWoodType> {
	public static int getBlockCount(int variantsPerBlock) {
		return (int) Math.ceil((float) EnumWoodType.VALUES.length / variantsPerBlock);
	}

	public static PropertyWoodType create(@Nonnull String name, int blockNumber, int variantsPerBlock) {
		WoodTypePredicate filter = new WoodTypePredicate(blockNumber, variantsPerBlock);
		Collection<EnumWoodType> allowedValues = Collections2.filter(Lists.newArrayList(EnumWoodType.class.getEnumConstants()), filter);
		return new PropertyWoodType(name, EnumWoodType.class, allowedValues);
	}

	protected PropertyWoodType(String name, Class<EnumWoodType> valueClass, Collection<EnumWoodType> allowedValues) {
		super(name, valueClass, allowedValues);
	}

	@Nonnull
	public EnumWoodType getFirstType() {
		return getAllowedValues().iterator().next();
	}

	@Override
	public Collection<EnumWoodType> getAllowedValues() {
		return super.getAllowedValues();
	}
}
