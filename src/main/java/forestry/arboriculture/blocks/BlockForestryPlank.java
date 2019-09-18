package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.IWoodTyped;

//eg    public static final Block OAK_PLANKS = register("oak_planks", new Block(Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
public class BlockForestryPlank extends Block implements IWoodTyped {

	private final boolean fireproof;
	private final IWoodType woodType;

	public BlockForestryPlank(boolean fireproof, IWoodType woodType) {
		super(Block.Properties.create(Material.WOOD)
			.hardnessAndResistance(woodType.getHardness(), woodType.getHardness() * 1.5F)
			.sound(SoundType.WOOD));
		this.fireproof = fireproof;
		this.woodType = woodType;

		//		setHarvestLevel("axe", 0);	TODO harvest level
		//		setCreativeTab(Tabs.tabArboriculture); TODO creative tab
	}

	public IWoodType getWoodType() {
		return woodType;
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	@Override
	public WoodBlockKind getBlockKind() {
		return WoodBlockKind.PLANKS;
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
}
