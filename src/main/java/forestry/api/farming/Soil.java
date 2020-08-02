package forestry.api.farming;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

public final class Soil {
    private final ItemStack resource;
    private final BlockState soilState;

    public Soil(ItemStack resource, BlockState soilState) {
        this.resource = resource;
        this.soilState = soilState;
    }

    public ItemStack getResource() {
        return resource;
    }

    public BlockState getSoilState() {
        return soilState;
    }
}