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
package forestry.arboriculture;

import forestry.api.arboriculture.ITree;
import forestry.api.genetics.IIndividual;
import forestry.api.world.IWorldGenInterface;
import forestry.plugins.PluginArboriculture;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.ArrayList;
import java.util.Collections;

public class WorldGenHelper implements IWorldGenInterface {

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Class<? extends WorldGenerator>[] getTreeGenerators(String ident) {

		ArrayList<Class<? extends WorldGenerator>> generators = new ArrayList<Class<? extends WorldGenerator>>();
		for (IIndividual tree : PluginArboriculture.treeInterface.getIndividualTemplates())
			if (tree.getIdent().equals(ident))
				Collections.addAll(generators, ((ITree) tree).getGenome().getPrimary().getGeneratorClasses());

		return generators.toArray(new Class[generators.size()]);
	}

}
