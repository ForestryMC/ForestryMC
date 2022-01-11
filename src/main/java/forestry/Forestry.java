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

package forestry;

import java.io.File;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import forestry.api.climate.ClimateManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.ISetupListener;
import forestry.api.core.ISpriteRegistry;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IHygroregulatorRecipe;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.api.recipes.ISolderRecipe;
import forestry.api.recipes.ISqueezerContainerRecipe;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.IStillRecipe;
import forestry.arboriculture.loot.CountBlockFunction;
import forestry.arboriculture.loot.GrafterLootModifier;
import forestry.core.EventHandlerCore;
import forestry.core.circuits.CircuitRecipe;
import forestry.core.climate.ClimateFactory;
import forestry.core.climate.ClimateRoot;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.data.ForestryBackpackTagProvider;
import forestry.core.data.ForestryBlockModelProvider;
import forestry.core.data.ForestryBlockStateProvider;
import forestry.core.data.ForestryBlockTagsProvider;
import forestry.core.data.ForestryFluidTagsProvider;
import forestry.core.data.ForestryItemModelProvider;
import forestry.core.data.ForestryItemTagsProvider;
import forestry.core.data.ForestryLootModifierProvider;
import forestry.core.data.ForestryLootTableProvider;
import forestry.core.data.ForestryMachineRecipeProvider;
import forestry.core.data.ForestryRecipeProvider;
import forestry.core.data.WoodBlockModelProvider;
import forestry.core.data.WoodBlockStateProvider;
import forestry.core.data.WoodItemModelProvider;
import forestry.core.errors.EnumErrorCode;
import forestry.core.errors.ErrorStateRegistry;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.loot.ConditionLootModifier;
import forestry.core.loot.OrganismFunction;
import forestry.core.models.ModelBlockCached;
import forestry.core.network.NetworkHandler;
import forestry.core.network.PacketHandlerServer;
import forestry.core.proxy.Proxies;
import forestry.core.proxy.ProxyClient;
import forestry.core.proxy.ProxyCommon;
import forestry.core.proxy.ProxyRender;
import forestry.core.proxy.ProxyRenderClient;
import forestry.core.recipes.FallbackIngredient;
import forestry.core.recipes.HygroregulatorRecipe;
import forestry.core.recipes.ModuleEnabledCondition;
import forestry.core.render.ColourProperties;
import forestry.core.render.ForestrySpriteUploader;
import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.ForgeUtils;
import forestry.core.worldgen.VillagerJigsaw;
import forestry.factory.recipes.CarpenterRecipe;
import forestry.factory.recipes.CentrifugeRecipe;
import forestry.factory.recipes.FabricatorRecipe;
import forestry.factory.recipes.FabricatorSmeltingRecipe;
import forestry.factory.recipes.FermenterRecipe;
import forestry.factory.recipes.MoistenerRecipe;
import forestry.factory.recipes.SqueezerContainerRecipe;
import forestry.factory.recipes.SqueezerRecipe;
import forestry.factory.recipes.StillRecipe;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ForestryModules;
import forestry.modules.ModuleManager;
import forestry.modules.features.ModFeatureRegistry;
import genetics.api.alleles.IAllele;
import genetics.utils.AlleleUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.data.DataGenerator;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

/**
 * Forestry Minecraft Mod
 *
 * @author SirSengir
 */
//@Mod(
//	modid = Constants.MOD_ID,
//	name = Constants.MOD_NAME,
//	version = Constants.VERSION,
//	guiFactory = "forestry.core.config.ForestryGuiConfigFactory",
//	acceptedMinecraftVersions = "[1.12.2,1.13.0)",
//	dependencies = "required-after:forge@[14.23.4.2749,);"
//		+ "after:jei@[4.12.0.0,);"
//		+ "after:" + PluginIC2.MOD_ID + ";"
//		+ "after:" + PluginNatura.MOD_ID + ";"
//		+ "after:toughasnails;"
//		+ "after:" + PluginTechReborn.MOD_ID + ";"
//		+ "after:" + PluginBuildCraftFuels.MOD_ID + ";"
//		+ "before:binniecore@[2.5.1.184,)"
//the big TODO - things have to be properly sided now, can't keep just using OnlyIn I think
@Mod("forestry")
public class Forestry {

	@SuppressWarnings("NullableProblems")
	public static Forestry instance;

	private static final Logger LOGGER = LogManager.getLogger();

	@Nullable
	private File configFolder;

	public Forestry() {
		instance = this;
		ForestryAPI.instance = this;
		ForestryAPI.forestryConstants = new Constants();
		ForestryAPI.errorStateRegistry = new ErrorStateRegistry();
		ClimateManager.climateRoot = ClimateRoot.getInstance();
		ClimateManager.climateFactory = ClimateFactory.INSTANCE;
		ClimateManager.stateHelper = ClimateStateHelper.INSTANCE;
		EnumErrorCode.init();

		//TODO not sure where this is enabled any more
		//		FluidRegistry.enableUniversalBucket();
		ModuleManager moduleManager = ModuleManager.getInstance();
		ForestryAPI.moduleManager = moduleManager;
		moduleManager.registerContainers(new ForestryModules());//TODO compat, new ForestryCompatPlugins());
		ModuleManager.runSetup();
		NetworkHandler networkHandler = new NetworkHandler();
		//				DistExecutor.runForDist(()->()-> networkHandler.clientPacketHandler(), ()->()-> networkHandler.serverPacketHandler());
		IEventBus modEventBus = ForgeUtils.modBus();
		modEventBus.addListener(this::setup);
		//		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		modEventBus.addListener(this::processIMCMessages);
		modEventBus.addListener(this::clientStuff);
		modEventBus.addListener(this::gatherData);
		EventHandlerCore eventHandlerCore = new EventHandlerCore();
		MinecraftForge.EVENT_BUS.register(eventHandlerCore);
		MinecraftForge.EVENT_BUS.register(this);
		Proxies.render = DistExecutor.runForDist(() -> ProxyRenderClient::new, () -> ProxyRender::new);
		Proxies.common = DistExecutor.runForDist(() -> ProxyClient::new, () -> ProxyCommon::new);

		ModuleManager.getModuleHandler().runSetup();
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> clientInit(modEventBus, networkHandler));
		modEventBus.addListener(EventPriority.NORMAL, false, FMLCommonSetupEvent.class, evt -> networkHandler.serverPacketHandler());
	}

	public void clientStuff(FMLClientSetupEvent e) {
		ModuleManager.getModuleHandler().registerGuiFactories();

		for (ModFeatureRegistry value : ModFeatureRegistry.getRegistries().values()) {
			value.clientSetup();
		}
	}

	@Nullable
	private static PacketHandlerServer packetHandler;

	public static PacketHandlerServer getPacketHandler() {
		Preconditions.checkNotNull(packetHandler);
		return packetHandler;
	}

	private void setup(FMLCommonSetupEvent event) {
		// Forestry's villager houses
		event.enqueueWork(VillagerJigsaw::init);

		packetHandler = new PacketHandlerServer();

		// Register event handler
		configFolder = new File("./config/forestry"); //new File(event.getModConfigurationDirectory(), Constants.MOD_ID);
		//TODO - config
		Config.load(Dist.DEDICATED_SERVER);
		String gameMode = Config.gameMode;
		Preconditions.checkNotNull(gameMode);

		//TODO - DistExecutor
		callSetupListeners(true);
		ModuleManager.getModuleHandler().runPreInit();
		Proxies.render.registerItemAndBlockColors();
		//TODO put these here for now
		ModuleManager.getModuleHandler().runInit();
		callSetupListeners(false);
		ModuleManager.getModuleHandler().runPostInit();
	}

	//TODO: Move to somewhere else
	private void callSetupListeners(boolean start) {
		for (IAllele allele : AlleleUtils.getAlleles()) {
			if (allele instanceof ISetupListener) {
				ISetupListener listener = (ISetupListener) allele;
				if (start) {
					listener.onStartSetup();
				} else {
					listener.onFinishSetup();
				}
			}
		}
	}

	private void gatherData(GatherDataEvent event) {
		CapabilityFluidHandler.register();
		DataGenerator generator = event.getGenerator();

		if (event.includeServer()) {
			ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
			ForestryBlockTagsProvider blockTagsProvider = new ForestryBlockTagsProvider(generator, existingFileHelper);
			generator.addProvider(blockTagsProvider);
			generator.addProvider(new ForestryItemTagsProvider(generator, blockTagsProvider, existingFileHelper));
			generator.addProvider(new ForestryBackpackTagProvider(generator, blockTagsProvider, existingFileHelper));
			generator.addProvider(new ForestryFluidTagsProvider(generator, existingFileHelper));
			generator.addProvider(new ForestryLootTableProvider(generator));
			generator.addProvider(new WoodBlockStateProvider(generator));
			generator.addProvider(new WoodBlockModelProvider(generator));
			generator.addProvider(new WoodItemModelProvider(generator));
			generator.addProvider(new ForestryBlockStateProvider(generator));
			generator.addProvider(new ForestryBlockModelProvider(generator));
			generator.addProvider(new ForestryItemModelProvider(generator));
			generator.addProvider(new ForestryRecipeProvider(generator));
			generator.addProvider(new ForestryMachineRecipeProvider(generator));
			generator.addProvider(new ForestryLootModifierProvider(generator));
		}
	}

	private void clientInit(IEventBus modEventBus, NetworkHandler networkHandler) {
		modEventBus.addListener(EventPriority.NORMAL, false, ColorHandlerEvent.Block.class, x -> {
			Minecraft minecraft = Minecraft.getInstance();
			ForestrySpriteUploader spriteUploader = new ForestrySpriteUploader(minecraft.textureManager, TextureManagerForestry.LOCATION_FORESTRY_TEXTURE, "gui");
			TextureManagerForestry.getInstance().init(spriteUploader);
			IResourceManager resourceManager = minecraft.getResourceManager();
			if (resourceManager instanceof IReloadableResourceManager) {
				IReloadableResourceManager reloadableManager = (IReloadableResourceManager) resourceManager;
				reloadableManager.registerReloadListener(ColourProperties.INSTANCE);
				reloadableManager.registerReloadListener(GuiElementFactory.INSTANCE);
				reloadableManager.registerReloadListener(spriteUploader);
			}
			//EntriesCategory.registerSearchTree();
			ModuleManager.getModuleHandler().runClientInit();

		});
		modEventBus.addListener(EventPriority.NORMAL, false, FMLLoadCompleteEvent.class, fmlLoadCompleteEvent -> networkHandler.clientPacketHandler());
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Constants.MOD_ID)
	public static class RegistryEvents {

		private RegistryEvents() {
		}

		@SubscribeEvent(priority = EventPriority.HIGH)
		public static void createFeatures(RegistryEvent.Register<Block> event) {
			ModuleManager.getModuleHandler().createFeatures();
		}

		@SubscribeEvent(priority = EventPriority.LOW)
		public static void createObjects(RegistryEvent.Register<Block> event) {
			ModuleManager.getModuleHandler().createObjects((type, moduleID) -> !moduleID.equals(ForestryModuleUids.CRATE));
			ModuleManager.getModuleHandler().runRegisterBackpacksAndCrates();
			ModuleManager.getModuleHandler().createObjects((type, moduleID) -> moduleID.equals(ForestryModuleUids.CRATE));
		}

		@SubscribeEvent(priority = EventPriority.LOWEST)
		public static void registerObjects(RegistryEvent.Register<?> event) {
			ModuleManager.getModuleHandler().registerObjects(event);
		}

		@SubscribeEvent
		public static void registerRecipeSerialziers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
			IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();
			CraftingHelper.register(ModuleEnabledCondition.Serializer.INSTANCE);
			CraftingHelper.register(new ResourceLocation(Constants.MOD_ID, "fallback"), FallbackIngredient.Serializer.INSTANCE);

			register(registry, ICarpenterRecipe.TYPE, new CarpenterRecipe.Serializer());
			register(registry, ICentrifugeRecipe.TYPE, new CentrifugeRecipe.Serializer());
			register(registry, IFabricatorRecipe.TYPE, new FabricatorRecipe.Serializer());
			register(registry, IFabricatorSmeltingRecipe.TYPE, new FabricatorSmeltingRecipe.Serializer());
			register(registry, IFermenterRecipe.TYPE, new FermenterRecipe.Serializer());
			register(registry, IHygroregulatorRecipe.TYPE, new HygroregulatorRecipe.Serializer());
			register(registry, IMoistenerRecipe.TYPE, new MoistenerRecipe.Serializer());
			register(registry, ISqueezerRecipe.TYPE, new SqueezerRecipe.Serializer());
			register(registry, ISqueezerContainerRecipe.TYPE, new SqueezerContainerRecipe.Serializer());
			register(registry, IStillRecipe.TYPE, new StillRecipe.Serializer());
			register(registry, ISolderRecipe.TYPE, new CircuitRecipe.Serializer());
		}

		private static void register(IForgeRegistry<IRecipeSerializer<?>> registry, IRecipeType<?> type, IRecipeSerializer<?> serializer) {
			Registry.register(Registry.RECIPE_TYPE, type.toString(), type);
			registry.register(serializer.setRegistryName(new ResourceLocation(type.toString())));
		}

		@SubscribeEvent
		public static void registerLootModifiers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
			IForgeRegistry<GlobalLootModifierSerializer<?>> registry = event.getRegistry();
			registry.register(ConditionLootModifier.SERIALIZER);
			registry.register(GrafterLootModifier.SERIALIZER);

			OrganismFunction.type = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(Constants.MOD_ID, "set_species_nbt"), new LootFunctionType(new OrganismFunction.Serializer()));
			CountBlockFunction.type = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(Constants.MOD_ID, "count_from_block"), new LootFunctionType(new CountBlockFunction.Serializer()));
		}


		@SubscribeEvent
		@OnlyIn(Dist.CLIENT)
		public void handleTextureRemap(TextureStitchEvent.Pre event) {
			if (event.getMap().location() == PlayerContainer.BLOCK_ATLAS) {
				TextureManagerForestry.getInstance().registerSprites(ISpriteRegistry.fromEvent(event));
				ModelBlockCached.clear();
			}
		}
	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
		ModuleManager.serverStarting(event.getServer());
	}

	@Nullable
	public File getConfigFolder() {
		return configFolder;
	}

	public void processIMCMessages(InterModProcessEvent event) {
		ModuleManager.getModuleHandler().processIMCMessages(event.getIMCStream());
	}
}
