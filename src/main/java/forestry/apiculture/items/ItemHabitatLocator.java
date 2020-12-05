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

import forestry.api.core.*;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.apiculture.gui.ContainerHabitatLocator;
import forestry.apiculture.inventory.ItemInventoryHabitatLocator;
import forestry.core.items.ItemWithGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemHabitatLocator extends ItemWithGui implements ISpriteRegister {
    private static final String iconName = "forestry:items/biomefinder";

    private final HabitatLocatorLogic locatorLogic;

    public ItemHabitatLocator() {
        super((new Item.Properties()).group(ItemGroups.tabApiculture).maxStackSize(1));
        locatorLogic = new HabitatLocatorLogic();
    }

    public HabitatLocatorLogic getLocatorLogic() {
        return locatorLogic;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity player, int slot, boolean selected) {
        if (!world.isRemote) {
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
    public void addInformation(
            ItemStack itemstack,
            @Nullable World world,
            List<ITextComponent> list,
            ITooltipFlag flag
    ) {
        super.addInformation(itemstack, world, list, flag);

        Minecraft minecraft = Minecraft.getInstance();
        if (world != null && minecraft.player != null) {
            ClientPlayerEntity player = minecraft.player;
            Biome currentBiome = player.world.getBiome(player.getPosition());

            EnumTemperature temperature = EnumTemperature.getFromBiome(currentBiome, player.getPosition());
            EnumHumidity humidity = EnumHumidity.getFromValue(currentBiome.getDownfall());

            list.add(
                    new TranslationTextComponent("for.gui.currentBiome")
                            .append(new StringTextComponent(": "))
                            .append(new TranslationTextComponent(
                                    "biome." + currentBiome.getRegistryName().getNamespace() + "." +
                                    currentBiome.getRegistryName().getPath()
                            ))
            );

            list.add(new TranslationTextComponent("for.gui.temperature")
                    .append(new StringTextComponent(": "))
                    .append(AlleleManager.climateHelper.toDisplay(temperature)));

            list.add(new TranslationTextComponent("for.gui.humidity")
                    .append(new StringTextComponent(": "))
                    .append(AlleleManager.climateHelper.toDisplay(humidity)));
        }
    }

    @Override
    public Container getContainer(int windowId, PlayerEntity player, ItemStack heldItem) {
        return new ContainerHabitatLocator(windowId, player, new ItemInventoryHabitatLocator(player, heldItem));
    }
}
