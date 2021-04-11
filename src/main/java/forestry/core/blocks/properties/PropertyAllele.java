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

import java.util.Optional;

import net.minecraft.state.Property;

import forestry.api.genetics.alleles.IAlleleProperty;

import genetics.api.alleles.IAllele;
import genetics.utils.AlleleUtils;

public abstract class PropertyAllele<A extends IAlleleProperty<A>> extends Property<A> {

	public PropertyAllele(String name, Class<A> valueClass) {
		super(name, valueClass);
	}

	@Override
	public Optional<A> getValue(String value) {
		IAllele allele = AlleleUtils.getAlleleOrNull(value);
		Class<A> valueClass = getValueClass();
		if (valueClass.isInstance(allele)) {
			return Optional.of(valueClass.cast(allele));
		}
		return Optional.empty();
	}

}
