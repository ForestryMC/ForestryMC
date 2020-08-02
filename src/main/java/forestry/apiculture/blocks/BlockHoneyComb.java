package forestry.apiculture.blocks;

import forestry.apiculture.items.EnumHoneyComb;
import forestry.core.blocks.IColoredBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockHoneyComb extends Block implements IColoredBlock {
    public final EnumHoneyComb type;

    public BlockHoneyComb(EnumHoneyComb type) {
        super(Block.Properties.create(Material.WOOL)
                .hardnessAndResistance(1F));
        this.type = type;
    }

    public EnumHoneyComb getType() {
        return type;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int colorMultiplier(BlockState state, IBlockReader reader, BlockPos pos, int tintIndex) {
        EnumHoneyComb honeyComb = type;
        if (tintIndex == 1) {
            return honeyComb.primaryColor;
        } else {
            return honeyComb.secondaryColor;
        }
    }
}
