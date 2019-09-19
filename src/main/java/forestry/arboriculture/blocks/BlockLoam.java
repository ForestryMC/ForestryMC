package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockLoam extends Block {

	public BlockLoam() {
		super(Block.Properties.create(Material.EARTH)
			.hardnessAndResistance(0.5f)
			.sound(SoundType.GROUND));
		//		setCreativeTab(ModuleCharcoal.getTag()); TODO creative tab
	}

}
