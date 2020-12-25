/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.fuels;

import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class MoistenerFuel {
    private final Ingredient resource;
    private final ItemStack product;
    private final int moistenerValue;
    private final int stage;

    public MoistenerFuel(Ingredient resource, ItemStack product, int stage, int moistenerValue) {
        Preconditions.checkNotNull(resource);
        Preconditions.checkNotNull(product);
        Preconditions.checkArgument(!resource.hasNoMatchingItems());
        Preconditions.checkArgument(!product.isEmpty());
        this.resource = resource;
        this.product = product;
        this.stage = stage;
        this.moistenerValue = moistenerValue;
    }

    /**
     * The item to use
     */
    public Ingredient getResource() {
        return resource;
    }

    /**
     * The item that leaves the moistener's working slot (i.e. mouldy wheat, decayed wheat, mulch)
     */
    public ItemStack getProduct() {
        return product;
    }

    /**
     * How much this item contributes to the final product of the moistener (i.e. mycelium)
     */
    public int getMoistenerValue() {
        return moistenerValue;
    }

    /**
     * What stage this product represents. Resources with lower stage value will be consumed first.
     */
    public int getStage() {
        return stage;
    }
}
