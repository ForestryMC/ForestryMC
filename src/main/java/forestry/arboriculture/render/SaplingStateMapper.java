package forestry.arboriculture.render;

import com.google.common.collect.Maps;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.blocks.BlockSapling;
import forestry.core.render.ForestryStateMapper;

@SideOnly(Side.CLIENT)
public class SaplingStateMapper extends ForestryStateMapper {

	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleTreeSpecies) {
				IAlleleTreeSpecies tree = (IAlleleTreeSpecies) allele;
				IBlockState state = block.getDefaultState().withProperty(BlockSapling.TREE, tree);
				LinkedHashMap<IProperty<?>, Comparable<?>> linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
				String modID = tree.getModID();
				String s = String.format("%s:%s", modID, "germlings");
				mapStateModelLocations.put(state, new ModelResourceLocation(s, getPropertyString(linkedhashmap)));
			}
		}
		return mapStateModelLocations;
	}

}
