package forestry.cultivation;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.cultivation.blocks.BlockRegistryCultivation;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.CULTIVATION, name = "Cultivation", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.cultivation.description")
public class ModuleCultivation extends BlankForestryModule {
	@Nullable
	private static BlockRegistryCultivation blocks;

	public static BlockRegistryCultivation getBlocks() {
		Preconditions.checkState(blocks != null);
		return blocks;
	}

	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryCultivation();
	}

	@Override
	public Set<ResourceLocation> getDependencyUids() {
		return ImmutableSet.of(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.CORE),
				new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.FARMING));
	}

	@Override
	public void doInit() {
		BlockRegistryCultivation blocks = getBlocks();

		blocks.arboretum.init();
		blocks.farmCrops.init();
		blocks.farmMushroom.init();
		blocks.farmNether.init();
		blocks.farmGourd.init();
		blocks.farmEnder.init();
		//blocks.plantation.init();
		blocks.peatBog.init();
	}
}
