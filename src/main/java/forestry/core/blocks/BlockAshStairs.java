package forestry.core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.Item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;

public class BlockAshStairs extends StairsBlock implements IItemModelRegister {
	public BlockAshStairs(BlockState modelState) {
		super(modelState, Block.Properties.from(modelState.getBlock()));
		//		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0);
	}
}
