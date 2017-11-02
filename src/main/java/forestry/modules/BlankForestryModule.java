package forestry.modules;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

import net.minecraft.util.ResourceLocation;

import forestry.core.config.Constants;

public class BlankForestryModule implements IForestryModule {

	/**
	 * The ForestryModule.moduleID()s of any other modules this module depends on.
	 */
	public Set<ResourceLocation> getDependencyUids(){
		return ImmutableSet.of(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.CORE));
	}

	@Override
	public String toString() {
		ForestryModule forestryModule = getClass().getAnnotation(ForestryModule.class);
		if (forestryModule == null) {
			return getClass().getSimpleName();
		}
		return forestryModule.name() + " Module";
	}
}
