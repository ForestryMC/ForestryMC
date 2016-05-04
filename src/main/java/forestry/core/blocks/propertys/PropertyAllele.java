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
package forestry.core.blocks.propertys;

import com.google.common.base.Optional;

import net.minecraft.block.properties.IProperty;

import java.util.List;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleProperty;

public abstract class PropertyAllele<A extends IAlleleProperty<A>> implements IProperty<A> {
	protected final String name;

	public PropertyAllele(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Optional<A> parseValue(String value) {
		IAllele allele = AlleleManager.alleleRegistry.getAllele(value);
		Class<A> valueClass = getValueClass();
		if (valueClass.isInstance(allele)) {
			A alleleValue = valueClass.cast(allele);
			return Optional.of(alleleValue);
		}
		return Optional.absent();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			PropertyAllele propertyAllele = (PropertyAllele) object;
			return name.equals(propertyAllele.name);
		} else {
			return false;
		}
	}
	
	@Override
	public abstract List<A> getAllowedValues();
	
}
