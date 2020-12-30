/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.farming;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;

public interface IFarmRegistry {
    /**
     * Registers farming logic in registry under given identifier
     *
     * @param identifier Valid identifiers: farmArboreal farmCrops farmGourd farmInfernal farmPoales farmSucculentes farmShroom
     */
    IFarmPropertiesBuilder getPropertiesBuilder(String identifier);

    /**
     * Can be used to add IFarmables to some of the vanilla farm logics.
     * <p>
     * Identifiers: farmArboreal farmCrops farmGourd farmInfernal farmPoales farmSucculentes farmShroom
     */
    void registerFarmables(String identifier, IFarmable... farmable);

    Collection<IFarmable> getFarmables(String identifier);

    IFarmableInfo getFarmableInfo(String identifier);

    /**
     * @param itemStack the fertilizer itemstack
     * @param value     The value of the fertilizer. The value of the forestry fertilizer is 500.
     */
    void registerFertilizer(ItemStack itemStack, int value);

    /**
     * @return The value of the fertilizer
     */
    int getFertilizeValue(ItemStack itemStack);

    /**
     * @since Forestry 5.8
     */
    @Nullable
    IFarmProperties getProperties(String identifier);

}
