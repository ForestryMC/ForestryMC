//package forestry.arboriculture.render;
//
//import com.google.common.collect.Maps;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.properties.IProperty;
//import net.minecraft.client.renderer.model.ModelResourceLocation;
//
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
//import forestry.api.genetics.AlleleManager;
//import forestry.api.genetics.IAllele;
//import forestry.arboriculture.blocks.BlockSapling;
//import forestry.core.render.ForestryStateMapper;
//
//@OnlyIn(Dist.CLIENT)
//public class SaplingStateMapper extends ForestryStateMapper {
//
//	@Override
//	public Map<BlockState, ModelResourceLocation> putStateModelLocations(Block block) {
//		for (IAllele allele : AlleleManager.geneticRegistry.getRegisteredAlleles().values()) {
//			if (allele instanceof IAlleleTreeSpecies) {
//				IAlleleTreeSpecies tree = (IAlleleTreeSpecies) allele;
//				BlockState state = block.getDefaultState().with(BlockSapling.TREE, tree);
//				LinkedHashMap<IProperty<?>, Comparable<?>> linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
//				String modID = tree.getModID();
//				String s = String.format("%s:%s", modID, "germlings");
//				mapStateModelLocations.put(state, new ModelResourceLocation(s, getPropertyString(linkedhashmap)));
//			}
//		}
//		return mapStateModelLocations;
//	}
//
//}
