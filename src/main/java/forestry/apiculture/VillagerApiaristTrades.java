package forestry.apiculture;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class VillagerApiaristTrades {

	public static class GiveRandomCombsForItems implements EntityVillager.ITradeList {
		public ItemStack itemToBuy;
		@Nullable
		public EntityVillager.PriceInfo buyInfo;
		@Nullable
		public EntityVillager.PriceInfo priceInfo;

		public GiveRandomCombsForItems(ItemStack stack, @Nullable EntityVillager.PriceInfo buyInfo, @Nullable EntityVillager.PriceInfo priceInfo) {
			this.itemToBuy = stack;
			this.buyInfo = buyInfo;
			this.priceInfo = priceInfo;
		}

		@Override
		public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
			int sellAmount = 1;
			if (this.priceInfo != null) {
				sellAmount = this.priceInfo.getPrice(random);
			}

			int buyAmount = 1;
			if (this.buyInfo != null) {
				buyAmount = this.buyInfo.getPrice(random);
			}

			ItemStack itemToBuy = this.itemToBuy.copy();
			itemToBuy.stackSize = buyAmount;
			ItemStack randomComb = PluginApiculture.items.beeComb.getRandomComb(sellAmount, random, false);
			recipeList.add(new MerchantRecipe(itemToBuy, randomComb));
		}
	}

}
