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
package forestry.apiculture.items;

import java.util.List;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ISpriteRegister;
import forestry.api.core.ITextureManager;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.apiculture.gui.ContainerHabitatLocator;
import forestry.apiculture.gui.GuiHabitatLocator;
import forestry.apiculture.inventory.ItemInventoryHabitatLocator;
import forestry.apiculture.render.TextureHabitatLocator;
import forestry.core.items.ItemWithGui;
import forestry.core.utils.ClimateUtil;
import forestry.core.utils.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemHabitatLocator extends ItemWithGui implements ISpriteRegister {
	private static final String iconName = "forestry:items/biomefinder";

	private final HabitatLocatorLogic locatorLogic;

	public ItemHabitatLocator() {
		setCreativeTab(Tabs.tabApiculture);
		setMaxStackSize(1);
		locatorLogic = new HabitatLocatorLogic();
	}

	public HabitatLocatorLogic getLocatorLogic() {
		return locatorLogic;
	}

	@Override
	public void onUpdate(ItemStack p_77663_1_, World world, Entity player, int p_77663_4_, boolean p_77663_5_) {
		if (!world.isRemote) {
			locatorLogic.onUpdate(world, player);
		}
	}

	/* SPRITES */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerSprites(ITextureManager manager) {
		TextureAtlasSprite texture = new TextureHabitatLocator(iconName);
		Minecraft.getMinecraft().getTextureMapBlocks().setTextureEntry(texture);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {
		super.addInformation(itemstack, player, list, flag);

		Biome currentBiome = player.world.getBiome(player.getPosition());

		float temperatureValue = ClimateUtil.getTemperature(player.world, player.getPosition());
		EnumTemperature temperature = EnumTemperature.getFromValue(temperatureValue);
		EnumHumidity humidity = EnumHumidity.getFromValue(ClimateUtil.getHumidity(player.world, player.getPosition()));

		list.add(Translator.translateToLocal("for.gui.currentBiome") + ": " + currentBiome.getBiomeName());
		list.add(Translator.translateToLocal("for.gui.temperature") + ": " + AlleleManager.climateHelper.toDisplay(temperature));
		list.add(Translator.translateToLocal("for.gui.humidity") + ": " + AlleleManager.climateHelper.toDisplay(humidity));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, ItemStack heldItem, int data) {
		return new GuiHabitatLocator(player, new ItemInventoryHabitatLocator(player, heldItem));
	}

	@Override
	public Container getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		return new ContainerHabitatLocator(player, new ItemInventoryHabitatLocator(player, heldItem));
	}
}
