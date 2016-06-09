package forestry.apiculture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import forestry.api.apiculture.EnumBeeType;
import forestry.apiculture.genetics.BeeDefinition;

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
		public void modifyMerchantRecipeList(@Nonnull MerchantRecipeList recipeList, @Nonnull Random random) {
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
	public static class GiveRandomHiveDroneForItems implements EntityVillager.ITradeList {
		@Nonnull
		public ItemStack buyingItemStack;
		@Nullable
		public EntityVillager.PriceInfo buyingPriceInfo;
		@Nonnull
		public ItemStack buyingItemStackTwo;
		@Nullable
		public EntityVillager.PriceInfo buyingPriceItemTwoInfo;

		public GiveRandomHiveDroneForItems(
				@Nonnull ItemStack buyingItemStack,
				@Nullable EntityVillager.PriceInfo buyingPriceInfo,
				@Nonnull ItemStack buyingItemStackTwo,
				@Nullable EntityVillager.PriceInfo buyingPriceItemTwoInfo) {
			this.buyingItemStack = buyingItemStack;
			this.buyingPriceInfo = buyingPriceInfo;
			this.buyingItemStackTwo = buyingItemStackTwo;
			this.buyingPriceItemTwoInfo = buyingPriceItemTwoInfo;
		}

		@Override
		public void modifyMerchantRecipeList(@Nonnull MerchantRecipeList recipeList, @Nonnull Random random) {

			int buyAmount = 1;
			if (this.buyingPriceInfo != null) {
				buyAmount = this.buyingPriceInfo.getPrice(random);
			}

			int buyTwoAmount = 1;
			if (this.buyingPriceItemTwoInfo != null) {
				buyTwoAmount = this.buyingPriceItemTwoInfo.getPrice(random);
			}
			BeeDefinition [] forestryMundane = new BeeDefinition[] { BeeDefinition.FOREST, BeeDefinition.MEADOWS, BeeDefinition.MODEST, BeeDefinition.WINTRY, BeeDefinition.TROPICAL, BeeDefinition.MARSHY };
			ItemStack randomHiveDrone = forestryMundane[random.nextInt(forestryMundane.length)].getMemberStack(EnumBeeType.DRONE);
			ItemStack buyItemStack = this.buyingItemStack.copy();
			buyItemStack.stackSize = buyAmount;
			ItemStack buyItemStackTwo = this.buyingItemStackTwo.copy();
			buyItemStackTwo.stackSize = buyTwoAmount;
			recipeList.add(new MerchantRecipe(buyItemStack, buyItemStackTwo, randomHiveDrone));
		}
	}
}
