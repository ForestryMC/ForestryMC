package forestry.arboriculture.compat;

import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.items.ItemRegistryArboriculture;
import forestry.core.utils.JeiUtil;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class ArboricultureJeiPlugin extends BlankModPlugin {
	@Override
	public void register(IModRegistry registry) {
		ItemRegistryArboriculture items = PluginArboriculture.items;

		JeiUtil.addDescription(registry,
				items.grafter,
				items.grafterProven
		);
	}
}
