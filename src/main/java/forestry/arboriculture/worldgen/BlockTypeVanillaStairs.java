package forestry.arboriculture.worldgen;

import net.minecraft.block.Block;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.worldgen.BlockType;

public class BlockTypeVanillaStairs extends BlockType {
	public BlockTypeVanillaStairs(Block block, int meta) {
		super(block, meta);
	}

	@Override
	public void setDirection(ForgeDirection facing) {
		switch (facing) {
			case NORTH:
				meta = 3;
				break;
			case SOUTH:
				meta = 2;
				break;
			case WEST:
				meta = 1;
				break;
			default:
				meta = 0;
				break;
		}
	}
}
