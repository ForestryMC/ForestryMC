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

@Deprecated
public class VillageHandlerArboriculture {

	/*@SuppressWarnings("unchecked")
	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
		IAllele[] randomTemplate = TreeManager.treeRoot.getRandomTemplate(random);
		ITreeGenome randomGenome = TreeManager.treeRoot.templateAsGenome(randomTemplate);
		ITree randomTree = TreeManager.treeRoot.getTree(villager.worldObj, randomGenome);
		ItemStack randomTreeStack = TreeManager.treeRoot.getMemberStack(randomTree, EnumGermlingType.SAPLING.ordinal());

		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 8), randomTreeStack));
		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 2), ForestryItem.grafterProven.getItemStack()));

		WoodType randomWoodType = WoodType.VALUES[random.nextInt(WoodType.VALUES.length)];
		ItemStack planks = randomWoodType.getPlanks(false);
		planks.stackSize = 32;

		recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1), planks));
	}*/

}
