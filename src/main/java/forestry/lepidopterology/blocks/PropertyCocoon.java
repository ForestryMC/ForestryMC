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
package forestry.lepidopterology.blocks;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.lepidopterology.IAlleleButterflyCocoon;
import forestry.core.blocks.properties.PropertyAllele;

public class PropertyCocoon extends PropertyAllele<IAlleleButterflyCocoon> {
	private static final Map<String, IAlleleButterflyCocoon> namesMap = new HashMap<>();

	public PropertyCocoon(String name) {
		super(name);
	}

	@Override
	public Class<IAlleleButterflyCocoon> getValueClass() {
		return IAlleleButterflyCocoon.class;
	}

	@Override
	public List<IAlleleButterflyCocoon> getAllowedValues() {
		List<IAlleleButterflyCocoon> trees = new ArrayList<>();
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleButterflyCocoon) {
				trees.add((IAlleleButterflyCocoon) allele);
			}
		}
		return trees;
	}

	@Override
	public String getName(IAlleleButterflyCocoon value) {
		return value.getCocoonName();
	}

	@Override
	public Optional<IAlleleButterflyCocoon> parseValue(String value) {
		if (namesMap.isEmpty()) {
			List<IAlleleButterflyCocoon> allowedValues = getAllowedValues();
			for (IAlleleButterflyCocoon allowedValue : allowedValues) {
				String propertyName = getName(allowedValue);
				namesMap.put(propertyName, allowedValue);
			}
		}
		return Optional.fromNullable(namesMap.get(value));
	}
}
