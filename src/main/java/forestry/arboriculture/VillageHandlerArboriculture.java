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

import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IAllele;
import forestry.plugins.PluginArboriculture;

public class VillageHandlerArboriculture implements IVillageTradeHandler {

	@SuppressWarnings("unchecked")
	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
		IAllele[] randomTemplate = TreeManager.treeRoot.getRandomTemplate(random);
		ITreeGenome randomGenome = TreeManager.treeRoot.templateAsGenome(randomTemplate);
		ITree randomTree = TreeManager.treeRoot.getTree(villager.worldObj, randomGenome);
		ItemStack randomTreeStack = TreeManager.treeRoot.getMemberStack(randomTree, EnumGermlingType.SAPLING.ordinal());

		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 8), randomTreeStack));
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 2), PluginArboriculture.items.grafterProven.getItemStack()));

		EnumWoodType randomWoodType = EnumWoodType.getRandom(random);
		ItemStack planks = TreeManager.woodItemAccess.getPlanks(randomWoodType, false);
		planks.stackSize = 32;

		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1), planks));
	}

}
