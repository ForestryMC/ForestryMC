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

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

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
		if (sells.ordinal() > 15) {
			plankBlock = ForestryBlock.planks2.block();
			meta = sells.ordinal() - 16;
		} else {
			plankBlock = ForestryBlock.planks1.block();
			meta = sells.ordinal();
		}

		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1), new ItemStack(plankBlock, 32, meta)));
	}

}
