package forestry.core.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import forestry.core.config.Constants;
import forestry.core.fluids.ForestryFluids;

public class ForestryBlockModelProvider extends ModelProvider {
	public ForestryBlockModelProvider(DataGenerator generator) {
		super(generator, "block");
	}

	@Override
	protected void registerModels() {
		registerModel("farm", new ModelBuilder().parent("block/cube_all").texture("all", new ResourceLocation("block/stone_bricks")));
		for (ForestryFluids fluid : ForestryFluids.values()) {
			Block block = fluid.getFeature().fluidBlock().block();
			registerModel(block, new ModelBuilder().particle(fluid.getFeature().properties().resources[0]));
		}

		registerCubeAll("apatite_ore", "apatite");
		registerCubeAll("deepslate_apatite_ore", "deepslate_apatite");
		registerCubeAll("tin_ore", "tin");
		registerCubeAll("deepslate_tin_ore", "deepslate_tin");
		registerCubeAll("raw_tin_block", "raw_tin_block");
	}

	private void registerCubeAll(String path, String texturePath) {
		registerModel(path, new ModelBuilder().parent("block/cube_all").texture("all", new ResourceLocation(Constants.MOD_ID, "block/ores/" + texturePath)));
	}
}
