/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.plugins.PluginArboriculture;

public class VillageHandlerArboriculture implements IVillageTradeHandler {

	@SuppressWarnings("unchecked")
	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 8), PluginArboriculture.treeInterface.getMemberStack(
				PluginArboriculture.treeInterface.getTree(villager.worldObj,
						PluginArboriculture.treeInterface.templateAsGenome(PluginArboriculture.treeInterface.getRandomTemplate(random))),
						EnumGermlingType.SAPLING.ordinal())));

		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 2), ForestryItem.grafterProven.getItemStack()));

		WoodType sells = WoodType.VALUES[random.nextInt(WoodType.VALUES.length)];
		Block plankBlock;
		int meta;
		if(!sells.hasPlank) {
			plankBlock = ForestryBlock.planks1;
			meta = 0;
		} else if(sells.ordinal() > 15) {
			plankBlock = ForestryBlock.planks2;
			meta = sells.ordinal() - 16;
		} else {
			plankBlock = ForestryBlock.planks1;
			meta = sells.ordinal();
		}

		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1), new ItemStack(plankBlock, 32, meta)));
	}

}
