package forestry.book.gui.buttons;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import forestry.api.book.IBookEntry;
import forestry.core.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiButtonEntry extends Button {
    public final IBookEntry entry;

    public GuiButtonEntry(int x, int y, IBookEntry entry, IPressable action) {
        super(
                x,
                y,
                Minecraft.getInstance().fontRenderer.getStringWidth(entry.getTitle().getString()) + 9,
                11,
                entry.getTitle(),
                action
        );
        this.entry = entry;
    }

    @Override
    public void render(MatrixStack transform, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.isHovered = mouseX >= this.x
                             && mouseY >= this.y
                             && mouseX < this.x + this.width
                             && mouseY < this.y + this.height;

            ITextComponent text = getMessage();
            if (isHovered) {
                ((IFormattableTextComponent) text).mergeStyle(TextFormatting.GOLD);
            } else {
                ((IFormattableTextComponent) text).mergeStyle(TextFormatting.DARK_GRAY);
            }

            boolean unicode = fontRenderer.getBidiFlag();
            //fontRenderer.setBidiFlag(true);
            fontRenderer.drawString(transform, text.getString(), this.x + 9, this.y, 0);
            //fontRenderer.setBidiFlag(unicode);

            ItemStack stack = entry.getStack();
            if (!stack.isEmpty()) {
                RenderSystem.pushMatrix();
                RenderSystem.translatef(x, y, getBlitOffset());    //TODO correct?
                //RenderHelper.enableGUIStandardItemLighting(); TODO: Gui Item Light
                GlStateManager.enableRescaleNormal();
                GlStateManager.scalef(0.5F, 0.5F, 0.5F);
                GuiUtil.drawItemStack(fontRenderer, stack, 0, 0);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popMatrix();
            }
        }
    }
}
