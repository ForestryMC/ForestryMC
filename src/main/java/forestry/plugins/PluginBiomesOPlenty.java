/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.plugins;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.farming.Farmables;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.farming.logic.FarmableGenericCrop;
import forestry.farming.logic.FarmableGenericSapling;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;


@Plugin(pluginID = "BiomesOPlenty", name = "BiomesOPlenty", author = "Nirek", url = Defaults.URL, unlocalizedDescription = "for.plugin.biomesoplenty.description")
public class PluginBiomesOPlenty extends ForestryPlugin {


    private static final String BoP = "BiomesOPlenty";

    public static Block BoPSaplings;
    public static Block BoPColorizedSaplings;
    public static Block BoPTurnip;
    public static Item BoPTurnipSeeds;


    @Override
    public boolean isAvailable() {
        return Proxies.common.isModLoaded(BoP);
    }

    @Override
    public String getFailMessage() {
        return "BiomesOPlenty not found";
    }

    @Override
    public void doInit() {
        super.doInit();

        BoPSaplings = GameRegistry.findBlock(BoP, "saplings");
        BoPColorizedSaplings = GameRegistry.findBlock(BoP, "colorizedSaplings");
        BoPTurnipSeeds = GameRegistry.findItem(BoP, "turnipSeeds");
        BoPTurnip = GameRegistry.findBlock(BoP, "turnip");

        ArrayList<String> saplingItemKeys = new ArrayList<String>();

        if (BoPSaplings != null) {
            saplingItemKeys.add("saplings");
        }
        if (BoPColorizedSaplings != null) {
            saplingItemKeys.add("colorizedSaplings");
        }

        for (String key : saplingItemKeys) {
            Item saplingItem = GameRegistry.findItem(BoP, key);
            String saplingName = GameData.getItemRegistry().getNameForObject(saplingItem);
            FMLInterModComms.sendMessage(Defaults.MOD, "add-farmable-sapling", String.format("farmArboreal@%s.-1", saplingName));
        }
        ItemStack BoPPersimmon = new ItemStack(GameRegistry.findItem(BoP,"food"),1,8);
        Farmables.farmables.get("farmArboreal").add(new FarmableGenericSapling(BoPSaplings,15, BoPPersimmon));

        Farmables.farmables.get("farmVegetables").add(new FarmableGenericCrop(new ItemStack(BoPTurnipSeeds, 1, 0), BoPTurnip, 7));
    }

    @Override
    protected void registerRecipes() {
    }

}
