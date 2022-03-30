package forestry.core.data;

import javax.annotation.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;

import net.minecraftforge.common.data.ExistingFileHelper;

import forestry.core.config.Constants;
import forestry.core.fluids.ForestryFluids;

public class ForestryFluidTagsProvider extends FluidTagsProvider {
	public ForestryFluidTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper fileHelper) {
		super(generator, Constants.MOD_ID, fileHelper);
	}

	@Override
	protected void addTags() {
		for (ForestryFluids fluid : ForestryFluids.values()) {
			tag(FluidTags.WATER).add(fluid.getFluid()).add(fluid.getFlowing());
		}
	}


	@Override
	public String getName() {
		return "Forestry Fluid Tags";
	}
}
