package forestry.plugins.compat;

import forestry.api.core.CamouflageManager;
import forestry.core.config.Constants;
import forestry.core.utils.ModUtil;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.item.ItemStack;

@ForestryPlugin(pluginID = ForestryPluginUids.MALISIS_DOORES, name = "malisisdoors", author = "nedelosk", url = Constants.URL, unlocalizedDescription = "for.plugin.malisisDoors.description")
public class PluginMalisisDoors extends BlankForestryPlugin {

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded("malisisdoors");
	}

	@Override
	public String getFailMessage() {
		return "Malisis Door's not found";
	}
	
	@Override
	public void preInit() {
		CamouflageManager.camouflageAccess.addModIdToBlackList("door", "malisisdoors");
	}
	
}
