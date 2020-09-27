package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;

public class BlockCharcoal extends Block {

    public BlockCharcoal() {
        super(Block.Properties.create(Material.ROCK, MaterialColor.BLACK)
                              .hardnessAndResistance(5.0f, 10.0f)
                              .sound(SoundType.STONE)
                              .harvestTool(ToolType.PICKAXE)
                              .harvestLevel(1));
    }
}
