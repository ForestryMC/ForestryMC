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

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ISpriteRegister;
import forestry.api.core.ISpriteRegistry;
import forestry.api.core.ItemGroups;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.apiculture.gui.ContainerHabitatLocator;
import forestry.apiculture.inventory.ItemInventoryHabitatLocator;
import forestry.core.items.ItemWithGui;

public class ItemHabitatLocator extends ItemWithGui implements ISpriteRegister {
	private static final String iconName = "forestry:items/biomefinder";

	private final HabitatLocatorLogic locatorLogic;

	public ItemHabitatLocator() {
		super((new Item.Properties()).tab(ItemGroups.tabApiculture).stacksTo(1));
		locatorLogic = new HabitatLocatorLogic();
	}

	public HabitatLocatorLogic getLocatorLogic() {
		return locatorLogic;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity player, int slot, boolean selected) {
		if (!world.isClientSide) {
			locatorLogic.onUpdate(world, player);
		}
	}

	/* SPRITES */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerSprites(ISpriteRegistry registry) {
		//TextureAtlasSprite texture = new TextureHabitatLocator(iconName);
		//		Minecraft.getInstance().getTextureMap().setTextureEntry(texture);
		//TODO textures
	}


	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack itemstack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		Minecraft minecraft = Minecraft.getInstance();

		if (world != null && minecraft.player != null) {
			LocalPlayer player = minecraft.player;
			Biome currentBiome = player.level.getBiome(player.blockPosition()).value();

			EnumTemperature temperature = EnumTemperature.getFromBiome(currentBiome, player.blockPosition());
			EnumHumidity humidity = EnumHumidity.getFromValue(currentBiome.getDownfall());

			list.add(Component.translatable("for.gui.currentBiome")
					.append(Component.literal(": "))
					.append(Component.translatable("biome." + currentBiome.getRegistryName().toString().replace(":", "."))));

			list.add(Component.translatable("for.gui.temperature")
					.append(Component.literal(": "))
					.append(AlleleManager.climateHelper.toDisplay(temperature)));

			list.add(Component.translatable("for.gui.humidity")
				.append(Component.literal(": "))
				.append(AlleleManager.climateHelper.toDisplay(humidity)));
		}
	}

	@Override
	public AbstractContainerMenu getContainer(int windowId, Player player, ItemStack heldItem) {
		return new ContainerHabitatLocator(windowId, player, new ItemInventoryHabitatLocator(player, heldItem));
	}
}
