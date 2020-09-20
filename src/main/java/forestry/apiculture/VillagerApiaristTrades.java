package forestry.apiculture;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;

import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.apiculture.items.ItemHoneyComb;

//TODO - trades once things are a little less obsfucated
public class VillagerApiaristTrades {

	public static class GiveRandomCombsForItems implements VillagerTrades.ITrade {
		private final int combCount;
		public final ItemStack itemToBuy;
		private final int maxUses;
		private final int xpValue;

		public GiveRandomCombsForItems(int combCount, ItemStack itemToBuy, int maxUses, int xpValue) {
			this.combCount = combCount;
			this.itemToBuy = itemToBuy;
			this.maxUses = maxUses;
			this.xpValue = xpValue;
		}

		@Nullable
		@Override
		public MerchantOffer getOffer(Entity trader, Random rand) {
			ItemStack itemToBuy = this.itemToBuy.copy();
			ItemStack randomComb = ItemHoneyComb.getRandomComb(combCount, rand, false);
			if (randomComb.isEmpty()) {
				return null;
			}
			return new MerchantOffer(randomComb, itemToBuy, maxUses, xpValue, 0.05F);
		}
	}

	public static class GiveRandomHiveDroneForItems implements VillagerTrades.ITrade {
		public final ItemStack buyingItemStack;
		public final ItemStack buyingItemStackTwo;
		private final int maxUses;
		private final int xpValue;
		private final float priceMultiplier;

		public GiveRandomHiveDroneForItems(
			ItemStack buyingItemStack,
			ItemStack buyingItemStackTwo,
			int maxUses, int xpValue, float priceMultiplier) {
			this.buyingItemStack = buyingItemStack;
			this.buyingItemStackTwo = buyingItemStackTwo;
			this.maxUses = maxUses;
			this.xpValue = xpValue;
			this.priceMultiplier = priceMultiplier;
		}

		@Nullable
		@Override
		public MerchantOffer getOffer(Entity trader, Random rand) {
			BeeDefinition[] forestryMundane = new BeeDefinition[]{BeeDefinition.FOREST, BeeDefinition.MEADOWS, BeeDefinition.MODEST, BeeDefinition.WINTRY, BeeDefinition.TROPICAL, BeeDefinition.MARSHY};
			ItemStack randomHiveDrone = forestryMundane[rand.nextInt(forestryMundane.length)].getMemberStack(EnumBeeType.DRONE);
			ItemStack buyItemStack = this.buyingItemStack.copy();
			ItemStack buyItemStackTwo = this.buyingItemStackTwo.copy();
			return new MerchantOffer(buyItemStack, buyItemStackTwo, randomHiveDrone, maxUses, xpValue, priceMultiplier);
		}
	}
}
