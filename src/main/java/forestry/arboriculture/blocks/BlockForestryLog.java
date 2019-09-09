package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LogBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.IWoodTyped;

//eg    public static final Block BIRCH_LOG = register("birch_log", new LogBlock(MaterialColor.SAND, Block.Properties.create(Material.WOOD, MaterialColor.QUARTZ).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
//TODO stripped logs    public static final Block STRIPPED_BIRCH_LOG = register("stripped_birch_log", new LogBlock(MaterialColor.SAND, Block.Properties.create(Material.WOOD, MaterialColor.SAND).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
//worst part is probably textures
public class BlockForestryLog extends LogBlock implements IWoodTyped {

	private final boolean fireproof;
	private final IWoodType woodType;

	protected BlockForestryLog(boolean fireproof, IWoodType woodType) {
		super(MaterialColor.WOOD, Block.Properties.create(Material.WOOD)
			.hardnessAndResistance(woodType.getHardness(), woodType.getHardness() * 1.5F)
			.sound(SoundType.WOOD));
		this.fireproof = fireproof;
		this.woodType = woodType;

		//		setHarvestLevel("axe", 0);	TODO harvest level
		//		setCreativeTab(Tabs.tabArboriculture);	TODO creative tab
	}


	@Override
	public final WoodBlockKind getBlockKind() {
		return WoodBlockKind.LOG;
	}

	@Override
	public final boolean isFireproof() {
		return fireproof;
	}

	@Override
	public IWoodType getWoodType() {
		return woodType;
	}

	/* PROPERTIES */
	@Override
	public final int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		if (fireproof) {
			return 0;
		} else if (face == Direction.DOWN) {
			return 20;
		} else if (face != Direction.UP) {
			return 10;
		} else {
			return 5;
		}
	}

	@Override
	public final int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		if (fireproof) {
			return 0;
		}
		return 5;
	}
}
