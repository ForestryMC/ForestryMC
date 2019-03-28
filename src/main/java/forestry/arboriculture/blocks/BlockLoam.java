package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.arboriculture.ModuleCharcoal;

public class BlockLoam extends Block implements IItemModelRegister {

	public BlockLoam() {
		super(Material.GROUND);
		setHardness(0.5F);
		setSoundType(SoundType.GROUND);
		setCreativeTab(ModuleCharcoal.getTag());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0);
	}

}
