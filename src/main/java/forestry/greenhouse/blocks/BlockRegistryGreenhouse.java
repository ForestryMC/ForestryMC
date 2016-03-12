package forestry.greenhouse.blocks;

import java.util.Map;

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;
import net.minecraft.item.ItemStack;

public class BlockRegistryGreenhouse extends BlockRegistry{
	
	private final Map<BlockGreenhouseType, BlockGreenhouse> greenhouseBlockMap;
	
	public BlockRegistryGreenhouse() {
		greenhouseBlockMap = BlockGreenhouse.create();
		for (BlockGreenhouse block : greenhouseBlockMap.values()) {
			registerBlock(block, new ItemBlockForestry(block), "greenhouse." + block.getGreenhouseType());
		}
	}

	public ItemStack getGreenhouseBlock(BlockGreenhouseType type) {
		BlockGreenhouse greenhouseBlock = greenhouseBlockMap.get(type);
		return new ItemStack(greenhouseBlock);
	}
	
}
