package forestry.modules;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public interface IClientModuleHandler extends ISidedModuleHandler {

    default void registerSprites(TextureStitchEvent.Pre event) {
    }

    default void handleSprites(TextureStitchEvent.Post event) {
    }

    default void bakeModels(ModelBakeEvent event) {
    }

    default void registerModels(ModelRegistryEvent event) {
    }

    default void setupClient(FMLClientSetupEvent event) {
    }
}
