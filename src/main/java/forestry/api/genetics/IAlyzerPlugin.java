/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IAlyzerPlugin {

	@OnlyIn(Dist.CLIENT)
	void drawAnalyticsPage1(MatrixStack transform, Screen gui, ItemStack itemStack);

	@OnlyIn(Dist.CLIENT)
	void drawAnalyticsPage2(MatrixStack transform, Screen gui, ItemStack itemStack);

	@OnlyIn(Dist.CLIENT)
	void drawAnalyticsPage3(MatrixStack transform, ItemStack itemStack, Screen gui);

	/**
	 * The hints that will be shown in the alyzer gui.
	 */
	List<String> getHints();

	Map<ResourceLocation, ItemStack> getIconStacks();
}
