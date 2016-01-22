package forestry.arboriculture.render;

import java.util.LinkedHashMap;
import java.util.Map;
import com.google.common.collect.Maps;

import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.blocks.BlockFruitPod;
import forestry.core.render.StateMapperForestry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StateMapperFluidPod extends StateMapperForestry{
	
	protected Map mapStateModelLocations = Maps.newLinkedHashMap();

	@Override
	public Map putStateModelLocations(Block block) {
		for	(EnumFacing facing : EnumFacing.values()){
			if(facing != EnumFacing.UP && facing != EnumFacing.DOWN){
				for	(int age = 0;age < BlockCocoa.AGE.getAllowedValues().size();age++){
					for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
						if (allele instanceof IAlleleFruit) {
							if(((IAlleleFruit) allele).getModelName() != null){
								IBlockState state = block.getDefaultState().withProperty(BlockFruitPod.FRUIT, (IAlleleFruit)allele).withProperty(BlockDirectional.FACING, facing).withProperty(BlockCocoa.AGE, age);
								LinkedHashMap linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
								String modID = ((IAlleleFruit) allele).getModID();
								if(modID == null){
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