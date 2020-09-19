package forestry.cultivation.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import forestry.api.core.tooltips.ToolTip;
import forestry.api.farming.FarmDirection;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.render.ColourProperties;
import forestry.cultivation.inventory.InventoryPlanter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.Locale;

public class GhostItemStackWidget extends ItemStackWidget {
    private final Slot slot;

    public GhostItemStackWidget(WidgetManager widgetManager, int xPos, int yPos, ItemStack itemStack, Slot slot) {
        super(widgetManager, xPos, yPos, itemStack);
        this.slot = slot;
    }

    @Override
    public void draw(MatrixStack transform, int startY, int startX) {
        if (!slot.getHasStack()) {
            super.draw(transform, startY, startX);
        }
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        ITextComponent directionString = getDirectionString();
        if (!directionString.getString().isEmpty()) {
            FontRenderer fontRenderer = manager.minecraft.fontRenderer;
            fontRenderer.func_238407_a_(
                    transform,
                    getDirectionString(),
                    xPos + startX + 5,
                    yPos + startY + 4,
                    ColourProperties.INSTANCE.get("gui.screen")
            );
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.5F);

        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bindTexture(manager.gui.textureFile);
        manager.gui.blit(transform, xPos + startX, yPos + startY, 206, 0, 16, 16);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableLighting();
    }

    private ITextComponent getDirectionString() {
        if (slot.getSlotIndex() >= InventoryPlanter.SLOT_PRODUCTION_1 || slot.getSlotIndex() < InventoryPlanter.SLOT_RESOURCES_1 + InventoryPlanter.SLOT_RESOURCES_COUNT) {
            return new StringTextComponent("");
        }

        int index = slot.getSlotIndex() % 4;
        FarmDirection direction = FarmDirection.values()[index];
        String directionString = direction.toString().toLowerCase(Locale.ENGLISH);
        return new TranslationTextComponent("for.gui.planter." + directionString);
    }

    @Nullable
    @Override
    public ToolTip getToolTip(int mouseX, int mouseY) {
        return null;
    }
}
