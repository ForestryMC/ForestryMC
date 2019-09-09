package forestry.apiculture.blocks;

import net.minecraft.item.Item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;

//TODO unused
public class AlvearyItemModel implements IItemModelRegister {
	private final BlockAlveary blockAlveary;

	public AlvearyItemModel(BlockAlveary blockAlveary) {
		this.blockAlveary = blockAlveary;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "apiculture/alveary." + blockAlveary.getType());
	}
}