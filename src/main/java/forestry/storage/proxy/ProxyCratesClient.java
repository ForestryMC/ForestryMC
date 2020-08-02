package forestry.storage.proxy;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import forestry.core.config.Constants;
import forestry.core.utils.ForgeUtils;
import forestry.modules.IClientModuleHandler;
import forestry.modules.features.FeatureItem;
import forestry.storage.ModuleCrates;
import forestry.storage.items.ItemCrated;
import forestry.storage.models.ModelLoaderCrate;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ProxyCratesClient extends ProxyCrates implements IClientModuleHandler {

    public ProxyCratesClient() {
        ForgeUtils.registerSubscriber(this);
    }

    @Override
    public void setupClient(FMLClientSetupEvent event) {
        ModelLoaderRegistry.registerLoader(ModelLoaderCrate.LOCATION, ModelLoaderCrate.INSTANCE);
    }

    @Override
    public void registerCrateModel() {
        //ModelLoaderRegistry.registerLoader(ModelLoaderCrate.INSTANCE);
		/*ModelResourceLocation modelLocation = new ModelResourceLocation("forestry:crate-filled", "crate-filled");
		ModelEntry modelEntry = new ModelEntry(modelLocation, new ModelCrate());
		ModelManager.getInstance().registerCustomModel(modelEntry);*/
    }

    @SubscribeEvent
    public void onModelRegister(ModelRegistryEvent event) {
        ModelLoader.addSpecialModel(new ModelResourceLocation(Constants.MOD_ID + ":crate-filled", "inventory"));
    }


    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        //TODO: Remove if forge fixes the model loaders
        for (FeatureItem<ItemCrated> featureCrated : ModuleCrates.crates) {
            ItemCrated itemCrated = featureCrated.item();
            ResourceLocation registryName = itemCrated.getRegistryName();
            if (registryName == null) {
                continue;
            }
            //event.getModelRegistry().put(new ModelResourceLocation(registryName, "inventory"), new ModelCrate(itemCrated).bake(event.getModelLoader(), DefaultTextureGetter.INSTANCE, ModelRotation.X0_Y0, DefaultVertexFormats.ITEM));
        }
    }
}
