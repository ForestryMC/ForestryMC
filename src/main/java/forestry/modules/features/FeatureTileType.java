package forestry.modules.features;

import forestry.core.config.Constants;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Supplier;

public class FeatureTileType<T extends TileEntity> implements ITileTypeFeature<T> {
    protected final String moduleID;
    protected final String identifier;
    private final Supplier<T> constructorTileEntity;
    @Nullable
    private TileEntityType<T> tileType;
    private final Supplier<Collection<? extends Block>> validBlocks;

    public FeatureTileType(
            String moduleID,
            String identifier,
            Supplier<T> constructorTileEntity,
            Supplier<Collection<? extends Block>> validBlocks
    ) {
        this.moduleID = moduleID;
        this.identifier = identifier;
        this.constructorTileEntity = constructorTileEntity;
        this.validBlocks = validBlocks;
    }


    @Override
    public boolean hasTileType() {
        return tileType != null;
    }

    @Nullable
    @Override
    public TileEntityType<T> getTileType() {
        return tileType;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.TILE;
    }

    @Override
    public String getModId() {
        return Constants.MOD_ID;
    }

    @Override
    public String getModuleId() {
        return moduleID;
    }

    @Override
    public void setTileType(TileEntityType<T> tileType) {
        this.tileType = tileType;
    }

    @Override
    public TileEntityType.Builder<T> getTileTypeConstructor() {
        return TileEntityType.Builder.create(constructorTileEntity, validBlocks.get().toArray(new Block[0]));
    }
}
