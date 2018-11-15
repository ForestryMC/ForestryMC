package forestry.arboriculture;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IIndividual;

public class VillagerArboristTrades {

	public static class GiveLogsForEmeralds implements EntityVillager.ITradeList {
		@Nullable
		public final EntityVillager.PriceInfo emeraldPriceInfo;
		@Nullable
		public final EntityVillager.PriceInfo sellInfo;

		public GiveLogsForEmeralds(@Nullable EntityVillager.PriceInfo emeraldPriceInfo, @Nullable EntityVillager.PriceInfo sellInfo) {
			this.emeraldPriceInfo = emeraldPriceInfo;
			this.sellInfo = sellInfo;
		}

		@Override
		public void addMerchantRecipe(IMerchant p_190888_1_, MerchantRecipeList recipeList, Random random) {
			int i = 1;
			if (this.sellInfo != null) {
				i = this.sellInfo.getPrice(random);
			}

			int j = 1;
			if (this.emeraldPriceInfo != null) {
				j = this.emeraldPriceInfo.getPrice(random);
			}

			EnumForestryWoodType woodType = EnumForestryWoodType.getRandom(random);
			ItemStack sellStack = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.LOG, false);
			sellStack.setCount(i);

			ItemStack emeralds = new ItemStack(Items.EMERALD, j);

			recipeList.add(new MerchantRecipe(emeralds, sellStack));
		}
	}

	public static class GivePlanksForEmeralds implements EntityVillager.ITradeList {
		@Nullable
		public final EntityVillager.PriceInfo emeraldPriceInfo;
		@Nullable
		public final EntityVillager.PriceInfo sellInfo;

		public GivePlanksForEmeralds(@Nullable EntityVillager.PriceInfo emeraldPriceInfo, @Nullable EntityVillager.PriceInfo sellInfo) {
			this.emeraldPriceInfo = emeraldPriceInfo;
			this.sellInfo = sellInfo;
		}

		@Override
		public void addMerchantRecipe(IMerchant p_190888_1_, MerchantRecipeList recipeList, Random random) {
			int i = 1;
			if (this.sellInfo != null) {
				i = this.sellInfo.getPrice(random);
			}

			int j = 1;
			if (this.emeraldPriceInfo != null) {
				j = this.emeraldPriceInfo.getPrice(random);
			}

			EnumForestryWoodType woodType = EnumForestryWoodType.getRandom(random);
			ItemStack sellStack = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.PLANKS, false);
			sellStack.setCount(i);

			ItemStack emeralds = new ItemStack(Items.EMERALD, j);

			recipeList.add(new MerchantRecipe(emeralds, sellStack));
		}
	}

	public static class GivePollenForEmeralds implements EntityVillager.ITradeList {
		@Nullable
		public final EntityVillager.PriceInfo emeraldPriceInfo;
		@Nullable
		public final EntityVillager.PriceInfo sellInfo;

		private final EnumGermlingType type;

		private final int maxComplexity;

		public GivePollenForEmeralds(@Nullable EntityVillager.PriceInfo emeraldPriceInfo, @Nullable EntityVillager.PriceInfo sellInfo, EnumGermlingType type, int maxComplexity) {
			this.emeraldPriceInfo = emeraldPriceInfo;
			this.sellInfo = sellInfo;
			this.type = type;
			this.maxComplexity = maxComplexity;
		}

		@Override
		public void addMerchantRecipe(IMerchant p_190888_1_, MerchantRecipeList recipeList, Random random) {
			int i = 1;
			if (this.sellInfo != null) {
				i = this.sellInfo.getPrice(random);
			}

			int j = 1;
			if (this.emeraldPriceInfo != null) {
				j = this.emeraldPriceInfo.getPrice(random);
			}

			IChromosomeType treeSpeciesType = TreeManager.treeRoot.getSpeciesChromosomeType();
			Collection<IAllele> registeredSpecies = AlleleManager.alleleRegistry.getRegisteredAlleles(treeSpeciesType);
			List<IAlleleSpecies> potentialSpecies = new ArrayList<>();
			for (IAllele allele : registeredSpecies) {
				if (allele instanceof IAlleleSpecies) {
					IAlleleSpecies species = (IAlleleSpecies) allele;
					if (species.getComplexity() <= maxComplexity) {
						potentialSpecies.add(species);
					}
				}
			}

			if (potentialSpecies.isEmpty()) {
				return;
			}

			IAlleleSpecies chosenSpecies = potentialSpecies.get(random.nextInt(potentialSpecies.size()));
			IAllele[] template = TreeManager.treeRoot.getTemplate(chosenSpecies);
			IIndividual individual = TreeManager.treeRoot.templateAsIndividual(template);

			ItemStack sellStack = TreeManager.treeRoot.getMemberStack(individual, type);
			sellStack.setCount(i);

			ItemStack emeralds = new ItemStack(Items.EMERALD, j);

			recipeList.add(new MerchantRecipe(emeralds, sellStack));
		}
	}
}
