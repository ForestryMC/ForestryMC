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
package forestry.core.blocks.properties;

import com.google.common.base.MoreObjects;

import java.util.List;
import java.util.Optional;

import net.minecraft.state.IProperty;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;

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
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("name", this.name)
			.add("clazz", getValueClass())
			.add("values", this.getAllowedValues())
			.toString();
	}

	@Override
	public final int hashCode() {
		return 31 * getValueClass().hashCode() + this.name.hashCode();
	}

	@Override
	public Optional<A> parseValue(String value) {
		IAllele allele = GeneticsAPI.apiInstance.getAlleleRegistry().getAllele(value).orElse(null);
		Class<A> valueClass = getValueClass();
		if (valueClass.isInstance(allele)) {
			return Optional.of(valueClass.cast(allele));
		}
		return Optional.empty();
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
