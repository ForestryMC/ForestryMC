package forestry.cultivation.tiles;

import net.minecraft.item.ItemStack;

import forestry.core.ModuleCore;

public class TileBog extends TilePlanter {
	public TileBog() {
		super("farmPeat");
	}

	@Override
	public ItemStack[] createGermlingStacks() {
		return new ItemStack[0];
	}

	@Override
	public ItemStack[] createResourceStacks() {
		return new ItemStack[]{
			new ItemStack(ModuleCore.getBlocks().bogEarth),
			new ItemStack(ModuleCore.getBlocks().bogEarth),
			new ItemStack(ModuleCore.getBlocks().bogEarth),
			new ItemStack(ModuleCore.getBlocks().bogEarth)
		};
	}

	@Override
	public ItemStack[] createProductionStacks() {
		return new ItemStack[]{
			new ItemStack(ModuleCore.getItems().peat),
			new ItemStack(ModuleCore.getItems().peat),
			new ItemStack(ModuleCore.getItems().peat),
			new ItemStack(ModuleCore.getItems().peat)
		};
	}
}
