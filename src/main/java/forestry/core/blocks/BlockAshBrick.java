package forestry.core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;

public class BlockAshBrick extends Block implements IItemModelRegister {

	public BlockAshBrick() {
		super(Block.Properties.create(Material.ROCK, MaterialColor.STONE)
			.hardnessAndResistance(2.0f, 10.0f)
			.sound(SoundType.STONE));
		//setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0);
	}

}
