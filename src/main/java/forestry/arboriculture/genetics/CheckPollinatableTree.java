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
package forestry.arboriculture.genetics;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.arboriculture.ITree;
import forestry.api.genetics.ICheckPollinatable;
import forestry.api.genetics.IIndividual;
import forestry.apiculture.ModuleApiculture;

public class CheckPollinatableTree implements ICheckPollinatable {
	private final ITree tree;

	public CheckPollinatableTree(ITree tree) {
		this.tree = tree;
	}

	@Override
	public EnumPlantType getPlantType() {
		return tree.getGenome().getPrimary().getPlantType();
	}

	@Override
	public ITree getPollen() {
		return tree;
	}

	@Override
	public boolean canMateWith(IIndividual pollen) {
		return pollen instanceof ITree &&
			tree.getMate() == null &&
			(ModuleApiculture.doSelfPollination || !tree.isGeneticEqual(pollen));
	}

	@Override
	public boolean isPollinated() {
		return tree.getMate() != null;
	}
}
