/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IAlyzerPlugin {

	@OnlyIn(Dist.CLIENT)
	void drawAnalyticsPage1(PoseStack transform, Screen gui, ItemStack itemStack);

	@OnlyIn(Dist.CLIENT)
	void drawAnalyticsPage2(PoseStack transform, Screen gui, ItemStack itemStack);

	@OnlyIn(Dist.CLIENT)
	void drawAnalyticsPage3(PoseStack transform, ItemStack itemStack, Screen gui);

	/**
	 * The hints that will be shown in the alyzer gui.
	 */
	List<String> getHints();

	Map<ResourceLocation, ItemStack> getIconStacks();
}
