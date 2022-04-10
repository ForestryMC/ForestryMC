package forestry.apiculture.villagers;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.village.PointOfInterestType;

import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.apiculture.blocks.BlockTypeApiculture;
import forestry.apiculture.features.ApicultureBlocks;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.apiculture.items.EnumPropolis;
import forestry.apiculture.items.ItemHoneyComb;
import forestry.core.config.Constants;
import forestry.core.registration.RegisterVillagerPointOfInterest;
import forestry.core.registration.RegisterVillagerProfession;
import forestry.core.registration.VillagerTrade;

public class RegisterVillager {
	public static final ResourceLocation BEEKEEPER = new ResourceLocation(Constants.MOD_ID, "beekeeper");

	public RegisterVillager() {

	}

	@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Registers {
		public static final DeferredRegister<PointOfInterestType> POINTS_OF_INTEREST = DeferredRegister.create(ForgeRegistries.POI_TYPES, Constants.MOD_ID);
		public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, Constants.MOD_ID);

		// public static final RegistryObject<PointOfInterestType> POI_APIARY = POINTS_OF_INTEREST.register("apiary", () -> RegisterVillagerPointOfInterest.create("apiary", RegisterVillagerPointOfInterest.assembleStates(ApicultureBlocks.BASE.get(BlockTypeApiculture.APIARY).getBlock())));
		// public static final RegistryObject<VillagerProfession> PROF_BEEKEEPER = PROFESSIONS.register(BEEKEEPER.getPath(), () -> RegisterVillagerProfession.create(BEEKEEPER, POI_APIARY.get(), SoundEvents.VILLAGER_WORK_FISHERMAN));
	}

	@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class Events {
		@SubscribeEvent
		public void villagerTrades(VillagerTradesEvent event) {
			if (BEEKEEPER.equals(event.getType().getRegistryName())) {
				event.getTrades().get(1).add(new GiveHoneyCombForItem(ApicultureItems.BEE_COMBS.getItems(), Items.WHEAT, new VillagerTrade.PriceInterval(2, 4), new VillagerTrade.PriceInterval(8, 12), 8, 2, 0F));
				event.getTrades().get(1).add(new GiveHoneyCombForItem(ApicultureItems.BEE_COMBS.getItems(), Items.CARROT, new VillagerTrade.PriceInterval(2, 4), new VillagerTrade.PriceInterval(8, 12), 8, 2, 0F));
				event.getTrades().get(1).add(new GiveHoneyCombForItem(ApicultureItems.BEE_COMBS.getItems(), Items.POTATO, new VillagerTrade.PriceInterval(2, 4), new VillagerTrade.PriceInterval(8, 12), 8, 2, 0F));

				event.getTrades().get(2).add(new VillagerTrade.GiveItemForEmeralds(ApicultureItems.SMOKER.getItem(), new VillagerTrade.PriceInterval(1, 1), new VillagerTrade.PriceInterval(1, 4), 8, 6));
				event.getTrades().get(2).add(new GiveDroneForItems(ApicultureItems.PROPOLIS.stack(EnumPropolis.NORMAL).getItem(), new VillagerTrade.PriceInterval(2, 4), new VillagerTrade.PriceInterval(1, 1), 8, 6, 0F));

				event.getTrades().get(3).add(new VillagerTrade.GiveEmeraldForItem(ApicultureItems.BEE_PRINCESS.getItem(), new VillagerTrade.PriceInterval(1, 1), new VillagerTrade.PriceInterval(1, 1), 8, 10));
				event.getTrades().get(3).add(new VillagerTrade.GiveItemForEmeralds(ApicultureItems.FRAME_PROVEN.getItem(), new VillagerTrade.PriceInterval(1, 2), new VillagerTrade.PriceInterval(1, 6), 8, 10));
				event.getTrades().get(3).add(new VillagerTrade.GiveItemForLogAndEmerald(new VillagerTrade.PriceInterval(32, 64), new VillagerTrade.PriceInterval(16, 32), ApicultureBlocks.BASE.get(BlockTypeApiculture.APIARY).stack().getItem(), new VillagerTrade.PriceInterval(1, 1), 8, 10));

				event.getTrades().get(4).add(new VillagerTrade.GiveItemForItemAndEmerald(ApicultureItems.BEE_PRINCESS.getItem(), new VillagerTrade.PriceInterval(1, 1), new VillagerTrade.PriceInterval(10, 64), BeeDefinition.MONASTIC.getMemberStack(EnumBeeType.DRONE).getItem(), new VillagerTrade.PriceInterval(1, 1), 8, 15));
				event.getTrades().get(4).add(new VillagerTrade.GiveItemForTwoItems(ApicultureItems.BEE_DRONE.getItem(), new VillagerTrade.PriceInterval(1, 1), Items.ENDER_EYE, new VillagerTrade.PriceInterval(12, 16), BeeDefinition.ENDED.getMemberStack(EnumBeeType.DRONE).getItem(), new VillagerTrade.PriceInterval(1, 1), 8, 15));
			}
		}

		private static class GiveHoneyCombForItem implements VillagerTrades.ITrade {
			final int maxUses;
			final int xp;
			final float priceMult;
			public Collection<ItemHoneyComb> itemHoneyCombs;
			public Item buying;
			public VillagerTrade.PriceInterval sellingPriceInfo;
			public VillagerTrade.PriceInterval buyingPriceInfo;

			public GiveHoneyCombForItem(Collection<ItemHoneyComb> selling, Item buying, VillagerTrade.PriceInterval sellingPriceInfo, VillagerTrade.PriceInterval buyingPriceInfo, int maxUses, int xp, float priceMult) {
				this.itemHoneyCombs = selling;
				this.buying = buying;
				this.sellingPriceInfo = sellingPriceInfo;
				this.buyingPriceInfo = buyingPriceInfo;
				this.maxUses = maxUses;
				this.xp = xp;
				this.priceMult = priceMult;
			}

			@Nullable
			@Override
			public MerchantOffer getOffer(Entity trader, Random rand) {
				return new MerchantOffer(new ItemStack(buying, buyingPriceInfo.getPrice(rand)), new ItemStack(itemHoneyCombs.stream().skip((int) (itemHoneyCombs.size() * Math.random())).findFirst().get(), sellingPriceInfo.getPrice(rand)), maxUses, xp, priceMult);
			}
		}

		private static class GiveDroneForItems implements VillagerTrades.ITrade {
			final int maxUses;
			final int xp;
			final float priceMult;
			public Item buying;
			public VillagerTrade.PriceInterval buyingPriceInfo;
			public VillagerTrade.PriceInterval sellingPriceInfo;

			public GiveDroneForItems(Item buying, VillagerTrade.PriceInterval buyingPriceInfo, VillagerTrade.PriceInterval sellingPriceInfo, int maxUses, int xp, float priceMult) {
				this.buying = buying;
				this.buyingPriceInfo = buyingPriceInfo;
				this.sellingPriceInfo = sellingPriceInfo;
				this.maxUses = maxUses;
				this.xp = xp;
				this.priceMult = priceMult;
			}

			@Nullable
			@Override
			public MerchantOffer getOffer(Entity trader, Random rand) {
				BeeDefinition[] forestryMundane = new BeeDefinition[]{BeeDefinition.FOREST, BeeDefinition.MEADOWS, BeeDefinition.MODEST, BeeDefinition.WINTRY, BeeDefinition.TROPICAL, BeeDefinition.MARSHY};
				ItemStack randomHiveDrone = forestryMundane[rand.nextInt(forestryMundane.length)].getMemberStack(EnumBeeType.DRONE);
				randomHiveDrone.setCount(sellingPriceInfo.getPrice(rand));

				return new MerchantOffer(new ItemStack(buying, buyingPriceInfo.getPrice(rand)), randomHiveDrone, maxUses, xp, priceMult);
			}
		}
	}
}
