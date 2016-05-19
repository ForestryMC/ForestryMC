package forestry.core.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import forestry.apiculture.PluginApiculture;

public class VillagerTradeLists {
	/**
	 * Copy of {@link net.minecraft.entity.passive.EntityVillager.ItemAndEmeraldToItem}
	 * that takes ItemStacks as parameters and has emerald price info
	 */
	public static class GiveItemForItemAndEmerald implements EntityVillager.ITradeList {
		@Nonnull
		public ItemStack buyingItemStack;
		@Nullable
		public EntityVillager.PriceInfo buyingPriceInfo;
		@Nullable
		public EntityVillager.PriceInfo emeraldPriceInfo;
		@Nonnull
		public ItemStack sellingItemstack;
		@Nullable
		public EntityVillager.PriceInfo sellingPriceInfo;

		public GiveItemForItemAndEmerald(
				@Nonnull ItemStack buyingItemStack,
				@Nullable EntityVillager.PriceInfo buyingPriceInfo,
				@Nullable EntityVillager.PriceInfo emeraldPriceInfo,
				@Nonnull ItemStack sellingItemstack,
				@Nullable EntityVillager.PriceInfo sellingPriceInfo) {
			this.buyingItemStack = buyingItemStack;
			this.buyingPriceInfo = buyingPriceInfo;
			this.emeraldPriceInfo = emeraldPriceInfo;
			this.sellingItemstack = sellingItemstack;
			this.sellingPriceInfo = sellingPriceInfo;
		}

		@Override
		public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
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
			buyItemStack.stackSize = buyAmount;
			ItemStack sellItemStack = this.sellingItemstack.copy();
			sellItemStack.stackSize = sellAmount;
			recipeList.add(new MerchantRecipe(buyItemStack, new ItemStack(Items.EMERALD, emeraldAmount, 0), sellItemStack));
		}
	}

	/**
	 * Copy of {@link EntityVillager.EmeraldForItems}
	 * that takes itemStack as a parameter
	 */
	public static class GiveEmeraldForItems implements EntityVillager.ITradeList {
		public ItemStack buyingItem;
		public EntityVillager.PriceInfo price;

		public GiveEmeraldForItems(ItemStack itemIn, EntityVillager.PriceInfo priceIn) {
			this.buyingItem = itemIn;
			this.price = priceIn;
		}

		/**
		 * Affects the given MerchantRecipeList to possibly add or remove MerchantRecipes.
		 */
		@Override
		public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
			int buyAmount = 1;
			if (this.price != null) {
				buyAmount = this.price.getPrice(random);
			}

			ItemStack itemToBuy = this.buyingItem.copy();
			itemToBuy.stackSize = buyAmount;
			recipeList.add(new MerchantRecipe(itemToBuy, Items.EMERALD));
		}
	}

	/**
	 * Copy of {@link EntityVillager.ListItemForEmeralds}
	 * that copies itemStacks properly
	 */
	public static class GiveItemForEmeralds implements EntityVillager.ITradeList {
		@Nullable
		public EntityVillager.PriceInfo emeraldPriceInfo;
		@Nonnull
		public ItemStack itemToSell;
		@Nullable
		public EntityVillager.PriceInfo sellInfo;

		public GiveItemForEmeralds(@Nullable EntityVillager.PriceInfo emeraldPriceInfo, @Nonnull ItemStack itemToSell, @Nullable EntityVillager.PriceInfo sellInfo) {
			this.emeraldPriceInfo = emeraldPriceInfo;
			this.itemToSell = itemToSell;
			this.sellInfo = sellInfo;
		}

		@Override
		public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
			int i = 1;
			if (this.sellInfo != null) {
				i = this.sellInfo.getPrice(random);
			}

			int j = 1;
			if (this.emeraldPriceInfo != null) {
				j = this.emeraldPriceInfo.getPrice(random);
			}

			ItemStack sellStack = this.itemToSell.copy();
			sellStack.stackSize = i;

			ItemStack emeralds = new ItemStack(Items.EMERALD, j);

			recipeList.add(new MerchantRecipe(emeralds, sellStack));
		}
	}

	public static class GiveItemForLogsAndEmeralds implements EntityVillager.ITradeList {
		@Nonnull
		public ItemStack itemToSell;
		@Nonnull
		public EntityVillager.PriceInfo itemInfo;
		@Nonnull
		public EntityVillager.PriceInfo logsInfo;
		@Nonnull
		public EntityVillager.PriceInfo emeraldsInfo;

		public GiveItemForLogsAndEmeralds(@Nonnull ItemStack itemToSell, @Nonnull EntityVillager.PriceInfo itemInfo, @Nonnull EntityVillager.PriceInfo logsInfo, @Nonnull EntityVillager.PriceInfo emeraldsInfo) {
			this.itemToSell = itemToSell;
			this.itemInfo = itemInfo;
			this.logsInfo = logsInfo;
			this.emeraldsInfo = emeraldsInfo;
		}

		@Override
		public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
			int itemAmount = this.itemInfo.getPrice(random);
			int emeraldsAmount = this.emeraldsInfo.getPrice(random);
			int logsAmount = this.logsInfo.getPrice(random);

			ItemStack itemToSell = this.itemToSell.copy();
			itemToSell.stackSize = itemAmount;

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
}
