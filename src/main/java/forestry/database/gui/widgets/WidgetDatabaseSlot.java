package forestry.database.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import forestry.api.core.tooltips.ToolTip;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.utils.ItemTooltipUtil;
import forestry.core.utils.NetworkUtil;
import forestry.database.DatabaseItem;
import forestry.database.gui.GuiDatabase;
import forestry.database.network.packets.PacketExtractItem;
import forestry.database.network.packets.PacketInsertItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class WidgetDatabaseSlot extends Widget {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(
            Constants.MOD_ID,
            Constants.TEXTURE_PATH_GUI + "database_inventory.png"
    );

    public static final Drawable SLOT = new Drawable(TEXTURE_LOCATION, 218, 0, 22, 22);
    public static final Drawable SLOT_SELECTED = new Drawable(TEXTURE_LOCATION, 218, 22, 22, 22);

    private int xPos;
    private int yPos;
    private boolean isEmpty;
    private int databaseIndex;
    private boolean ignoreMouseUp = false;
    private boolean mouseOver;

    public WidgetDatabaseSlot(WidgetManager manager) {
        super(manager, 0, 0);
        this.databaseIndex = -1;
        this.isEmpty = false;
    }

    public void update(int xPos, int yPos, int databaseIndex, boolean isEmpty) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.databaseIndex = databaseIndex;
        this.isEmpty = isEmpty;
    }

    public int getDatabaseIndex() {
        return databaseIndex;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= xPos && mouseX <= xPos + this.width && mouseY >= yPos && mouseY <= yPos + this.height;
    }

    @Override
    public void draw(MatrixStack transform, int startY, int startX) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        manager.minecraft.textureManager.bindTexture(TEXTURE_LOCATION);
        Drawable texture = SLOT;
        if (isSelected()) {
            texture = SLOT_SELECTED;
        }
        texture.draw(transform, startY + yPos - 3, startX + xPos - 3);
        ItemStack itemStack = getItemStack();
        if (!itemStack.isEmpty()) {
            Minecraft minecraft = Minecraft.getInstance();
            TextureManager textureManager = minecraft.getTextureManager();
            textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            //RenderHelper.enableGUIStandardItemLighting(); TODO Gui Light
            GuiUtil.drawItemStack(manager.gui, itemStack, startX + xPos, startY + yPos);
            RenderHelper.disableStandardItemLighting();
        }
        if (mouseOver) {
            drawMouseOver();
        }
    }

    private void drawMouseOver() {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        //		manager.gui.blit(xPos, yPos, xPos + width, yPos + height, -2130706433, -2130706433);	TODO still not quite right
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    @Override
    public void update(int mouseX, int mouseY) {
        mouseOver = isMouseOver(mouseX, mouseY);
    }

    @Override
    public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton != 0 && mouseButton != 1 && mouseButton != 2 ||
            !manager.minecraft.player.inventory.getItemStack().isEmpty()) {
            return;
        }
        GuiDatabase gui = (GuiDatabase) manager.gui;
        DatabaseItem item = gui.getItem(databaseIndex);
        if (item == null) {
            return;
        }

        if (Screen.hasControlDown() && mouseButton == 0) {
            gui.analyzer.setSelectedSlot(databaseIndex);
            return;
        }

        ignoreMouseUp = true;
        byte flags = 0;
        if (Screen.hasShiftDown()) {
            flags |= PacketExtractItem.SHIFT;
        }
        if (mouseButton == 1) {
            flags |= PacketExtractItem.HALF;
        } else if (mouseButton == 2 && manager.minecraft.player.isCreative()) {
            flags |= PacketExtractItem.CLONE;
        }
        NetworkUtil.sendToServer(new PacketExtractItem(item.invIndex, flags));
    }

    @Override
    public boolean handleMouseRelease(double mouseX, double mouseY, int eventType) {
        if (!isMouseOver(mouseX, mouseY)
            || ignoreMouseUp
            || eventType != 0 && eventType != 1
            || manager.minecraft.player.inventory.getItemStack().isEmpty()) {
            ignoreMouseUp = false;
            return false;
        }
        GuiDatabase gui = (GuiDatabase) manager.gui;
        DatabaseItem item = gui.getItem(databaseIndex);
        if (item == null) {
            return false;
        }

        NetworkUtil.sendToServer(new PacketInsertItem(eventType == 1));
        return true;
    }

    @Nullable
    @Override
    public ToolTip getToolTip(int mouseX, int mouseY) {
        ItemStack itemStack = getItemStack();
        ToolTip tip = new ToolTip();
        if (!itemStack.isEmpty()) {
            tip.addAll(ItemTooltipUtil.getInformation(itemStack));
        }
        return tip;
    }

    public boolean isSelected() {
        if (!isEmpty) {
            return false;
        }
        DatabaseItem slotItem = ((GuiDatabase) manager.gui).getItem(databaseIndex);
        DatabaseItem selectedItem = ((GuiDatabase) manager.gui).getSelectedItem();
        return slotItem != null && slotItem.equals(selectedItem);
    }

    public ItemStack getItemStack() {
        if (!isEmpty) {
            return ItemStack.EMPTY;
        }
        DatabaseItem item = ((GuiDatabase) manager.gui).getItem(databaseIndex);
        if (item == null) {
            return ItemStack.EMPTY;
        }
        return item.itemStack;
    }
}
