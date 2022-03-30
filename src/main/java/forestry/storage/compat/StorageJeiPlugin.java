package forestry.storage.compat;

import net.minecraft.resources.ResourceLocation;

import forestry.core.config.Constants;
import forestry.core.utils.JeiUtil;
import forestry.modules.ForestryModuleUids;
import forestry.storage.features.BackpackItems;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;


@JeiPlugin
public class StorageJeiPlugin implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.BACKPACKS);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		JeiUtil.addDescription(registration, "miner_bag",
				BackpackItems.MINER_BACKPACK,
				BackpackItems.MINER_BACKPACK_T_2
		);
		JeiUtil.addDescription(registration, "digger_bag",
				BackpackItems.DIGGER_BACKPACK,
				BackpackItems.DIGGER_BACKPACK_T_2
		);
		JeiUtil.addDescription(registration, "forester_bag",
				BackpackItems.FORESTER_BACKPACK,
				BackpackItems.FORESTER_BACKPACK_T_2
		);
		JeiUtil.addDescription(registration, "hunter_bag",
				BackpackItems.HUNTER_BACKPACK,
				BackpackItems.HUNTER_BACKPACK_T_2
		);
		JeiUtil.addDescription(registration, "adventurer_bag",
				BackpackItems.ADVENTURER_BACKPACK,
				BackpackItems.ADVENTURER_BACKPACK_T_2
		);
		JeiUtil.addDescription(registration, "builder_bag",
				BackpackItems.BUILDER_BACKPACK,
				BackpackItems.BUILDER_BACKPACK_T_2
		);
		JeiUtil.addDescription(registration, BackpackItems.APIARIST_BACKPACK);
		JeiUtil.addDescription(registration, BackpackItems.LEPIDOPTERIST_BACKPACK);
	}
}
