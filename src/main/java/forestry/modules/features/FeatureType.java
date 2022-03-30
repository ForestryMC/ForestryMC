package forestry.modules.features;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraftforge.registries.IForgeRegistryEntry;

public enum FeatureType {
	MACHINE(Block.class),
	FLUID(Fluid.class),
	BLOCK(Block.class),
	ENTITY(EntityType.class),
	ITEM(Item.class),
	TILE(BlockEntityType.class),
	CONTAINER(MenuType.class);

	public final Class<? extends IForgeRegistryEntry> superType;

	FeatureType(Class<? extends IForgeRegistryEntry> superType) {
		this.superType = superType;
	}
}
