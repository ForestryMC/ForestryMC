package forestry.apiculture.blocks;

import net.minecraft.item.Item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;

public class AlvearyItemModel implements IItemModelRegister {
	private final BlockAlveary blockAlveary;

	public AlvearyItemModel(BlockAlveary blockAlveary) {
		this.blockAlveary = blockAlveary;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "apiculture/alveary." + blockAlveary.getAlvearyType());
	}
}