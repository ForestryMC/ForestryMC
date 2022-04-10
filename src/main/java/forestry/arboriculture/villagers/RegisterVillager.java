package forestry.arboriculture.villagers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
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

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.AlleleForestrySpecies;
import forestry.core.registration.RegisterVillagerPointOfInterest;
import forestry.core.registration.RegisterVillagerProfession;
import forestry.core.registration.VillagerTrade;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IChromosomeType;
import genetics.api.organism.IOrganismType;
import genetics.utils.AlleleUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public class RegisterVillager {
	public static final ResourceLocation ARBORIST = new ResourceLocation(Constants.MOD_ID, "arborist");

	@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Registers {
		public static final DeferredRegister<PointOfInterestType> POINTS_OF_INTEREST = DeferredRegister.create(ForgeRegistries.POI_TYPES, Constants.MOD_ID);
		public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, Constants.MOD_ID);

		// public static final RegistryObject<PointOfInterestType> POI_TREE_CHEST = POINTS_OF_INTEREST.register("tree_chest", () -> RegisterVillagerPointOfInterest.create("tree_chest", RegisterVillagerPointOfInterest.assembleStates(ArboricultureBlocks.TREE_CHEST.getBlock())));
		// public static final RegistryObject<VillagerProfession> PROF_BEEKEEPER = PROFESSIONS.register(ARBORIST.getPath(), () -> RegisterVillagerProfession.create(ARBORIST, POI_TREE_CHEST.get(), SoundEvents.VILLAGER_WORK_FISHERMAN));
	}

	@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class Events {
		@SubscribeEvent
		public void villagerTrades(VillagerTradesEvent event) {
			Int2ObjectMap<List<VillagerTrades.ITrade>> trades = event.getTrades();
			if (ARBORIST.equals(event.getType().getRegistryName())) {
				event.getTrades().get(1).add(new GivePlanksForEmeralds(new VillagerTrade.PriceInterval(1, 4), new VillagerTrade.PriceInterval(10, 32), 8, 2, 0F));
				event.getTrades().get(1).add(new GivePollenForEmeralds(new VillagerTrade.PriceInterval(1, 1), new VillagerTrade.PriceInterval(1, 3), EnumGermlingType.SAPLING, 4, 8, 2, 0F));

				event.getTrades().get(2).add(new GivePlanksForEmeralds(new VillagerTrade.PriceInterval(1, 4), new VillagerTrade.PriceInterval(10, 32), 8, 6, 0F));
				event.getTrades().get(2).add(new GivePollenForEmeralds(new VillagerTrade.PriceInterval(2, 3), new VillagerTrade.PriceInterval(1, 1), EnumGermlingType.POLLEN, 6, 8, 6, 0F));
				event.getTrades().get(2).add(new VillagerTrade.GiveItemForEmeralds(ArboricultureItems.GRAFTER_PROVEN.getItem(), new VillagerTrade.PriceInterval(1, 1), new VillagerTrade.PriceInterval(1, 4), 8, 6));

				event.getTrades().get(3).add(new GiveLogsForEmeralds(new VillagerTrade.PriceInterval(2, 5), new VillagerTrade.PriceInterval(6, 18), 8, 2, 0F));

				event.getTrades().get(3).add(new GiveLogsForEmeralds(new VillagerTrade.PriceInterval(2, 5), new VillagerTrade.PriceInterval(6, 18), 8, 2, 0F));

				event.getTrades().get(4).add(new GivePollenForEmeralds(new VillagerTrade.PriceInterval(5, 20), new VillagerTrade.PriceInterval(1, 1), EnumGermlingType.POLLEN, 10, 8, 15, 0F));
				event.getTrades().get(4).add(new GivePollenForEmeralds(new VillagerTrade.PriceInterval(5, 20), new VillagerTrade.PriceInterval(1, 1), EnumGermlingType.SAPLING, 10, 8, 15, 0F));
			}
		}
	}

	private static class GivePlanksForEmeralds implements VillagerTrades.ITrade {
		final VillagerTrade.PriceInterval emeraldsPriceInfo;
		final VillagerTrade.PriceInterval sellingPriceInfo;
		final int maxUses;
		final int xp;
		final float priceMult;

		public GivePlanksForEmeralds(VillagerTrade.PriceInterval emeraldsPriceInfo, VillagerTrade.PriceInterval sellingPriceInfo, int maxUses, int xp, float priceMult) {
			this.emeraldsPriceInfo = emeraldsPriceInfo;
			this.sellingPriceInfo = sellingPriceInfo;
			this.maxUses = maxUses;
			this.xp = xp;
			this.priceMult = priceMult;
		}

		@Nullable
		@Override
		public MerchantOffer getOffer(Entity trader, Random rand) {
			EnumForestryWoodType woodType = EnumForestryWoodType.getRandom(rand);
			ItemStack sellStack = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.PLANKS, false);
			sellStack.setCount(sellingPriceInfo.getPrice(rand));

			return new MerchantOffer(new ItemStack(Items.EMERALD, emeraldsPriceInfo.getPrice(rand)), sellStack, maxUses, xp, priceMult);
		}
	}

	private static class GiveLogsForEmeralds implements VillagerTrades.ITrade {
		final VillagerTrade.PriceInterval emeraldsPriceInfo;
		final VillagerTrade.PriceInterval sellingPriceInfo;
		final int maxUses;
		final int xp;
		final float priceMult;

		public GiveLogsForEmeralds(VillagerTrade.PriceInterval emeraldsPriceInfo, VillagerTrade.PriceInterval sellingPriceInfo, int maxUses, int xp, float priceMult) {
			this.emeraldsPriceInfo = emeraldsPriceInfo;
			this.sellingPriceInfo = sellingPriceInfo;
			this.maxUses = maxUses;
			this.xp = xp;
			this.priceMult = priceMult;
		}

		@Nullable
		@Override
		public MerchantOffer getOffer(Entity trader, Random rand) {
			EnumForestryWoodType woodType = EnumForestryWoodType.getRandom(rand);
			ItemStack sellStack = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.LOG, false);
			sellStack.setCount(sellingPriceInfo.getPrice(rand));

			return new MerchantOffer(new ItemStack(Items.EMERALD, emeraldsPriceInfo.getPrice(rand)), sellStack, maxUses, xp, priceMult);
		}
	}

	private static class GivePollenForEmeralds implements VillagerTrades.ITrade {
		final VillagerTrade.PriceInterval buyingPriceInfo;
		final VillagerTrade.PriceInterval sellingPriceInfo;
		final IOrganismType type;
		final int maxComplexity;
		final int maxUses;
		final int xp;
		final float priceMult;

		public GivePollenForEmeralds(VillagerTrade.PriceInterval buyingPriceInfo, VillagerTrade.PriceInterval sellingPriceInfo, IOrganismType type, int maxComplexity, int maxUses, int xp, float priceMult) {
			this.buyingPriceInfo = buyingPriceInfo;
			this.sellingPriceInfo = sellingPriceInfo;
			this.type = type;
			this.maxComplexity = maxComplexity;
			this.maxUses = maxUses;
			this.xp = xp;
			this.priceMult = priceMult;
		}

		@Nullable
		@Override
		public MerchantOffer getOffer(Entity trader, Random rand) {
			IChromosomeType treeSpeciesType = TreeChromosomes.SPECIES;
			Collection<IAllele> registeredSpecies = AlleleUtils.getAllelesByType(treeSpeciesType);
			List<IAlleleSpecies> potentialSpecies = new ArrayList<>();
			for (IAllele allele : registeredSpecies) {
				if (allele instanceof AlleleForestrySpecies) {
					AlleleForestrySpecies species = (AlleleForestrySpecies) allele;
					if (species.getComplexity() <= maxComplexity) {
						potentialSpecies.add(species);
					}
				}
			}

			if (potentialSpecies.isEmpty()) {
				return null;
			}

			IAlleleSpecies chosenSpecies = potentialSpecies.get(rand.nextInt(potentialSpecies.size()));
			IAllele[] template = TreeManager.treeRoot.getTemplate(chosenSpecies.getRegistryName().toString());
			ITree individual = TreeManager.treeRoot.templateAsIndividual(template);

			ItemStack sellStack = TreeManager.treeRoot.createStack(individual, type);
			sellStack.setCount(sellingPriceInfo.getPrice(rand));

			return new MerchantOffer(new ItemStack(Items.EMERALD, buyingPriceInfo.getPrice(rand)), sellStack, maxUses, xp, priceMult);
		}
	}
}
