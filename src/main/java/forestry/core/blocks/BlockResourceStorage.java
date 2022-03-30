package forestry.core.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

public class BlockResourceStorage extends Block {
	private EnumResourceType type;

	public BlockResourceStorage(EnumResourceType type) {
		super(Block.Properties.of(Material.METAL).strength(3f, 5f));
		this.type = type;
	}

	public EnumResourceType getType() {
		return this.type;
	}
}
