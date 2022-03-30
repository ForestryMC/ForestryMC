package forestry.arboriculture.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.IWoodTyped;

public class BlockForestryPlank extends Block implements IWoodTyped {

	public static Properties createWoodProperties(IWoodType woodType) {
		return Block.Properties.of(Material.WOOD)
				.strength(woodType.getHardness(), woodType.getHardness() * 1.5F)
				.sound(SoundType.WOOD);
	}

	private final boolean fireproof;
	private final IWoodType woodType;

	public BlockForestryPlank(boolean fireproof, IWoodType woodType) {
		super(createWoodProperties(woodType));
		this.fireproof = fireproof;
		this.woodType = woodType;
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
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (fireproof) {
			return 0;
		}
		return 20;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (fireproof) {
			return 0;
		}
		return 5;
	}

}
