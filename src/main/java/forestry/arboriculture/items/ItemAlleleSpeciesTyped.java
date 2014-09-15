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
package forestry.arboriculture.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.IAlleleSpecies;
import forestry.arboriculture.gadgets.IAlleleSpeciesTyped;
import forestry.core.utils.StringUtil;
import forestry.core.items.ItemForestryBlock;

public class ItemAlleleSpeciesTyped extends ItemForestryBlock {

	public ItemAlleleSpeciesTyped(Block block) {
		super(block);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		int meta = itemstack.getItemDamage();
		IAlleleSpeciesTyped alleleTypedBlock = (IAlleleSpeciesTyped)getBlock();
		IAlleleSpecies species = alleleTypedBlock.getAlleleForMeta(meta);

		if (alleleTypedBlock == null || species == null)
			return super.getUnlocalizedName();

		String blockKind = alleleTypedBlock.getBlockKind();
		String customName;
		String grammarPrefix;

		if (species instanceof IAlleleTreeSpecies) {
			customName = "trees.custom.treealyzer." + blockKind + "." + species.getUnlocalizedName().replace("trees.species.", "");
			grammarPrefix = "trees.grammar.";
		} else {
			return super.getUnlocalizedName();
		}

		if(StatCollector.canTranslate("for." + customName)){
			return customName;
		}

		String grammar = StringUtil.localize(grammarPrefix + blockKind);
		String typeName = StringUtil.localize(grammarPrefix + blockKind + ".type");

		return grammar.replaceAll("%SPECIES", species.getName()).replaceAll("%TYPE", typeName);
	}

}
