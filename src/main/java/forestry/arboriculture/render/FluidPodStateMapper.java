package forestry.arboriculture.render;

import com.google.common.collect.Maps;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.blocks.BlockFruitPod;
import forestry.core.render.ForestryStateMapper;

@SideOnly(Side.CLIENT)
public class FluidPodStateMapper extends ForestryStateMapper {
	
	private final Map<IBlockState, ModelResourceLocation> mapStateModelLocations = Maps.newLinkedHashMap();

	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {
		for (EnumFacing facing : EnumFacing.values()) {
			if (facing != EnumFacing.UP && facing != EnumFacing.DOWN) {
				for (int age = 0; age < BlockCocoa.AGE.getAllowedValues().size(); age++) {
					for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
						if (allele instanceof IAlleleFruit) {
							IAlleleFruit fruit = (IAlleleFruit) allele;
							if (fruit.getModelName() != null) {
								IBlockState state = block.getDefaultState().withProperty(BlockFruitPod.FRUIT, fruit).withProperty(BlockDirectional.FACING, facing).withProperty(BlockCocoa.AGE, age);
								LinkedHashMap<IProperty, Comparable> linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
								String modID = fruit.getModID();
								if (modID == null) {
									modID = "forestry";
								}
								String modelName = BlockFruitPod.FRUIT.getName((IAlleleFruit) linkedhashmap.remove(BlockFruitPod.FRUIT));
								String s = String.format("%s:%s", modID, "pods/" + modelName);
								mapStateModelLocations.put(state, new ModelResourceLocation(s, getPropertyString(linkedhashmap)));
							}
						}
					}
				}
			}
		}
		return mapStateModelLocations;
	}
	
}