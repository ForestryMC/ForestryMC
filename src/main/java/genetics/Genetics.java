package genetics;

import javax.annotation.Nullable;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import genetics.api.GeneticHelper;
import genetics.api.GeneticsAPI;
import genetics.api.IGeneTemplate;
import genetics.api.organism.IOrganism;
import genetics.api.root.IRootDefinition;
import genetics.api.root.components.DefaultStage;
import genetics.commands.CommandListAlleles;
import genetics.individual.GeneticSaveHandler;
import genetics.individual.SaveFormat;
import genetics.items.GeneTemplate;
import genetics.plugins.PluginManager;

@Mod(Genetics.MOD_ID)
public class Genetics {
	public static final String MOD_ID = "geneticsapi";

	/**
	 * Capability for {@link IOrganism}.
	 */
	public static Capability<IOrganism> ORGANISM = CapabilityManager.get(new CapabilityToken<>() {});
	public static Capability<IGeneTemplate> GENE_TEMPLATE = CapabilityManager.get(new CapabilityToken<>() {});

	public Genetics() {
		GeneticsAPI.apiInstance = ApiInstance.INSTANCE;
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setupCommon);
		modBus.addListener(this::loadComplete);
		modBus.register(this);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	@SuppressWarnings("unused")
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		CapabilityManager.INSTANCE.register(IOrganism.class, new NullStorage<>(), () -> GeneticHelper.EMPTY);
		CapabilityManager.INSTANCE.register(IGeneTemplate.class, new NullStorage<>(), () -> GeneTemplate.EMPTY);

		PluginManager.create();

		PluginManager.initPlugins();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@SuppressWarnings("unused")
	public void registerFinished(RegistryEvent.Register<Item> event) {
		for (IRootDefinition definition : GeneticsAPI.apiInstance.getRoots().values()) {
			if (!definition.isPresent()) {
				continue;
			}
			definition.get().getComponentContainer().onStage(DefaultStage.REGISTRATION);
		}
	}

	@SuppressWarnings("unused")
	private void setupCommon(FMLCommonSetupEvent event) {
		for (IRootDefinition definition : GeneticsAPI.apiInstance.getRoots().values()) {
			if (!definition.isPresent()) {
				continue;
			}
			definition.get().getComponentContainer().onStage(DefaultStage.SETUP);
		}
	}

	@SuppressWarnings("unused")
	private void loadComplete(FMLLoadCompleteEvent event) {
		for (IRootDefinition definition : GeneticsAPI.apiInstance.getRoots().values()) {
			if (!definition.isPresent()) {
				continue;
			}
			definition.get().getComponentContainer().onStage(DefaultStage.COMPLETION);
		}
		GeneticSaveHandler.setWriteFormat(SaveFormat.BINARY);
	}

	public void registerCommands(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		LiteralArgumentBuilder<CommandSourceStack> rootCommand = LiteralArgumentBuilder.literal("genetics");
		rootCommand.then(CommandListAlleles.register());
		dispatcher.register(rootCommand);
	}
}
