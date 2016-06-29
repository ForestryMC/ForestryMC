package forestry.greenhouse.models;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.Maps;

import forestry.core.render.ForestryStateMapper;
import forestry.greenhouse.blocks.BlockGreenhouseDoor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public class GreenhouseDoorStateMapper extends ForestryStateMapper {

	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {
		for(IBlockState state : block.getBlockState().getValidStates()){
			if(state.getValue(BlockGreenhouseDoor.CAMOUFLAGED)){
				mapStateModelLocations.put(state, new ModelResourceLocation(block.getRegistryName(), "camouflage"));
			}else{
				LinkedHashMap<IProperty<?>, Comparable<?>> linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
				linkedhashmap.remove(BlockGreenhouseDoor.CAMOUFLAGED);
				linkedhashmap.remove(BlockDoor.POWERED);
				mapStateModelLocations.put(state, new ModelResourceLocation(block.getRegistryName(), getPropertyString(linkedhashmap)));
			}
		}
		return mapStateModelLocations;
	}

}
