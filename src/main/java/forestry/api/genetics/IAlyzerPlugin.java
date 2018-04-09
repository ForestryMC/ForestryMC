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

import forestry.api.gui.IElementGenetic;

public interface IAlyzerPlugin {

	@SideOnly(Side.CLIENT)
	void drawAnalyticsPage1(GuiScreen gui, ItemStack itemStack);

	@SideOnly(Side.CLIENT)
	void drawAnalyticsPage2(GuiScreen gui, ItemStack itemStack);

	@SideOnly(Side.CLIENT)
	void drawAnalyticsPage3(GuiScreen gui, ItemStack itemStack);

	/**
	 * Creates the first page of the alyzer.
	 *
	 * @param gui A instance of the alyzer gui.
	 * @param container A helper to create the gui elements.
	 * @param individual The individual that is currently in the alyzer slot.
	 */
	default void createFirstPage(GuiScreen gui, IElementGenetic container, IIndividual individual){
	}

	/**
	 * Creates the second page of the alyzer.
	 *
	 * @param gui A instance of the alyzer gui.
	 * @param container A helper to create the gui elements.
	 * @param individual The individual that is currently in the alyzer slot.
	 */
	default void createSecondPage(GuiScreen gui, IElementGenetic container, IIndividual individual){
	}

	/**
	 * Creates the third page of the alyzer. This page is usually used to display the products of the individual.
	 *
	 * @param gui A instance of the alyzer gui.
	 * @param container A helper to create the gui elements.
	 * @param individual The individual that is currently in the alyzer slot.
	 */
	default void createThirdPage(GuiScreen gui, IElementGenetic container, IIndividual individual){
	}

	Map<String, ItemStack> getIconStacks();

	/**
	 * The hints that will be shown in the alyzer gui.
	 */
	List<String> getHints();
}
