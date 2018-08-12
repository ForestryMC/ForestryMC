package forestry.storage.proxy;

import net.minecraftforge.client.model.ModelLoaderRegistry;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.storage.models.ModelLoaderCrate;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ProxyStorageClient extends ProxyStorage {

	@Override
	public void registerCrateModel() {
		ModelLoaderRegistry.registerLoader(ModelLoaderCrate.INSTANCE);
		/*ModelResourceLocation modelLocation = new ModelResourceLocation("forestry:crate-filled", "crate-filled");
		ModelEntry modelEntry = new ModelEntry(modelLocation, new ModelCrate());
		ModelManager.getInstance().registerCustomModel(modelEntry);*/
	}

}
