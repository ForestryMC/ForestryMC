package genetics;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;

@Mod(Genetics.MOD_ID)
public class Genetics {
    public static final String MOD_ID = "geneticsapi";

    /**
     * Capability for {@link IOrganism}.
     */
    @CapabilityInject(IOrganism.class)
    @Nullable
    public static Capability<IOrganism> ORGANISM;
    @CapabilityInject(IGeneTemplate.class)
    @Nullable
    public static Capability<IGeneTemplate> GENE_TEMPLATE;

    public Genetics() {
        GeneticsAPI.apiInstance = ApiInstance.INSTANCE;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::setupCommon);
        modBus.addListener(this::loadComplete);
        modBus.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
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

    private static class NullStorage<T> implements Capability.IStorage<T> {
        @Nullable
        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
            /* compiled code */
            return null;
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
            /* compiled code */
        }
    }

    public void serverStarting(FMLServerStartingEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getServer().getCommandManager().getDispatcher();
        LiteralArgumentBuilder<CommandSource> rootCommand = LiteralArgumentBuilder.literal("genetics");
        rootCommand.then(CommandListAlleles.register());
        dispatcher.register(rootCommand);
    }
}
