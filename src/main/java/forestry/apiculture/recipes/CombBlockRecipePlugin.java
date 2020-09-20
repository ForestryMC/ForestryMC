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
package forestry.apiculture.recipes;

import forestry.core.config.Constants;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@JeiPlugin
@OnlyIn(Dist.CLIENT)
public class CombBlockRecipePlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Constants.MOD_ID);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
//        Set<CombBlockRecipeWrapper> recipes = new HashSet<>();
//        for (int i = 0; i < EnumHoneyComb.values().length; i++) {
//            recipes.add(new CombBlockRecipeWrapper(i));
//        }
//
//        registration.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);
    }
}
