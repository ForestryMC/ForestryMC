package forestry.core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BlockResourceStorage extends Block {
    private final EnumResourceType type;

    public BlockResourceStorage(EnumResourceType type) {
        super(Block.Properties.create(Material.IRON)
                              .hardnessAndResistance(3f, 5f)
                              .harvestTool(ToolType.PICKAXE)
                              .harvestLevel(0));
        this.type = type;
    }

    public EnumResourceType getType() {
        return this.type;
    }
}
