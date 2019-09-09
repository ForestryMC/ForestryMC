//package forestry.arboriculture.render;
//
//import java.util.Map;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.client.renderer.model.ModelResourceLocation;
//
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//import forestry.api.arboriculture.genetics.IAlleleFruit;
//import forestry.arboriculture.blocks.BlockFruitPod;
//import forestry.core.config.Constants;
//import forestry.core.render.ForestryStateMapper;
//
//@OnlyIn(Dist.CLIENT)
//public class FruitPodStateMapper extends ForestryStateMapper {
//
//	@Override
//	public Map<BlockState, ModelResourceLocation> putStateModelLocations(Block block) {
//		if (block instanceof BlockFruitPod) {
//			BlockFruitPod blockFruitPod = (BlockFruitPod) block;
//			IAlleleFruit fruit = blockFruitPod.getFruit();
//			String modID = fruit.getModID();
//			if (modID == null) {
//				modID = Constants.MOD_ID;
//			}
//			String modelName = fruit.getModelName();
//			String resourcePath = modID + ":pods/" + modelName;
//			for (BlockState state : block.getBlockState().getValidStates()) {
//				String propertyString = getPropertyString(state.getProperties());
//				mapStateModelLocations.put(state, new ModelResourceLocation(resourcePath, propertyString));
//			}
//		}
//		return mapStateModelLocations;
//	}
//
//}
