package genetics;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import genetics.api.GeneticsAPI;
import genetics.api.IGeneTemplate;
import genetics.api.organism.IOrganism;
import genetics.api.root.IRootDefinition;
import genetics.api.root.components.DefaultStage;
import genetics.commands.CommandListAlleles;
import genetics.plugins.PluginManager;

@Mod(Genetics.MOD_ID)
public class Genetics {
	public static final String MOD_ID = "geneticsapi";

	/**
	 * Capability for {@link IOrganism}.
	 */
	public static Capability<IOrganism<?>> ORGANISM = CapabilityManager.get(new CapabilityToken<>() {
	});
	public static Capability<IGeneTemplate> GENE_TEMPLATE = CapabilityManager.get(new CapabilityToken<>() {
	});

	public Genetics() {
		GeneticsAPI.apiInstance = ApiInstance.INSTANCE;
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setupCommon);
		modBus.addListener(this::loadComplete);
		modBus.register(this);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
	}

	@SubscribeEvent
	public void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(IOrganism.class);
		event.register(IGeneTemplate.class);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		PluginManager.create();
		PluginManager.initPlugins();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void registerFinished(RegistryEvent.Register<Item> event) {
		for (IRootDefinition<?> definition : GeneticsAPI.apiInstance.getRoots().values()) {
			if (!definition.isPresent()) {
				continue;
			}
			definition.get().getComponentContainer().onStage(DefaultStage.REGISTRATION);
		}
	}

	private void setupCommon(FMLCommonSetupEvent event) {
		for (IRootDefinition<?> definition : GeneticsAPI.apiInstance.getRoots().values()) {
			if (!definition.isPresent()) {
				continue;
			}
			definition.get().getComponentContainer().onStage(DefaultStage.SETUP);
		}
	}

	private void loadComplete(FMLLoadCompleteEvent event) {
		for (IRootDefinition<?> definition : GeneticsAPI.apiInstance.getRoots().values()) {
			if (!definition.isPresent()) {
				continue;
			}
			definition.get().getComponentContainer().onStage(DefaultStage.COMPLETION);
		}
	}

	public void registerCommands(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		LiteralArgumentBuilder<CommandSourceStack> rootCommand = LiteralArgumentBuilder.literal("genetics");
		rootCommand.then(CommandListAlleles.register());
		dispatcher.register(rootCommand);
	}
}
