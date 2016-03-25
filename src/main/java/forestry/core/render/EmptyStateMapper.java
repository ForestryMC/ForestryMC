package forestry.core.render;

import java.util.Collections;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EmptyStateMapper implements IStateMapper {

	public static final EmptyStateMapper instance = new EmptyStateMapper();
	
	private EmptyStateMapper() {
	}
	
	@Override
	public Map putStateModelLocations(Block block) {
		return Collections.emptyMap();
	}

}
