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

import java.util.ArrayList;
import java.util.List;

import forestry.api.farming.IFarmable;
import forestry.core.circuits.Circuit;
import forestry.core.config.Config;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.logic.FarmLogicEnder;
import forestry.farming.logic.FarmLogicRubber;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.farming.Farmables;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.farming.logic.FarmableGenericCrop;
import forestry.farming.logic.FarmableGenericSapling;

@Plugin(pluginID = "ExtraUtilities", name = "ExtraUtilities", author = "Nirek", url = Defaults.URL, unlocalizedDescription = "for.plugin.extrautilities.description")
public class PluginExtraUtilities extends ForestryPlugin {

    private static final String ExU = "ExtraUtilities";

    public static Block ExUEnderLilly;

    @Override
    public boolean isAvailable() {
        return Proxies.common.isModLoaded(ExU);
    }

    @Override
    public String getFailMessage() {
        return "ExtraUtilities not found";
    }

    @Override
    public void doInit() {
        super.doInit();

        ExUEnderLilly = GameRegistry.findBlock(ExU, "plant/ender_lilly");
        Farmables.farmables.put("farmEnder", new ArrayList<IFarmable>());
        if(Config.isExUtilEnderLilyEnabled()) {
            Circuit.farmEnderManaged = new CircuitFarmLogic("managedEnder", FarmLogicEnder.class);
            Farmables.farmables.get("farmEnder").add(new FarmableGenericCrop(new ItemStack(ExUEnderLilly, 1, 0), ExUEnderLilly, 7));
        }
    }
}
