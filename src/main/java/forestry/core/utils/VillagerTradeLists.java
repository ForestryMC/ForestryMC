//package forestry.core.utils;
//
//import java.util.Random;
//
//import net.minecraft.block.Block;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.merchant.villager.VillagerEntity;
//import net.minecraft.entity.merchant.villager.VillagerTrades;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//import net.minecraft.item.MerchantOffer;
//import net.minecraft.util.IItemProvider;
//
////TODO - come back to this when params are a bit clearer
//public class VillagerTradeLists {
//	//TODO find where this class is now and copy it
//
//	/**
//	 * Copy of {@link VillagerTrades.ItemsForEmeraldsTrade}
//	 * that takes ItemStacks as parameters and has emerald price info
//	 * TODO - can we just AT the villager trade now? (or perhaps get it from a registry?
//	 * or just use provided constructor in fact????
//	 */
//	static class ItemsForEmeraldsTrade implements VillagerTrades.ITrade {
//		private final ItemStack itemStack;
//		private final int emeraldCost;
//		private final int numberOfItems;
//		private final int maxUses;
//		private final int villagerXp;
//		private final float priceMultiplier;
//
//		public ItemsForEmeraldsTrade(Block p_i50528_1_, int p_i50528_2_, int p_i50528_3_, int p_i50528_4_, int p_i50528_5_) {
//			this(new ItemStack(p_i50528_1_), p_i50528_2_, p_i50528_3_, p_i50528_4_, p_i50528_5_);
//		}
//
//		public ItemsForEmeraldsTrade(Item p_i50529_1_, int p_i50529_2_, int p_i50529_3_, int p_i50529_4_) {
//			this(new ItemStack(p_i50529_1_), p_i50529_2_, p_i50529_3_, 6, p_i50529_4_);
//		}
//
//		public ItemsForEmeraldsTrade(Item p_i50530_1_, int p_i50530_2_, int p_i50530_3_, int p_i50530_4_, int p_i50530_5_) {
//			this(new ItemStack(p_i50530_1_), p_i50530_2_, p_i50530_3_, p_i50530_4_, p_i50530_5_);
//		}
//
//		public ItemsForEmeraldsTrade(ItemStack p_i50531_1_, int p_i50531_2_, int p_i50531_3_, int p_i50531_4_, int p_i50531_5_) {
//			this(p_i50531_1_, p_i50531_2_, p_i50531_3_, p_i50531_4_, p_i50531_5_, 0.05F);
//		}
//
//		public ItemsForEmeraldsTrade(ItemStack p_i50532_1_, int p_i50532_2_, int p_i50532_3_, int p_i50532_4_, int p_i50532_5_, float p_i50532_6_) {
//			this.itemStack = p_i50532_1_;
//			this.emeraldCost = p_i50532_2_;
//			this.numberOfItems = p_i50532_3_;
//			this.maxUses = p_i50532_4_;
//			this.villagerXp = p_i50532_5_;
//			this.priceMultiplier = p_i50532_6_;
//		}
//
//		public MerchantOffer getOffer(Entity p_221182_1_, Random p_221182_2_) {
//			return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(this.itemStack.getItem(), this.numberOfItems), this.maxUses, this.villagerXp, this.priceMultiplier);
//		}
//	}
//
//	/**
//	 * Copy of {@link VillagerTrades.EmeraldForItemsTrade}
//	 * that takes itemStack as a parameter
//	 * TODO - needs ItemStack param?
//	 */
//	static class EmeraldForItemsTrade implements VillagerTrades.ITrade {
//		private final Item item;
//		private final int cost;
//		private final int maxUses;
//		private final int villagerXp;
//		private final float priceMultiplier;
//
//		public EmeraldForItemsTrade(IItemProvider p_i50539_1_, int p_i50539_2_, int p_i50539_3_, int p_i50539_4_) {
//			this.item = p_i50539_1_.asItem();
//			this.cost = p_i50539_2_;
//			this.maxUses = p_i50539_3_;
//			this.villagerXp = p_i50539_4_;
//			this.priceMultiplier = 0.05F;
//		}
//
//		public MerchantOffer getOffer(Entity p_221182_1_, Random p_221182_2_) {
//			ItemStack itemstack = new ItemStack(this.item, this.cost);
//			return new MerchantOffer(itemstack, new ItemStack(Items.EMERALD), this.maxUses, this.villagerXp, this.priceMultiplier);
//		}
//	}
//
//	/**
//	 * Copy of {@link VillagerEntity.ListItemForEmeralds}
//	 * that copies itemStacks properly
//	 */
//	//TODO - trades
//	//	public static class GiveItemForEmeralds implements VillagerEntity.ITradeList {
//	//		public final ItemStack itemToSell;
//	//		@Nullable
//	//		public final VillagerEntity.PriceInfo emeraldPriceInfo;
//	//		@Nullable
//	//		public final VillagerEntity.PriceInfo sellInfo;
//	//
//	//		public GiveItemForEmeralds(@Nullable VillagerEntity.PriceInfo emeraldPriceInfo, ItemStack itemToSell, @Nullable VillagerEntity.PriceInfo sellInfo) {
//	//			this.emeraldPriceInfo = emeraldPriceInfo;
//	//			this.itemToSell = itemToSell;
//	//			this.sellInfo = sellInfo;
//	//		}
//	//
//	//		@Override
//	//		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
//	//			int i = 1;
//	//			if (this.sellInfo != null) {
//	//				i = this.sellInfo.getPrice(random);
//	//			}
//	//
//	//			int j = 1;
//	//			if (this.emeraldPriceInfo != null) {
//	//				j = this.emeraldPriceInfo.getPrice(random);
//	//			}
//	//
//	//			ItemStack sellStack = this.itemToSell.copy();
//	//			sellStack.setCount(i);
//	//
//	//			ItemStack emeralds = new ItemStack(Items.EMERALD, j);
//	//
//	//			recipeList.add(new MerchantRecipe(emeralds, sellStack));
//	//		}
//	//	}
//	//
//	//	public static class GiveItemForLogsAndEmeralds implements VillagerEntity.ITradeList {
//	//
//	//		public final ItemStack itemToSell;
//	//
//	//		public final VillagerEntity.PriceInfo itemInfo;
//	//
//	//		public final VillagerEntity.PriceInfo logsInfo;
//	//
//	//		public final VillagerEntity.PriceInfo emeraldsInfo;
//	//
//	//		public GiveItemForLogsAndEmeralds(ItemStack itemToSell, VillagerEntity.PriceInfo itemInfo, VillagerEntity.PriceInfo logsInfo, VillagerEntity.PriceInfo emeraldsInfo) {
//	//			this.itemToSell = itemToSell;
//	//			this.itemInfo = itemInfo;
//	//			this.logsInfo = logsInfo;
//	//			this.emeraldsInfo = emeraldsInfo;
//	//		}
//	//
//	//		@Override
//	//		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
//	//			int itemAmount = this.itemInfo.getPrice(random);
//	//			int emeraldsAmount = this.emeraldsInfo.getPrice(random);
//	//			int logsAmount = this.logsInfo.getPrice(random);
//	//
//	//			ItemStack itemToSell = this.itemToSell.copy();
//	//			itemToSell.setCount(itemAmount);
//	//
//	//			int logMeta = random.nextInt(6);
//	//			Block log;
//	//			if (logMeta < 4) {
//	//				log = Blocks.LOG;
//	//			} else {
//	//				log = Blocks.LOG2;
//	//				logMeta -= 4;
//	//			}
//	//			ItemStack randomLog = new ItemStack(log, logsAmount, logMeta);
//	//
//	//			recipeList.add(new MerchantRecipe(randomLog, new ItemStack(Items.EMERALD, emeraldsAmount), itemToSell));
//	//		}
//	//	}
//	//
//	//	public static class GiveItemForTwoItems implements VillagerEntity.ITradeList {
//	//
//	//		public final ItemStack buyingItemStack;
//	//		@Nullable
//	//		public final VillagerEntity.PriceInfo buyingPriceInfo;
//	//
//	//		public final ItemStack buyingItemStackTwo;
//	//		@Nullable
//	//		public final VillagerEntity.PriceInfo buyingPriceItemTwoInfo;
//	//
//	//		public final ItemStack sellingItemstack;
//	//		@Nullable
//	//		public final VillagerEntity.PriceInfo sellingPriceInfo;
//	//
//	//		public GiveItemForTwoItems(
//	//			ItemStack buyingItemStack,
//	//			@Nullable VillagerEntity.PriceInfo buyingPriceInfo,
//	//			ItemStack buyingItemStackTwo,
//	//			@Nullable VillagerEntity.PriceInfo buyingPriceItemTwoInfo,
//	//			ItemStack sellingItemstack,
//	//			@Nullable VillagerEntity.PriceInfo sellingPriceInfo) {
//	//			this.buyingItemStack = buyingItemStack;
//	//			this.buyingPriceInfo = buyingPriceInfo;
//	//			this.buyingItemStackTwo = buyingItemStackTwo;
//	//			this.buyingPriceItemTwoInfo = buyingPriceItemTwoInfo;
//	//			this.sellingItemstack = sellingItemstack;
//	//			this.sellingPriceInfo = sellingPriceInfo;
//	//		}
//	//
//	//		@Override
//	//		public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
//	//			int buyAmount = 1;
//	//			if (this.buyingPriceInfo != null) {
//	//				buyAmount = this.buyingPriceInfo.getPrice(random);
//	//			}
//	//
//	//			int buyTwoAmount = 1;
//	//			if (this.buyingPriceItemTwoInfo != null) {
//	//				buyTwoAmount = this.buyingPriceItemTwoInfo.getPrice(random);
//	//			}
//	//
//	//			int sellAmount = 1;
//	//			if (this.sellingPriceInfo != null) {
//	//				sellAmount = this.sellingPriceInfo.getPrice(random);
//	//			}
//	//
//	//			ItemStack buyItemStack = this.buyingItemStack.copy();
//	//			buyItemStack.setCount(buyAmount);
//	//			ItemStack buyItemStackTwo = this.buyingItemStackTwo.copy();
//	//			buyItemStackTwo.setCount(buyTwoAmount);
//	//			ItemStack sellItemStack = this.sellingItemstack.copy();
//	//			sellItemStack.setCount(sellAmount);
//	//			recipeList.add(new MerchantRecipe(buyItemStack, buyItemStackTwo, sellItemStack));
//	//		}
//	//	}
//}
