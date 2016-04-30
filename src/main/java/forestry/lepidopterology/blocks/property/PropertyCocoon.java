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
package forestry.lepidopterology.blocks.property;

import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.List;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.lepidopterology.IAlleleButterflyCocoon;
import forestry.core.blocks.propertys.PropertyAllele;

public class PropertyCocoon extends PropertyAllele<IAlleleButterflyCocoon> {

	public PropertyCocoon(String name) {
		super(name);
	}

	@Override
	public Class<IAlleleButterflyCocoon> getValueClass() {
		return IAlleleButterflyCocoon.class;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("name", this.name).add("clazz", IAlleleButterflyCocoon.class).add("values", this.getAllowedValues()).toString();
	}

	@Override
	public int hashCode() {
		return 31 * IAlleleTreeSpecies.class.hashCode() + this.name.hashCode();
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
	
}
