package forestry.farming.proxy;

import forestry.core.config.ForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.core.render.BlockModelIndex;
import forestry.farming.render.FarmBlockRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class ClientProxyFarming extends ProxyFarming {

	@Override
	public void initializeRendering() {
		Proxies.render.registerBlockModel(new BlockModelIndex(new ModelResourceLocation("forestry:ffarm"),
				new ModelResourceLocation("forestry:ffarm", "inventory"), new FarmBlockRenderer(),
				ForestryBlock.farm.block()));
	}

}
