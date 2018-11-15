package forestry.arboriculture.compat;

import com.google.common.base.Preconditions;

import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.items.ItemRegistryArboriculture;
import forestry.core.utils.JeiUtil;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class ArboricultureJeiPlugin implements IModPlugin {
	@Override
	public void register(IModRegistry registry) {
		if (!ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
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
