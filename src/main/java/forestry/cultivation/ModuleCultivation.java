package forestry.cultivation;

import com.google.common.collect.ImmutableSet;
import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.cultivation.features.CultivationContainers;
import forestry.cultivation.gui.GuiPlanter;
import forestry.cultivation.proxy.ProxyCultivation;
import forestry.cultivation.proxy.ProxyCultivationClient;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ISidedModuleHandler;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

import java.util.Set;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.CULTIVATION, name = "Cultivation", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.cultivation.description")
public class ModuleCultivation extends BlankForestryModule {

    @SuppressWarnings("NullableProblems")
    public static ProxyCultivation proxy;

    public ModuleCultivation() {
        proxy = DistExecutor.runForDist(() -> ProxyCultivationClient::new, () -> ProxyCultivation::new);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerGuiFactories() {
        ScreenManager.registerFactory(CultivationContainers.PLANTER.containerType(), GuiPlanter::new);
    }

    @Override
    public Set<ResourceLocation> getDependencyUids() {
        return ImmutableSet.of(
                new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.CORE),
                new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.FARMING)
        );
    }

    @Override
    public ISidedModuleHandler getModuleHandler() {
        return proxy;
    }

}
