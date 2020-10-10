package forestry.api.registration;

import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class VillagerTrade {
    public static class GiveItemForEmeralds implements VillagerTrades.ITrade {
        public Item sellingItem;
        public PriceInterval sellingAmounts;
        public PriceInterval emeraldAmounts;
        final int maxUses;
        final int xp;

        public GiveItemForEmeralds(
                @Nonnull Item sellingItem,
                @Nonnull PriceInterval sellingAmounts,
                @Nonnull PriceInterval emeraldAmounts,
                int maxUses,
                int xp
        ) {
            this.sellingItem = sellingItem;
            this.sellingAmounts = sellingAmounts;
            this.emeraldAmounts = emeraldAmounts;
            this.maxUses = maxUses;
            this.xp = xp;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity trader, Random rand) {
            return new MerchantOffer(
                    new ItemStack(Items.EMERALD, this.emeraldAmounts.getPrice(rand)),
                    new ItemStack(this.sellingItem, this.sellingAmounts.getPrice(rand)),
                    maxUses,
                    xp,
                    0.05f
            );
        }
    }

    public static class GiveEmeraldForItem implements VillagerTrades.ITrade {
        public Item buyingItem;
        public PriceInterval buyingAmounts;
        public PriceInterval emeraldAmounts;
        final int maxUses;
        final int xp;

        public GiveEmeraldForItem(
                @Nonnull Item buyingItem,
                @Nonnull PriceInterval buyingAmounts,
                @Nonnull PriceInterval emeraldAmounts,
                int maxUses,
                int xp
        ) {
            this.buyingItem = buyingItem;
            this.buyingAmounts = buyingAmounts;
            this.emeraldAmounts = emeraldAmounts;
            this.maxUses = maxUses;
            this.xp = xp;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity trader, Random rand) {
            return new MerchantOffer(
                    new ItemStack(this.buyingItem, this.buyingAmounts.getPrice(rand)),
                    new ItemStack(Items.EMERALD, this.emeraldAmounts.getPrice(rand)),
                    maxUses,
                    xp,
                    0.05f
            );
        }
    }

    public static class GiveItemForItemAndEmerald implements VillagerTrades.ITrade {
        public Item buyingItem;
        public PriceInterval buyAmounts;
        public PriceInterval emeralsAmounts;
        public Item sellingItem;
        public PriceInterval sellingAmounts;
        final int maxUses;
        final int xp;

        public GiveItemForItemAndEmerald(
                @Nonnull Item buyingItem,
                @Nonnull PriceInterval buyAmounts,
                @Nonnull PriceInterval emeralsAmounts,
                @Nonnull Item sellingItem,
                @Nonnull PriceInterval sellingAmounts,
                int maxUses,
                int xp
        ) {
            this.buyingItem = buyingItem;
            this.buyAmounts = buyAmounts;
            this.emeralsAmounts = emeralsAmounts;
            this.sellingItem = sellingItem;
            this.sellingAmounts = sellingAmounts;
            this.maxUses = maxUses;
            this.xp = xp;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity trader, Random rand) {
            return new MerchantOffer(
                    new ItemStack(this.buyingItem, this.buyAmounts.getPrice(rand)),
                    new ItemStack(Items.EMERALD, this.emeralsAmounts.getPrice(rand)),
                    new ItemStack(this.sellingItem, this.sellingAmounts.getPrice(rand)),
                    maxUses,
                    xp,
                    0.05f
            );
        }
    }

    public static class GiveItemForLogAndEmerald implements VillagerTrades.ITrade {
        public PriceInterval buyAmounts;
        public PriceInterval emeraldAmounts;
        public Item sellingItem;
        public PriceInterval sellingAmounts;
        final int maxUses;
        final int xp;

        public GiveItemForLogAndEmerald(
                @Nonnull PriceInterval buyAmounts,
                @Nonnull PriceInterval emeraldAmounts,
                @Nonnull Item sellingItem,
                @Nonnull PriceInterval sellingAmounts,
                int maxUses,
                int xp
        ) {
            this.buyAmounts = buyAmounts;
            this.emeraldAmounts = emeraldAmounts;
            this.sellingItem = sellingItem;
            this.sellingAmounts = sellingAmounts;
            this.maxUses = maxUses;
            this.xp = xp;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity trader, Random rand) {
            Collection<Item> logsBlock = new HashSet<Item>();
            logsBlock.add(Items.ACACIA_LOG);
            logsBlock.add(Items.BIRCH_LOG);
            logsBlock.add(Items.DARK_OAK_LOG);
            logsBlock.add(Items.JUNGLE_LOG);
            logsBlock.add(Items.OAK_LOG);
            logsBlock.add(Items.SPRUCE_LOG);

            return new MerchantOffer(
                    new ItemStack(
                            logsBlock.stream()
                                     .skip((int) (logsBlock.size() * Math.random()))
                                     .findFirst().get(),
                            this.buyAmounts.getPrice(rand)
                    ),
                    new ItemStack(Items.EMERALD, this.emeraldAmounts.getPrice(rand)),
                    new ItemStack(this.sellingItem, this.sellingAmounts.getPrice(rand)),
                    maxUses,
                    xp,
                    0.05f
            );
        }
    }

    public static class GiveItemForTwoItems implements VillagerTrades.ITrade {
        public Item buyingItem;
        public PriceInterval buyAmounts;
        public Item buyingItem2;
        public PriceInterval buyAmounts2;
        public Item sellingItem;
        public PriceInterval sellingAmounts;
        final int maxUses;
        final int xp;

        public GiveItemForTwoItems(
                @Nonnull Item buyingItem,
                @Nonnull PriceInterval buyAmounts,
                @Nonnull Item buyingItem2,
                @Nonnull PriceInterval buyAmounts2,
                @Nonnull Item sellingItem,
                @Nonnull PriceInterval sellingAmounts,
                int maxUses,
                int xp
        ) {
            this.buyingItem = buyingItem;
            this.buyAmounts = buyAmounts;
            this.buyingItem2 = buyingItem2;
            this.buyAmounts2 = buyAmounts2;
            this.sellingItem = sellingItem;
            this.sellingAmounts = sellingAmounts;
            this.maxUses = maxUses;
            this.xp = xp;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity trader, Random rand) {
            return new MerchantOffer(
                    new ItemStack(this.buyingItem, this.buyAmounts.getPrice(rand)),
                    new ItemStack(this.buyingItem2, this.buyAmounts2.getPrice(rand)),
                    new ItemStack(this.sellingItem, this.sellingAmounts.getPrice(rand)),
                    maxUses,
                    xp,
                    0.05f
            );
        }
    }

    public static class PriceInterval {
        private final int min;
        private final int max;

        public PriceInterval(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getPrice(Random rand) {
            return min >= max ? min : min + rand.nextInt(max - min + 1);
        }
    }
}
