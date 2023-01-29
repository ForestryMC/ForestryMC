/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser Public License v3 which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to: SirSengir (original work), CovertJaguar, Player, Binnie,
 * MysteriousAges
 ******************************************************************************/
package forestry.core.items;

import java.awt.Color;

import net.minecraft.item.ItemStack;

import forestry.api.recipes.RecipeManagers;
import forestry.core.fluids.Fluids;
import forestry.core.recipes.RecipeUtil;
import forestry.plugins.PluginCore;

public enum EnumElectronTube implements ItemOverlay.IOverlayInfo {

    COPPER(new Color(0xe3b78e)),
    TIN(new Color(0xE6F8FF)),
    BRONZE(new Color(0xddc276)),
    IRON(new Color(0xCCCCCC)),
    GOLD(new Color(0xffff8b)),
    DIAMOND(new Color(0x8CF5E3)),
    OBSIDIAN(new Color(0x866bc0)),
    BLAZE(new Color(0xd96600), new Color(0xFFF87E)),
    RUBBER(new Color(0x444444)),
    EMERALD(new Color(0x00CC41)),
    APATITE(new Color(0x579CD9)),
    LAPIS(new Color(0x1c57c6)),
    ENDER(new Color(0x33adad), new Color(0x255661)),
    URANIUM(new Color(0xFF00CC41), new Color(0x33adad));

    public static final EnumElectronTube[] VALUES = values();

    private final String name;
    private final int primaryColor;
    private final int secondaryColor;

    EnumElectronTube(Color secondaryColor) {
        this(secondaryColor, Color.WHITE);
    }

    EnumElectronTube(Color secondaryColor, Color primaryColor) {
        this.name = "ex-" + ordinal();
        this.primaryColor = primaryColor.getRGB();
        this.secondaryColor = secondaryColor.getRGB();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPrimaryColor() {
        return primaryColor;
    }

    @Override
    public int getSecondaryColor() {
        return secondaryColor;
    }

    @Override
    public boolean isSecret() {
        return false;
    }

    public void registerTubeRecipe(ItemStack stack) {
        if (stack != null) {
            for (Object stackOreDict : RecipeUtil.getOreDictRecipeEquivalents(stack)) {
                RecipeManagers.fabricatorManager.addRecipe(
                        null,
                        Fluids.GLASS.getFluid(500),
                        PluginCore.items.tubes.get(this, 4),
                        new Object[] { " X ", "#X#", "XXX", '#', "dustRedstone", 'X', stackOreDict });
            }
        }
    }

    public void registerTubeRecipe(String stack) {
        RecipeManagers.fabricatorManager.addRecipe(
                null,
                Fluids.GLASS.getFluid(500),
                PluginCore.items.tubes.get(this, 4),
                new Object[] { " X ", "#X#", "XXX", '#', "dustRedstone", 'X', stack });
    }
}
