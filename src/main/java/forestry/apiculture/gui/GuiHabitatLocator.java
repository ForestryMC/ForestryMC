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
package forestry.apiculture.gui;

import com.google.common.collect.LinkedListMultimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import forestry.apiculture.inventory.ItemInventoryHabitatLocator;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.render.ColourProperties;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;

import java.util.*;

public class GuiHabitatLocator extends GuiForestry<ContainerHabitatLocator> {
    private static final LinkedListMultimap<String, Biome.Category> habitats = LinkedListMultimap.create();

    static {
        habitats.putAll("ocean", Arrays.asList(Biome.Category.OCEAN, Biome.Category.BEACH));
        habitats.put("plains", Biome.Category.PLAINS);
        habitats.put("desert", Biome.Category.DESERT);
        habitats.putAll("forest", Arrays.asList(Biome.Category.FOREST, Biome.Category.RIVER));
        habitats.put("jungle", Biome.Category.JUNGLE);
        habitats.put("taiga", Biome.Category.TAIGA);
        habitats.put("hills", Biome.Category.EXTREME_HILLS);
        habitats.put("swamp", Biome.Category.SWAMP);
        habitats.put("snow", Biome.Category.ICY);
        habitats.put("mushroom", Biome.Category.MUSHROOM);
        habitats.put("nether", Biome.Category.NETHER);
        habitats.put("end", Biome.Category.THEEND);
    }

    private final ItemInventoryHabitatLocator itemInventory;
    private final List<HabitatSlot> habitatSlots = new ArrayList<>(habitats.size());

    private int startX;
    private int startY;

    public GuiHabitatLocator(ContainerHabitatLocator container, PlayerInventory playerInv, ITextComponent title) {
        super(Constants.TEXTURE_PATH_GUI + "biomefinder.png", container, playerInv, title);

        this.itemInventory = container.getItemInventory();
        xSize = 176;
        ySize = 184;

        int slot = 0;
        for (String habitatName : habitats.keySet()) {
            int x;
            int y;
            if (slot > 5) {
                x = 18 + (slot - 6) * 20;
                y = 50;
            } else {
                x = 18 + slot * 20;
                y = 32;
            }

            Collection<Biome.Category> biomes = habitats.get(habitatName);
            HabitatSlot habitatSlot = new HabitatSlot(widgetManager, x, y, habitatName, biomes);
            habitatSlots.add(habitatSlot);
            widgetManager.add(habitatSlot);
            slot++;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int mouseY, int mouseX) {
        super.drawGuiContainerBackgroundLayer(transform, partialTicks, mouseY, mouseX);

        ITextComponent str = new TranslationTextComponent("item.forestry.habitat_locator");
        getFontRenderer().func_243248_b(
                transform,
                str,
                startX + 8 + textLayout.getCenteredOffset(str, 138),
                startY + 16,
                ColourProperties.INSTANCE.get("gui.screen")
        );

        // Set active according to valid biomes.
        Set<Biome.Category> activeBiomeTypes = new HashSet<>();
        for (Biome biome : itemInventory.getBiomesToSearch()) {
            Set<Biome.Category> biomeCategories = new HashSet<Biome.Category>(Arrays.asList(Biome.Category.values()));
            activeBiomeTypes.addAll(biomeCategories);
        }

        for (HabitatSlot habitatSlot : habitatSlots) {
            habitatSlot.setActive(activeBiomeTypes);
        }

        for (HabitatSlot slot : habitatSlots) {
            slot.draw(transform, startY, startX);
        }

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f); // Reset afterwards.
    }

    @Override
    public void init() {
        super.init();

        startX = (this.width - this.xSize) / 2;
        startY = (this.height - this.ySize) / 2;
    }

    @Override
    protected void addLedgers() {
        addErrorLedger(itemInventory);
        addHintLedger("habitat.locator");
    }
}
