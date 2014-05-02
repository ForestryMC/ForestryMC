/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture;

import java.util.ArrayList;

import net.minecraft.world.gen.feature.WorldGenerator;

import forestry.api.arboriculture.ITree;
import forestry.api.genetics.IIndividual;
import forestry.api.world.IWorldGenInterface;
import forestry.plugins.PluginArboriculture;

public class WorldGenHelper implements IWorldGenInterface {

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends WorldGenerator>[] getTreeGenerators(String ident) {

		ArrayList<Class<? extends WorldGenerator>> generators = new ArrayList<Class<? extends WorldGenerator>>();
		for (IIndividual tree : PluginArboriculture.treeInterface.getIndividualTemplates())
			if (tree.getIdent().equals(ident))
				for (Class<? extends WorldGenerator> generator : ((ITree)tree).getGenome().getPrimary().getGeneratorClasses())
					generators.add(generator);

		return generators.toArray(new Class[0]);
	}

}
