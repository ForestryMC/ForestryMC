package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import net.minecraftforge.common.ToolType;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.IWoodTyped;

//eg    public static final Block BIRCH_FENCE = register("birch_fence", new FenceBlock(Block.Properties.create(Material.WOOD, BIRCH_PLANKS.materialColor).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
public class BlockForestryFence extends FenceBlock implements IWoodTyped {

	private final boolean fireproof;
	private final IWoodType woodType;

	public BlockForestryFence(boolean fireproof, IWoodType woodType) {
		super(Block.Properties.create(Material.WOOD)
				.hardnessAndResistance(woodType.getHardness(), woodType.getHardness() * 1.5F)
				.sound(SoundType.WOOD)
				.harvestTool(ToolType.AXE)
				.harvestLevel(0));
		this.fireproof = fireproof;
		this.woodType = woodType;
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	@Override
	public IWoodType getWoodType() {
		return woodType;
	}


	@Override
	public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		if (fireproof) {
			return 0;
		}
		return 20;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		if (fireproof) {
			return 0;
		}
		return 5;
	}

	@Override
	public WoodBlockKind getBlockKind() {
		return WoodBlockKind.FENCE;
	}
}
