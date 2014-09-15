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

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.arboriculture.gadgets.BlockSapling;
import forestry.core.utils.StringUtil;
import forestry.core.items.ItemForestryBlock;

public class ItemSapling extends ItemForestryBlock {

	public ItemSapling(Block block) {
		super(block);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		int meta = itemstack.getItemDamage();
		BlockSapling saplingBlock = (BlockSapling)getBlock();
		IAlleleTreeSpecies treeSpecies = saplingBlock.getAllele(meta);

		if (saplingBlock == null || treeSpecies == null)
			return super.getUnlocalizedName();

		EnumGermlingType type = EnumGermlingType.SAPLING;

		String customName = "trees.custom.treealyzer." + type.getName() + "." + treeSpecies.getUnlocalizedName().replace("trees.species.", "");
		if(StatCollector.canTranslate("for." + customName)){
			return customName;
		}

		String treeGrammar = StringUtil.localize("trees.grammar." + type.getName());
		String typeName = StringUtil.localize("trees.grammar." + type.getName() + ".type");
		String treeSpeciesName = treeSpecies.getName();

		return treeGrammar.replaceAll("%SPECIES", treeSpeciesName).replaceAll("%TYPE", typeName);
	}

}
