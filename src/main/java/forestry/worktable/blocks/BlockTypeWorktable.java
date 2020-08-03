package forestry.worktable.blocks;

import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.MachineProperties;
import forestry.core.tiles.TileForestry;
import forestry.modules.features.FeatureTileType;
import forestry.worktable.features.WorktableTiles;

import java.util.function.Supplier;

public enum BlockTypeWorktable implements IBlockType {
    WORKTABLE(() -> WorktableTiles.WORKTABLE, "worktable");

    public static final BlockTypeWorktable[] VALUES = values();

    private final IMachineProperties machineProperties;

    <T extends TileForestry> BlockTypeWorktable(Supplier<FeatureTileType<? extends T>> teClass, String name) {
        this.machineProperties = new MachineProperties.Builder<>(teClass, name).create();
    }

    @Override
    public IMachineProperties getMachineProperties() {
        return machineProperties;
    }

    @Override
    public String getString() {
        return getMachineProperties().getString();
    }
}
