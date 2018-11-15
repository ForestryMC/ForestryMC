package forestry.lepidopterology.blocks;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.lepidopterology.IAlleleButterflyCocoon;
import forestry.core.config.Constants;
import forestry.core.render.ForestryStateMapper;
import forestry.lepidopterology.genetics.alleles.AlleleButterflyCocoon;

@SideOnly(Side.CLIENT)
public class CocoonStateMapper extends ForestryStateMapper {

	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {
		if (block instanceof BlockCocoon || block instanceof BlockSolidCocoon) {
			for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
				if (allele instanceof IAlleleButterflyCocoon) {
					for (int age = 0; age < 3; age++) {
						IAlleleButterflyCocoon cocoon = (IAlleleButterflyCocoon) allele;
						String resourcePath = Constants.MOD_ID + ":cocoons/cocoon_" + cocoon.getCocoonName();
						IBlockState state = block.getDefaultState().withProperty(AlleleButterflyCocoon.COCOON, cocoon).withProperty(AlleleButterflyCocoon.AGE, age);
						String propertyString = "age=" + age;
						mapStateModelLocations.put(state, new ModelResourceLocation(resourcePath, propertyString));
					}
				}
			}
		}
		return mapStateModelLocations;
	}

}
