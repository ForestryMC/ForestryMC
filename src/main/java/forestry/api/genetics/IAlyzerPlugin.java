/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IAlyzerPlugin {

	void drawAnalyticsPage1(GuiScreen gui, ItemStack itemStack);

	void drawAnalyticsPage2(GuiScreen gui, ItemStack itemStack);

	void drawAnalyticsPage3(GuiScreen gui, ItemStack itemStack);

	/**
	 * The hints that will be shown in the alyzer gui.
	 */
	List<String> getHints();

	Map<String, ItemStack> getIconStacks();
}
