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

import net.minecraft.state.EnumProperty;

import forestry.arboriculture.genetics.TreeDefinition;

public class PropertyTreeType extends EnumProperty<TreeDefinition> {
	public static int getBlockCount(int variantsPerBlock) {
		return (int) Math.ceil((float) TreeDefinition.VALUES.length / variantsPerBlock);
	}

	public static PropertyTreeType create(String name, int blockNumber, int variantsPerBlock) {
		TreeTypePredicate filter = new TreeTypePredicate(blockNumber, variantsPerBlock);
		Collection<TreeDefinition> allowedValues = Collections2.filter(Lists.newArrayList(TreeDefinition.class.getEnumConstants()), filter);
		return new PropertyTreeType(name, TreeDefinition.class, allowedValues);
	}

	protected PropertyTreeType(String name, Class<TreeDefinition> valueClass, Collection<TreeDefinition> allowedValues) {
		super(name, valueClass, allowedValues);
	}

	public TreeDefinition getFirstType() {
		return getAllowedValues().iterator().next();
	}
}
