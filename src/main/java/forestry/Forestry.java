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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tileentity.TileEntityType;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import forestry.api.climate.ClimateManager;
import forestry.api.core.ForestryAPI;
import forestry.core.EventHandlerCore;
import forestry.core.ModuleFluids;
import forestry.core.TickHandlerCoreServer;
import forestry.core.climate.ClimateFactory;
import forestry.core.climate.ClimateRoot;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.GameMode;
import forestry.core.data.BlockStateProvider;
import forestry.core.data.ForestryBlockTagsProvider;
import forestry.core.data.ForestryItemTagsProvider;
import forestry.core.data.ForestryLootTableProvider;
import forestry.core.data.ForestryRecipeProvider;
import forestry.core.data.WoodModelProvider;
import forestry.core.errors.EnumErrorCode;
import forestry.core.errors.ErrorStateRegistry;
import forestry.core.multiblock.MultiblockEventHandler;
import forestry.core.network.NetworkHandler;
import forestry.core.network.PacketHandlerServer;
import forestry.core.proxy.Proxies;
import forestry.core.proxy.ProxyClient;
import forestry.core.proxy.ProxyCommon;
import forestry.core.proxy.ProxyRender;
import forestry.core.proxy.ProxyRenderClient;
import forestry.core.recipes.ModuleEnabledCondition;
import forestry.core.render.ColourProperties;
import forestry.core.render.TextureManagerForestry;
import forestry.modules.ForestryModules;
import forestry.modules.ModuleManager;
//import forestry.plugins.ForestryCompatPlugins;
//import forestry.plugins.PluginBuildCraftFuels;
//import forestry.plugins.PluginIC2;
//import forestry.plugins.PluginNatura;
//import forestry.plugins.PluginTechReborn;

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
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
		//		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		modEventBus.addListener(this::processIMCMessages);
		modEventBus.addListener(this::clientStuff);
		modEventBus.addListener(this::gatherData);
		modEventBus.register(TickHandlerCoreServer.class);    //TODO - correct?
		EventHandlerCore eventHandlerCore = new EventHandlerCore();
		modEventBus.register(eventHandlerCore);
		MinecraftForge.EVENT_BUS.register(this);
		//TODO - I think this is how it works
		Proxies.render = DistExecutor.runForDist(() -> () -> new ProxyRenderClient(), () -> () -> new ProxyRender());
		Proxies.common = DistExecutor.runForDist(() -> () -> new ProxyClient(), () -> () -> new ProxyCommon());

		ModuleManager.getModuleHandler().runSetup();
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> clientInit(modEventBus));
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(this::setupClient));
	}

	public void clientStuff(FMLClientSetupEvent e) {
		ModuleManager.getModuleHandler().registerGuiFactories();
	}

	@Nullable
	private static PacketHandlerServer packetHandler;

	public static PacketHandlerServer getPacketHandler() {
		Preconditions.checkNotNull(packetHandler);
		return packetHandler;
	}

	private void setup(FMLCommonSetupEvent event) {
		packetHandler = new PacketHandlerServer();

		// Register event handler
		MinecraftForge.EVENT_BUS.register(new MultiblockEventHandler());
		MinecraftForge.EVENT_BUS.register(Config.class);
		Proxies.common.registerEventHandlers();
		configFolder = new File("."); //new File(event.getModConfigurationDirectory(), Constants.MOD_ID);
		//TODO - config
		Config.load(Dist.DEDICATED_SERVER);

		String gameMode = Config.gameMode;
		Preconditions.checkNotNull(gameMode);
		ForestryAPI.activeMode = new GameMode(gameMode);

		//TODO - DistExecutor
		ModuleManager.getModuleHandler().runPreInit();
		Proxies.render.registerItemAndBlockColors();
		//TODO put these here for now
		ModuleManager.getModuleHandler().runInit();
		ModuleManager.getModuleHandler().runPostInit();
	}

	@OnlyIn(Dist.CLIENT)
	public void setupClient(FMLClientSetupEvent event) {
		ModuleManager.getModuleHandler().runClientSetup();
	}

	private void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();

		if (event.includeServer()) {
			generator.addProvider(new ForestryBlockTagsProvider(generator));
			generator.addProvider(new ForestryItemTagsProvider(generator));
			generator.addProvider(new ForestryLootTableProvider(generator));
			generator.addProvider(new ForestryRecipeProvider(generator));
			generator.addProvider(new BlockStateProvider(generator));
			generator.addProvider(new WoodModelProvider(generator));

			try {
				generator.run();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			//generator.run();
		}
	}

	private void clientInit(IEventBus modEventBus) {
		modEventBus.addListener(EventPriority.NORMAL, false, ColorHandlerEvent.Block.class, x -> {
			Minecraft minecraft = Minecraft.getInstance();
			IResourceManager resourceManager = minecraft.getResourceManager();
			if(resourceManager instanceof IReloadableResourceManager) {
				IReloadableResourceManager reloadableManager = (IReloadableResourceManager) resourceManager;
				reloadableManager.addReloadListener(ColourProperties.INSTANCE);
				reloadableManager.addReloadListener(TextureManagerForestry.getInstance());
			}
		});
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Constants.MOD_ID)
	public static class RegistryEvents {
		@SubscribeEvent(priority = EventPriority.HIGH)
		@SuppressWarnings("unchecked")
		public static void onRegister(RegistryEvent.Register event) {
			ModuleManager.getModuleHandler().onObjectRegistration(event);
		}

		@SubscribeEvent
		public static void registerFluids(RegistryEvent.Register<Fluid> event) {
			ModuleFluids.registerFluids();
		}

		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
			ModuleManager.getModuleHandler().registerBlocks();
		}

		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event) {
			ModuleManager.getModuleHandler().registerItems();

			ModuleManager.getModuleHandler().runRegisterBackpacksAndCrates();
		}

		@SubscribeEvent
		public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
			ModuleManager.getModuleHandler().registerTileEntities();
		}

		@SubscribeEvent
		public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
			ModuleManager.getModuleHandler().registerContainerTypes(event.getRegistry());
		}

		@SubscribeEvent
		public static void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
			ModuleManager.getModuleHandler().registerEntityTypes(event.getRegistry());
		}

		@SubscribeEvent
		public static void registerRecipeSerialziers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
			CraftingHelper.register(ModuleEnabledCondition.Serializer.INSTANCE);
		}

	}


	//client
	@SubscribeEvent
	public void registerModels(ModelBakeEvent event) {
		Proxies.render.registerModels(event);
	}

	//split
	//TODO - when to run these events
	//		@EventHandler
	//		public void init(FMLInitializationEvent event) {
	//			// Register gui handler
	//			NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	//
	//			ModuleManager.getInternalHandler().runInit();
	//
	//			AdvancementManager.registerTriggers();
	//		}
	//
	////	@EventHandler
	//	public void postInit(FMLPostInitializationEvent event) {
	//		ModuleManager.getInternalHandler().runPostInit();
	//
	//		// Register world generator
	//		WorldGenerator worldGenerator = new WorldGenerator();
	//		GameRegistry.registerWorldGenerator(worldGenerator, 0);
	//
	//		// Register tick handlers
	//		Proxies.common.registerTickHandlers(worldGenerator);
	//	}

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
