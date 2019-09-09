package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;

public class BlockLoam extends Block implements IItemModelRegister {

	public BlockLoam() {
		super(Block.Properties.create(Material.EARTH)
			.hardnessAndResistance(0.5f)
			.sound(SoundType.GROUND));
		//		setCreativeTab(ModuleCharcoal.getTag()); TODO creative tab
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0);
	}

}
