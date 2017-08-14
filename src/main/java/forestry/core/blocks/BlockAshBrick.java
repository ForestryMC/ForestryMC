package forestry.core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.CreativeTabForestry;

public class BlockAshBrick extends Block implements IItemModelRegister {

	public BlockAshBrick() {
		super(Material.ROCK, MapColor.STONE);
		setCreativeTab(CreativeTabForestry.tabForestry);
		setHardness(2.0F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0);
	}

}
