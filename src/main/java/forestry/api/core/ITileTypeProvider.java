package forestry.api.core;

import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public interface ITileTypeProvider<T extends TileEntity> {
    boolean hasTileType();

    @Nullable
    TileEntityType<T> getTileType();

    TileEntityType<T> tileType();


}
