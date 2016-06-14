package forestry.factory.blocks;

import net.minecraft.item.Item;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;

public class DistillVatItemModel implements IItemModelRegister {
	private final BlockDistillVat blockDistillVat;

	public DistillVatItemModel(BlockDistillVat blockDistillVat) {
		this.blockDistillVat = blockDistillVat;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "distillvat." + blockDistillVat.getDistillVatType());
	}
}