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

import com.google.common.collect.Maps;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import forestry.api.arboriculture.TreeManager;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.modules.ForestryModule;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;
import forestry.core.utils.ForgeUtils;
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

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.LEPIDOPTEROLOGY, name = "Lepidopterology", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.lepidopterology.description")
public class ModuleLepidopterology extends BlankForestryModule {

	@SuppressWarnings("NullableProblems")
	public static ProxyLepidopterology proxy;
	private static final String CONFIG_CATEGORY = "lepidopterology";
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

		LepidopterologyFilterRule.init();
		LepidopterologyFilterRuleType.init();
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
