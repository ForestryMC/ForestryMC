package forestry.modules;

import net.minecraft.util.ResourceLocation;

import forestry.core.config.Constants;

public class ModuleIdentifier {
	public final ResourceLocation uid;

	public ModuleIdentifier(String moduleID) {
		this.uid = new ResourceLocation(Constants.MOD_ID, moduleID);
	}

	public boolean isEnabled() {
		return ModuleHelper.isModuleEnabled(uid.getPath(), uid.getNamespace());
	}
}
