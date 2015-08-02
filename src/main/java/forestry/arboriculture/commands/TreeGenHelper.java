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
package forestry.arboriculture.commands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.gen.feature.WorldGenerator;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.core.commands.SpeciesNotFoundException;
import forestry.core.commands.TemplateNotFoundException;
import forestry.core.worldgen.WorldGenBase;

public final class TreeGenHelper {

	public static WorldGenerator getWorldGen(String treeName, EntityPlayer player, int x, int y, int z) {
		ITreeGenome treeGenome = getTreeGenome(treeName);
		if (treeGenome == null) {
			return null;
		}

		ITree tree = TreeManager.treeRoot.getTree(player.worldObj, treeGenome);
		return tree.getTreeGenerator(player.worldObj, x, y, z, true);
	}

	public static void generateTree(WorldGenerator gen, EntityPlayer player, int x, int y, int z) {
		if (gen instanceof WorldGenBase) {
			((WorldGenBase) gen).generate(player.worldObj, x, y, z, true);
		} else {
			gen.generate(player.worldObj, player.worldObj.rand, x, y, z);
		}
	}

	private static ITreeGenome getTreeGenome(String speciesName) {
		IAlleleTreeSpecies species = null;

		for (String uid : AlleleManager.alleleRegistry.getRegisteredAlleles().keySet()) {

			if (!uid.equals(speciesName)) {
				continue;
			}

			IAllele allele = AlleleManager.alleleRegistry.getAllele(uid);
			if (allele instanceof IAlleleTreeSpecies) {
				species = (IAlleleTreeSpecies) allele;
				break;
			}
		}

		if (species == null) {
			for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
				if (allele instanceof IAlleleTreeSpecies && allele.getName().replaceAll("\\s", "").equals(speciesName)) {
					species = (IAlleleTreeSpecies) allele;
					break;
				}
			}
		}

		if (species == null) {
			throw new SpeciesNotFoundException(speciesName);
		}

		IAllele[] template = TreeManager.treeRoot.getTemplate(species.getUID());

		if (template == null) {
			throw new TemplateNotFoundException(species);
		}

		return TreeManager.treeRoot.templateAsGenome(template);
	}
}
