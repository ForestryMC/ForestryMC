/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.core.ItemGroups;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.apiculture.genetics.Bee;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.arboriculture.genetics.Tree;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.config.Constants;
import forestry.core.features.CoreItems;
import forestry.lepidopterology.genetics.Butterfly;
import forestry.lepidopterology.genetics.ButterflyDefinition;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import forestry.modules.features.FeatureProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

//Provides no features but needs to be loaded
@FeatureProvider
public class ItemGroupForestry extends ItemGroup {

    public static final ItemGroup tabForestry = new ItemGroupForestry(0, Constants.MOD_ID);

    static {
        ItemGroups.tabStorage = new ItemGroupForestry(1, "storage");

        if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
            ItemGroups.tabApiculture = new ItemGroupForestry(2, "apiculture");
        }

        if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
            ItemGroups.tabArboriculture = new ItemGroupForestry(3, "arboriculture");
        }

        if (ModuleHelper.isEnabled(ForestryModuleUids.LEPIDOPTEROLOGY)) {
            ItemGroups.tabLepidopterology = new ItemGroupForestry(4, "lepidopterology");
        }
    }

    private final int icon;

    private ItemGroupForestry(int icon, String label) {
        super(label);
        this.icon = icon;
    }

    public static void create() {
        //Needed to load the groups before the feature creation
    }

    @Override
    public ItemStack getIcon() {
        Item iconItem;
        switch (icon) {
            case 1:
                if (ModuleHelper.isEnabled(ForestryModuleUids.BACKPACKS)) {
                    iconItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Constants.MOD_ID, "miner_bag"));
                } else {
                    iconItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Constants.MOD_ID, "crate"));
                }
                break;
            case 2:
                return BeeManager.beeRoot.getTypes().createStack(
                        new Bee(BeeDefinition.FOREST.getGenome()),
                        EnumBeeType.DRONE
                );
            case 3:
                return TreeManager.treeRoot.getTypes().createStack(
                        new Tree(TreeDefinition.Oak.getGenome()),
                        EnumGermlingType.SAPLING
                );
            case 4:
                return ButterflyManager.butterflyRoot.getTypes().createStack(new Butterfly(ButterflyDefinition.Brimstone
                        .getGenome()), EnumFlutterType.BUTTERFLY);
            default:
                iconItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(
                        Constants.MOD_ID,
                        "fertilizer_compound"
                ));
                break;
        }
        if (iconItem == null) {
            iconItem = CoreItems.WRENCH.item();
        }
        return new ItemStack(iconItem);
    }

    @Override
    public ItemStack createIcon() {
        return getIcon();
    }
}
