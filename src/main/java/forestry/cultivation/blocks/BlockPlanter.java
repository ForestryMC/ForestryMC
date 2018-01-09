package forestry.cultivation.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;

import forestry.core.blocks.BlockBase;

public class BlockPlanter extends BlockBase<BlockTypePlanter> {
	public BlockPlanter(BlockTypePlanter blockType) {
		super(blockType, Material.WOOD);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
