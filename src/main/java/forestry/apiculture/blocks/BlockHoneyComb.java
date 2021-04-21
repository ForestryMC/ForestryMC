package forestry.apiculture.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.apiculture.items.EnumHoneyComb;
import forestry.core.blocks.IColoredBlock;

public class BlockHoneyComb extends Block implements IColoredBlock {
	public final EnumHoneyComb type;

	public BlockHoneyComb(EnumHoneyComb type) {
		super(Block.Properties.of(Material.WOOL)
				.strength(1F));
		this.type = type;
	}

	public EnumHoneyComb getType() {
		return type;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public int colorMultiplier(BlockState state, @Nullable IBlockReader reader, @Nullable BlockPos pos, int tintIndex) {
		EnumHoneyComb honeyComb = type;
		if (tintIndex == 1) {
			return honeyComb.primaryColor;
		} else {
			return honeyComb.secondaryColor;
		}
	}
}
