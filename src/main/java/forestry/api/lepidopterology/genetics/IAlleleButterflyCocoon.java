/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.lepidopterology.genetics;

import forestry.api.genetics.alleles.IAlleleProperty;
import forestry.api.genetics.products.IDynamicProductList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public interface IAlleleButterflyCocoon extends IAlleleProperty<IAlleleButterflyCocoon> {

    ModelResourceLocation getCocoonItemModel(int age);

    String getCocoonName();

    IDynamicProductList getCocoonLoot();

    void clearLoot();

    void bakeLoot();

    void addLoot(ItemStack loot, float chance);
}
