package forestry.arboriculture.compat;

import com.google.common.base.Preconditions;

import net.minecraft.util.ResourceLocation;

import forestry.api.core.ForestryAPI;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.items.ItemRegistryArboriculture;
import forestry.core.config.Constants;
import forestry.core.utils.JeiUtil;
import forestry.modules.ForestryModuleUids;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class ArboricultureJeiPlugin implements IModPlugin {
	@Override
	public void register(IModRegistry registry) {
		if (!ForestryAPI.enabledModules.contains(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.ARBORICULTURE))) {
			return;
		}

		ItemRegistryArboriculture items = ModuleArboriculture.getItems();
		Preconditions.checkNotNull(items);

		JeiUtil.addDescription(registry,
				items.grafter,
				items.grafterProven
		);
	}
}
