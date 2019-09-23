package forestry.modules.features;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;

import net.minecraftforge.registries.IForgeRegistryEntry;

public enum FeatureType {
	MACHINE(Block.class), FLUID(Fluid.class), BLOCK(Block.class), ITEM(Item.class), TILE(TileEntityType.class);

	public final Class<? extends IForgeRegistryEntry> superType;

	FeatureType(Class<? extends IForgeRegistryEntry> superType) {
		this.superType = superType;
	}
}
