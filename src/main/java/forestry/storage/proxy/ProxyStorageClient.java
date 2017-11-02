package forestry.storage.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.models.ModelEntry;
import forestry.core.models.ModelManager;
import forestry.storage.models.ModelCrate;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ProxyStorageClient extends ProxyStorage {

	@Override
	public void registerCrateModel() {
		ModelResourceLocation modelLocation = new ModelResourceLocation("forestry:crate-filled", "crate-filled");
		ModelEntry modelEntry = new ModelEntry(modelLocation, new ModelCrate());
		ModelManager.getInstance().registerCustomModel(modelEntry);
	}

}
