package forestry.arboriculture.compat;

import com.google.common.base.Preconditions;
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
		Preconditions.checkNotNull(items);

		JeiUtil.addDescription(registry,
				items.grafter,
				items.grafterProven
		);
	}
}
