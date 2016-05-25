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

import javax.annotation.Nonnull;
import java.util.Collection;

import net.minecraft.block.properties.PropertyEnum;

import forestry.api.arboriculture.IWoodType;

public abstract class PropertyWoodType<T extends Enum<T> & IWoodType> extends PropertyEnum<T> {
	protected PropertyWoodType(String name, Class<T> valueClass, Collection<T> allowedValues) {
		super(name, valueClass, allowedValues);
	}

	@Nonnull
	public T getFirstType() {
		return getAllowedValues().iterator().next();
	}

	@Override
	public Collection<T> getAllowedValues() {
		return super.getAllowedValues();
	}
}
