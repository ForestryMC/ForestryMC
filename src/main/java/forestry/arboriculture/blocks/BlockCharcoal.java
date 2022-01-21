package forestry.arboriculture.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class BlockCharcoal extends Block {

	public BlockCharcoal() {
		super(Block.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
				.strength(5.0f, 10.0f)
				.sound(SoundType.STONE));
	}
}
