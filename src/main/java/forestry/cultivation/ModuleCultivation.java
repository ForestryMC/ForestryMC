package forestry.cultivation;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.util.ResourceLocation;

import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.cultivation.features.CultivationContainers;
import forestry.cultivation.gui.GuiPlanter;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.CULTIVATION, name = "Cultivation", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.cultivation.description")
public class ModuleCultivation extends BlankForestryModule {

	@Override
	public void registerGuiFactories() {
		ScreenManager.registerFactory(CultivationContainers.PLANTER.containerType(), GuiPlanter::new);
	}

	@Override
	public Set<ResourceLocation> getDependencyUids() {
		return ImmutableSet.of(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.CORE),
				new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.FARMING));
	}
}
