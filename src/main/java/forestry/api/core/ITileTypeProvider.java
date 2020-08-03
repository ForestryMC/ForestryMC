package forestry.api.core;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

public interface ITileTypeProvider<T extends TileEntity> {
    boolean hasTileType();

    @Nullable
    TileEntityType<T> getTileType();

    TileEntityType<T> tileType();


}
