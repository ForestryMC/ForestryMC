package forestry.core.tiles;

import java.util.Collection;
import java.util.Locale;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import net.minecraftforge.registries.ForgeRegistries;

import forestry.core.utils.Log;

public abstract class TileRegistry {

	public <T extends TileEntity> TileEntityType<T> registerTileEntityType(TileEntityType<T> tileEntityType, String name) {

		if (!name.equals(name.toLowerCase(Locale.ENGLISH))) {
			Log.error("Name must be lowercase");
		}

		tileEntityType.setRegistryName(name);
		ForgeRegistries.TILE_ENTITIES.register(tileEntityType);

		return tileEntityType;
	}

	protected <T extends TileEntity> TileEntityType<T> registerTileEntityType(Supplier<T> supplier, String name, Block... validBlocks) {
		return registerTileEntityType(TileEntityType.Builder.create(supplier, validBlocks).build(null), name);
	}

	protected <T extends TileEntity> TileEntityType<T> registerTileEntityType(Supplier<T> supplier, String name, Collection<? extends Block> validBlocks) {
		return registerTileEntityType(supplier, name, validBlocks.toArray(new Block[0]));
	}
}
