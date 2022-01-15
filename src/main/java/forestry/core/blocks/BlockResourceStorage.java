package forestry.core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import net.minecraftforge.common.ToolType;

public class BlockResourceStorage extends Block {
	private EnumResourceType type;

	public BlockResourceStorage(EnumResourceType type) {
		super(Block.Properties.of(Material.METAL)
				.strength(3f, 5f)
				.harvestTool(ToolType.PICKAXE)
				.harvestLevel(0)
				.sound(SoundType.METAL));

		this.type = type;
	}

	public EnumResourceType getType() {
		return this.type;
	}
}
