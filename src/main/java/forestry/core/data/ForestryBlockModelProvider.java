package forestry.core.data;

import net.minecraft.world.level.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

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
	}
}
