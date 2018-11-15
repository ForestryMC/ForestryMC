package forestry.core.utils;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class VillagerTradeLists {
	/**
	 * Copy of {@link net.minecraft.entity.passive.EntityVillager.ItemAndEmeraldToItem}
	 * that takes ItemStacks as parameters and has emerald price info
	 */
	public static class GiveItemForItemAndEmerald implements EntityVillager.ITradeList {
		public final ItemStack buyingItemStack;
		public final ItemStack sellingItemstack;

		@Nullable
		public final EntityVillager.PriceInfo buyingPriceInfo;
		@Nullable
		public final EntityVillager.PriceInfo emeraldPriceInfo;
		@Nullable
		public final EntityVillager.PriceInfo sellingPriceInfo;

		public GiveItemForItemAndEmerald(
			ItemStack buyingItemStack,
			@Nullable EntityVillager.PriceInfo buyingPriceInfo,
			@Nullable EntityVillager.PriceInfo emeraldPriceInfo,
			ItemStack sellingItemstack,
			@Nullable EntityVillager.PriceInfo sellingPriceInfo) {
			this.buyingItemStack = buyingItemStack;
			this.buyingPriceInfo = buyingPriceInfo;
			this.emeraldPriceInfo = emeraldPriceInfo;
			this.sellingItemstack = sellingItemstack;
			this.sellingPriceInfo = sellingPriceInfo;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
			int buyAmount = 1;
			if (this.buyingPriceInfo != null) {
				buyAmount = this.buyingPriceInfo.getPrice(random);
			}

			int emeraldAmount = 1;
			if (this.emeraldPriceInfo != null) {
				emeraldAmount = this.emeraldPriceInfo.getPrice(random);
			}

			int sellAmount = 1;
			if (this.sellingPriceInfo != null) {
				sellAmount = this.sellingPriceInfo.getPrice(random);
			}

			ItemStack buyItemStack = this.buyingItemStack.copy();
			buyItemStack.setCount(buyAmount);
			ItemStack sellItemStack = this.sellingItemstack.copy();
			sellItemStack.setCount(sellAmount);
			recipeList.add(new MerchantRecipe(buyItemStack, new ItemStack(Items.EMERALD, emeraldAmount, 0), sellItemStack));
		}
	}

	/**
	 * Copy of {@link EntityVillager.EmeraldForItems}
	 * that takes itemStack as a parameter
	 */
	public static class GiveEmeraldForItems implements EntityVillager.ITradeList {
		public final ItemStack buyingItem;
		@Nullable
		public final EntityVillager.PriceInfo price;

		public GiveEmeraldForItems(ItemStack itemIn, @Nullable EntityVillager.PriceInfo priceIn) {
			this.buyingItem = itemIn;
			this.price = priceIn;
		}

		/**
		 * Affects the given MerchantRecipeList to possibly add or remove MerchantRecipes.
		 */
		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
			int buyAmount = 1;
			if (this.price != null) {
				buyAmount = this.price.getPrice(random);
			}

			ItemStack itemToBuy = this.buyingItem.copy();
			itemToBuy.setCount(buyAmount);
			recipeList.add(new MerchantRecipe(itemToBuy, Items.EMERALD));
		}
	}

	/**
	 * Copy of {@link EntityVillager.ListItemForEmeralds}
	 * that copies itemStacks properly
	 */
	public static class GiveItemForEmeralds implements EntityVillager.ITradeList {
		public final ItemStack itemToSell;
		@Nullable
		public final EntityVillager.PriceInfo emeraldPriceInfo;
		@Nullable
		public final EntityVillager.PriceInfo sellInfo;

		public GiveItemForEmeralds(@Nullable EntityVillager.PriceInfo emeraldPriceInfo, ItemStack itemToSell, @Nullable EntityVillager.PriceInfo sellInfo) {
			this.emeraldPriceInfo = emeraldPriceInfo;
			this.itemToSell = itemToSell;
			this.sellInfo = sellInfo;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
			int i = 1;
			if (this.sellInfo != null) {
				i = this.sellInfo.getPrice(random);
			}

			int j = 1;
			if (this.emeraldPriceInfo != null) {
				j = this.emeraldPriceInfo.getPrice(random);
			}

			ItemStack sellStack = this.itemToSell.copy();
			sellStack.setCount(i);

			ItemStack emeralds = new ItemStack(Items.EMERALD, j);

			recipeList.add(new MerchantRecipe(emeralds, sellStack));
		}
	}

	public static class GiveItemForLogsAndEmeralds implements EntityVillager.ITradeList {

		public final ItemStack itemToSell;

		public final EntityVillager.PriceInfo itemInfo;

		public final EntityVillager.PriceInfo logsInfo;

		public final EntityVillager.PriceInfo emeraldsInfo;

		public GiveItemForLogsAndEmeralds(ItemStack itemToSell, EntityVillager.PriceInfo itemInfo, EntityVillager.PriceInfo logsInfo, EntityVillager.PriceInfo emeraldsInfo) {
			this.itemToSell = itemToSell;
			this.itemInfo = itemInfo;
			this.logsInfo = logsInfo;
			this.emeraldsInfo = emeraldsInfo;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
			int itemAmount = this.itemInfo.getPrice(random);
			int emeraldsAmount = this.emeraldsInfo.getPrice(random);
			int logsAmount = this.logsInfo.getPrice(random);

			ItemStack itemToSell = this.itemToSell.copy();
			itemToSell.setCount(itemAmount);

			int logMeta = random.nextInt(6);
			Block log;
			if (logMeta < 4) {
				log = Blocks.LOG;
			} else {
				log = Blocks.LOG2;
				logMeta -= 4;
			}
			ItemStack randomLog = new ItemStack(log, logsAmount, logMeta);

			recipeList.add(new MerchantRecipe(randomLog, new ItemStack(Items.EMERALD, emeraldsAmount), itemToSell));
		}
	}

	public static class GiveItemForTwoItems implements EntityVillager.ITradeList {

		public final ItemStack buyingItemStack;
		@Nullable
		public final EntityVillager.PriceInfo buyingPriceInfo;

		public final ItemStack buyingItemStackTwo;
		@Nullable
		public final EntityVillager.PriceInfo buyingPriceItemTwoInfo;

		public final ItemStack sellingItemstack;
		@Nullable
		public final EntityVillager.PriceInfo sellingPriceInfo;

		public GiveItemForTwoItems(
			ItemStack buyingItemStack,
			@Nullable EntityVillager.PriceInfo buyingPriceInfo,
			ItemStack buyingItemStackTwo,
			@Nullable EntityVillager.PriceInfo buyingPriceItemTwoInfo,
			ItemStack sellingItemstack,
			@Nullable EntityVillager.PriceInfo sellingPriceInfo) {
			this.buyingItemStack = buyingItemStack;
			this.buyingPriceInfo = buyingPriceInfo;
			this.buyingItemStackTwo = buyingItemStackTwo;
			this.buyingPriceItemTwoInfo = buyingPriceItemTwoInfo;
			this.sellingItemstack = sellingItemstack;
			this.sellingPriceInfo = sellingPriceInfo;
		}

		@Override
		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
			int buyAmount = 1;
			if (this.buyingPriceInfo != null) {
				buyAmount = this.buyingPriceInfo.getPrice(random);
			}

			int buyTwoAmount = 1;
			if (this.buyingPriceItemTwoInfo != null) {
				buyTwoAmount = this.buyingPriceItemTwoInfo.getPrice(random);
			}

			int sellAmount = 1;
			if (this.sellingPriceInfo != null) {
				sellAmount = this.sellingPriceInfo.getPrice(random);
			}

			ItemStack buyItemStack = this.buyingItemStack.copy();
			buyItemStack.setCount(buyAmount);
			ItemStack buyItemStackTwo = this.buyingItemStackTwo.copy();
			buyItemStackTwo.setCount(buyTwoAmount);
			ItemStack sellItemStack = this.sellingItemstack.copy();
			sellItemStack.setCount(sellAmount);
			recipeList.add(new MerchantRecipe(buyItemStack, buyItemStackTwo, sellItemStack));
		}
	}
}
