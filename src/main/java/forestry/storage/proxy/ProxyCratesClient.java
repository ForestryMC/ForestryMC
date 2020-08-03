package forestry.storage.proxy;

import forestry.api.storage.EnumBackpackType;
import forestry.core.config.Constants;
import forestry.core.utils.ForgeUtils;
import forestry.modules.IClientModuleHandler;
import forestry.storage.BackpackMode;
import forestry.storage.models.BackpackItemModel;
import forestry.storage.models.CrateModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ProxyCratesClient extends ProxyCrates implements IClientModuleHandler {

    public ProxyCratesClient() {
        ForgeUtils.registerSubscriber(this);
    }

    @Override
    public void setupClient(FMLClientSetupEvent event) {
        ModelLoaderRegistry.registerLoader(CrateModel.Loader.LOCATION, new CrateModel.Loader());
        ModelLoaderRegistry.registerLoader(BackpackItemModel.Loader.LOCATION, new BackpackItemModel.Loader());


        for (EnumBackpackType backpackType : EnumBackpackType.values()) {
            for (BackpackMode mode : BackpackMode.values()) {
                ModelLoader.addSpecialModel(backpackType.getLocation(mode));
            }
        }

        ModelLoader.addSpecialModel(new ModelResourceLocation(Constants.MOD_ID + ":crate-filled", "inventory"));
    }

}
