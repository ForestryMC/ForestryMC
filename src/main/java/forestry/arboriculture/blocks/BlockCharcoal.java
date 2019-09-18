package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;

public class BlockCharcoal extends Block implements IItemModelRegister {

	public BlockCharcoal() {
		super(Block.Properties.create(Material.ROCK, MaterialColor.BLACK)
				.hardnessAndResistance(5.0f, 10.0f)
				.sound(SoundType.STONE)
				.harvestTool(ToolType.PICKAXE)
				.harvestLevel(1));
	}
}
