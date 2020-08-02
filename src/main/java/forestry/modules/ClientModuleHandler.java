package forestry.modules;

import java.util.function.Consumer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import forestry.api.modules.IForestryModule;
import forestry.core.network.IPacketRegistry;
import forestry.core.utils.ForgeUtils;
import forestry.core.utils.Log;

@OnlyIn(Dist.CLIENT)
public class ClientModuleHandler extends CommonModuleHandler {

    public ClientModuleHandler() {
        super();
        ForgeUtils.registerSubscriber(this);
    }

    @Override
    public void runClientSetup() {
        stage = Stage.CLIENT_SETUP;
        for (IForestryModule module : modules) {
            Log.debug("Init Start: {}", module);
            module.clientSetup();
            Log.debug("Init Complete: {}", module);
        }
        registry.clientSetup();
    }

    @Override
    protected void registerPackages(IPacketRegistry packetRegistry) {
        super.registerPackages(packetRegistry);
        packetRegistry.registerPacketsClient();
    }

    @SubscribeEvent
    public void registerSprites(TextureStitchEvent.Pre event) {
        modules.forEach((module -> actOnHandler(module, (handler) -> handler.registerSprites(event))));
    }

    @SubscribeEvent
    public void handleSprites(TextureStitchEvent.Post event) {
        modules.forEach((module -> actOnHandler(module, (handler) -> handler.handleSprites(event))));
    }

    @SubscribeEvent
    public void bakeModels(ModelBakeEvent event) {
        modules.forEach((module -> actOnHandler(module, (handler) -> handler.bakeModels(event))));
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        modules.forEach((module -> actOnHandler(module, (handler) -> handler.registerModels(event))));
    }

    @SubscribeEvent
    public void setupClient(FMLClientSetupEvent event) {
        modules.forEach((module -> actOnHandler(module, (handler) -> handler.setupClient(event))));
    }

    private void actOnHandler(BlankForestryModule module, Consumer<IClientModuleHandler> actor) {
        ISidedModuleHandler handler = module.getModuleHandler();
        if (!(handler instanceof IClientModuleHandler)) {
            return;
        }
        IClientModuleHandler clientHandler = (IClientModuleHandler) handler;
        actor.accept(clientHandler);
    }
}
