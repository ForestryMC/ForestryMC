/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.lepidopterology;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.Feature;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import forestry.Forestry;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.products.Product;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IAlleleButterflyCocoon;
import forestry.api.modules.ForestryModule;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.config.forge_old.Property;
import forestry.core.utils.ForgeUtils;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.Translator;
import forestry.lepidopterology.commands.CommandButterfly;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.features.LepidopterologyFeatures;
import forestry.lepidopterology.genetics.ButterflyDefinition;
import forestry.lepidopterology.genetics.ButterflyFactory;
import forestry.lepidopterology.genetics.ButterflyMutationFactory;
import forestry.lepidopterology.genetics.MothDefinition;
import forestry.lepidopterology.genetics.alleles.ButterflyAlleles;
import forestry.lepidopterology.proxy.ProxyLepidopterology;
import forestry.lepidopterology.proxy.ProxyLepidopterologyClient;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ISidedModuleHandler;
import forestry.modules.ModuleHelper;

import genetics.api.alleles.IAllele;
import genetics.utils.AlleleUtils;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.LEPIDOPTEROLOGY, name = "Lepidopterology", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.lepidopterology.description")
public class ModuleLepidopterology extends BlankForestryModule {

	@SuppressWarnings("NullableProblems")
	public static ProxyLepidopterology proxy;
	private static final String CONFIG_CATEGORY = "lepidopterology";
	public static int spawnConstraint = 100;
	public static int entityConstraint = 1000;
	public static int maxDistance = 64;
	private static boolean allowPollination = true;
	public static final Map<String, Float> spawnRaritys = Maps.newHashMap();
	private static boolean spawnButterflysFromLeaves = true;
	private static boolean generateCocoons = false;
	private static float generateCocoonsAmount = 1.0f;
	private static float serumChance = 0.55f;
	private static float secondSerumChance = 0;

	public ModuleLepidopterology() {
		proxy = DistExecutor.safeRunForDist(() -> ProxyLepidopterologyClient::new, () -> ProxyLepidopterology::new);
		ForgeUtils.registerSubscriber(this);

		MinecraftForge.EVENT_BUS.register(ForgeEvents.class);

		if (generateCocoons) {
			if (generateCocoonsAmount > 0.0) {
				IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
				modEventBus.addGenericListener(Feature.class, LepidopterologyFeatures::registerFeatures);
				MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, LepidopterologyFeatures::onBiomeLoad);
			}
		}
	}

	@Override
	public void setupAPI() {
		ButterflyManager.butterflyFactory = new ButterflyFactory();
		ButterflyManager.butterflyMutationFactory = new ButterflyMutationFactory();
	}

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);

		ButterflyDefinition.preInit();
		MothDefinition.preInit();

		proxy.preInitializeRendering();

		if (ModuleHelper.isEnabled(ForestryModuleUids.SORTING)) {
			LepidopterologyFilterRule.init();
			LepidopterologyFilterRuleType.init();
		}
	}

	@Override
	public Set<ResourceLocation> getDependencyUids() {
		Set<ResourceLocation> dependencyUids = new HashSet<>();
		dependencyUids.add(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.CORE));
		dependencyUids.add(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.ARBORICULTURE));
		return dependencyUids;
	}

	@Override
	public void doInit() {
		ModuleCore.rootCommand.then(CommandButterfly.register());

		MothDefinition.initMoths();
		ButterflyDefinition.initButterflies();
		ButterflyAlleles.createLoot();

		if (spawnButterflysFromLeaves) {
			TreeManager.treeRoot.registerLeafTickHandler(new ButterflySpawner());
		}

		//TODO recipes
		//		RecipeSorter.register("forestry:lepidopterologymating", MatingRecipe.class, RecipeSorter.Category.SHAPELESS,
		//				"before:minecraft:shapeless");
	}

	@Override
	public void postInit() {
		File configFile = new File(Forestry.instance.getConfigFolder(), CONFIG_CATEGORY + ".cfg");
		loadConfig(configFile);
	}

	private static void loadConfig(File configFile) {
		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.1.0");

		spawnConstraint = config.getIntLocalized("butterfly.entities", "spawn.limit", spawnConstraint, 0, 500);
		entityConstraint = config.getIntLocalized("butterfly.entities", "maximum", entityConstraint, 0, 5000);
		maxDistance = config.getIntLocalized("butterfly.entities", "maxDistance", maxDistance, 0, 256);
		allowPollination = config.getBooleanLocalized("butterfly.entities", "pollination", allowPollination);
		spawnButterflysFromLeaves = config.getBooleanLocalized("butterfly.entities", "spawn.leaves",
			spawnButterflysFromLeaves);

		generateCocoons = config.getBooleanLocalized("butterfly.cocoons", "generate", generateCocoons);
		generateCocoonsAmount = config.getFloatLocalized("butterfly.cocoons", "generate.amount", generateCocoonsAmount,
			0.0f, 10.0f);

		serumChance = config.getFloatLocalized("butterfly.cocoons", "serum", serumChance, 0.0f, 100.0f);
		secondSerumChance = config.getFloatLocalized("butterfly.cocoons", "second.serum", secondSerumChance, 0.0f,
			100.0f);

		parseRarity(config);
		parseCooconLoots(config);

		config.save();
	}

	private static void parseRarity(LocalizedConfiguration config) {
		List<String> butterflyRarity = Lists.newArrayList();
		AlleleUtils.forEach(ButterflyChromosomes.SPECIES, (species) -> {
			String identifier = species.getRegistryName().toString().replace(':', '_');
			butterflyRarity.add(identifier + ":" + species.getRarity());
		});
		Collections.sort(butterflyRarity);
		String[] defaultRaritys = butterflyRarity.toArray(new String[0]);

		Property rarityConf = config.get("butterfly.alleles", "rarity", defaultRaritys);
		rarityConf.setComment(Translator.translateToLocal("for.config.butterfly.alleles.rarity"));

		String[] configRaritys = rarityConf.getStringList();
		for (String rarity : configRaritys) {
			if (rarity.contains(":") && rarity.length() > 3) {
				String[] raritys = rarity.split(":");
				try {
					spawnRaritys.put(raritys[0], Float.parseFloat(raritys[1]));
				} catch (Exception e) {
					Log.error("Failed to parse spawn rarity for butterfly. {}", rarity, e);
				}
			}
		}
	}

	private static void parseCooconLoots(LocalizedConfiguration config) {
		for (IAllele allele : AlleleUtils.filteredAlleles(ButterflyChromosomes.COCOON)) {
			if (allele instanceof IAlleleButterflyCocoon) {
				parseCooconLoot(config, (IAlleleButterflyCocoon) allele);
			}
		}
	}

	private static void parseCooconLoot(LocalizedConfiguration config, IAlleleButterflyCocoon cocoon) {
		Map<ItemStack, Float> cooconLoot = new HashMap<>();
		List<String> lootList = new ArrayList<>();
		for (Product product : cocoon.getCocoonLoot().getPossibleProducts()) {
			lootList.add(product.getItem().getItem().getRegistryName() + ";" + product.getChance());
		}
		Collections.sort(lootList);
		String[] defaultLoot = lootList.toArray(new String[0]);

		Property lootConf = config.get("butterfly.cocoons.alleles.loot", cocoon.getRegistryName().toString(), defaultLoot);
		lootConf.setComment(Translator.translateToLocal("for.config.butterfly.alleles.loot"));

		String[] configLoot = lootConf.getStringList();
		for (String loot : configLoot) {
			if (loot.contains(";") && loot.length() > 3) {
				String[] loots = loot.split(";");
				try {
					ItemStack itemStack = null; //TODO tags, flatten ItemStackUtil.parseItemStackString(loots[0], OreDictionary.WILDCARD_VALUE);
					if (itemStack != null) {
						cooconLoot.put(itemStack, Float.parseFloat(loots[1]));
					}
				} catch (Exception e) {
					Log.error("Failed to parse cocoon loot. {}", loot, e);
				}
			}
		}
		cocoon.clearLoot();
		for (Entry<ItemStack, Float> entry : cooconLoot.entrySet()) {
			cocoon.addLoot(entry.getKey(), entry.getValue());
		}
		cocoon.bakeLoot();
	}

	@Override
	public void registerRecipes() {
		//		ForgeRegistries.RECIPES.register(new MatingRecipe());    //TODO - JSON this?
	}
	//
	//	@Override
	//	public void getHiddenItems(List<ItemStack> hiddenItems) {
	//		// cocoon itemBlock is different from the normal item
	//		hiddenItems.add(new ItemStack(blocks.cocoon));
	//		hiddenItems.add(new ItemStack(blocks.solidCocoon));
	//	}

	public static boolean isPollinationAllowed() {
		return allowPollination;
	}

	public static boolean isSpawnButterflysFromLeaves() {
		return spawnButterflysFromLeaves;
	}

	public static boolean isGenerateCocoons() {
		return generateCocoons;
	}

	public static float getGenerateCocoonsAmount() {
		return generateCocoonsAmount;
	}

	public static float getSerumChance() {
		return serumChance;
	}

	public static float getSecondSerumChance() {
		return secondSerumChance;
	}

	@Override
	public ISidedModuleHandler getModuleHandler() {
		return proxy;
	}

	private class ForgeEvents {
		@SubscribeEvent
		public void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
			if (event.getEntity() instanceof EntityButterfly) {
				event.setCanceled(true);
			}
		}
	}
}
