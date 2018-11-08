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

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.core.blocks.properties.PropertyAllele;
import forestry.core.config.Constants;

public class PropertyTree extends PropertyAllele<IAlleleTreeSpecies> {
	private static final Map<String, IAlleleTreeSpecies> namesMap = new HashMap<>();

	public PropertyTree(String name) {
		super(name);
	}

	@Override
	public Class<IAlleleTreeSpecies> getValueClass() {
		return IAlleleTreeSpecies.class;
	}

	@Override
	public List<IAlleleTreeSpecies> getAllowedValues() {
		List<IAlleleTreeSpecies> trees = new ArrayList<>();
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleTreeSpecies) {
				trees.add((IAlleleTreeSpecies) allele);
			}
		}
		return trees;
	}

	@Override
	public String getName(IAlleleTreeSpecies value) {
		return value.getUID().replace(Constants.MOD_ID + ".tree", "").toLowerCase(Locale.ENGLISH);
	}

	@Override
	public Optional<IAlleleTreeSpecies> parseValue(String value) {
		if (namesMap.isEmpty()) {
			List<IAlleleTreeSpecies> allowedValues = getAllowedValues();
			for (IAlleleTreeSpecies allowedValue : allowedValues) {
				String propertyName = getName(allowedValue);
				namesMap.put(propertyName, allowedValue);
			}
		}
		return Optional.fromNullable(namesMap.get(value));
	}

}
