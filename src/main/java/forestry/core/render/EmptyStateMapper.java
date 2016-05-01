package forestry.core.render;

import java.util.Collections;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.resources.model.ModelResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EmptyStateMapper implements IStateMapper {

	public static final EmptyStateMapper instance = new EmptyStateMapper();
	
	private EmptyStateMapper() {
	}
	
	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {
		return Collections.emptyMap();
	}

}
