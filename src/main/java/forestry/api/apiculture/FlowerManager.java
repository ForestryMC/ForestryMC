/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.apiculture;

import forestry.api.genetics.flowers.IFlowerRegistry;

public class FlowerManager {
    public static final String FlowerTypeVanilla = "flowersVanilla";
    public static final String FlowerTypeNether = "flowersNether";
    public static final String FlowerTypeCacti = "flowersCacti";
    public static final String FlowerTypeMushrooms = "flowersMushrooms";
    public static final String FlowerTypeEnd = "flowersEnd";
    public static final String FlowerTypeJungle = "flowersJungle";
    public static final String FlowerTypeSnow = "flowersSnow";
    public static final String FlowerTypeWheat = "flowersWheat";
    public static final String FlowerTypeGourd = "flowersGourd";
    /**
     * <blockquote><pre>e.g. FlowerManager.flowerRegister.registerPlantableFlower(new ItemStack(Blocks.RED_FLOWER), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);</pre></blockquote>
     */
    public static IFlowerRegistry flowerRegistry;

}
