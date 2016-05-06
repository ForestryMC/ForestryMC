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

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.core.blocks.propertys.PropertyAllele;

public class PropertyTree extends PropertyAllele<IAlleleTreeSpecies> {

	public PropertyTree(String name) {
		super(name);
	}

	@Override
	public Class<IAlleleTreeSpecies> getValueClass() {
		return IAlleleTreeSpecies.class;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("name", this.name).add("clazz", IAlleleTreeSpecies.class).add("values", this.getAllowedValues()).toString();
	}

	@Override
	public int hashCode() {
		return 31 * IAlleleTreeSpecies.class.hashCode() + this.name.hashCode();
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
		return value.getModelName().replace("tree", "").toLowerCase(Locale.ENGLISH);
	}
	
}
