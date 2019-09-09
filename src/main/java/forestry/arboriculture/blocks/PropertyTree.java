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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;

import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.core.blocks.properties.PropertyAllele;

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
		for (IAllele allele : GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredAlleles(TreeChromosomes.SPECIES)) {
			if (allele instanceof IAlleleTreeSpecies) {
				trees.add((IAlleleTreeSpecies) allele);
			}
		}
		return trees;
	}

	@Override
	public String getName(IAlleleTreeSpecies value) {
		return value.getRegistryName().getPath().replace("tree_", "").toLowerCase(Locale.ENGLISH);
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
		return Optional.ofNullable(namesMap.get(value));
	}

}
