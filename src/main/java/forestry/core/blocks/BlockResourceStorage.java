package forestry.core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;

public class BlockResourceStorage extends Block implements IItemModelRegister {
	private EnumResourceType type;

	public BlockResourceStorage(EnumResourceType type) {
		super(Block.Properties.create(Material.IRON)
			.hardnessAndResistance(3f, 5f));
		this.type = type;
		//		setCreativeTab(CreativeTabForestry.tabForestry);	TODO done in item.
	}

	//TODO needed?
	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (EnumResourceType resourceType : EnumResourceType.VALUES) {
			manager.registerItemModel(item, resourceType.getMeta(), "storage/" + resourceType.getName());
		}
	}

	public EnumResourceType getType() {
		return this.type;
	}
}
