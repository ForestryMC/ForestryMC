package forestry.book.gui.buttons;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import forestry.api.book.IBookCategory;
import forestry.api.core.tooltips.IToolTipProvider;
import forestry.api.core.tooltips.ToolTip;
import forestry.core.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiButtonBookCategory extends Button implements IToolTipProvider {
    public final IBookCategory category;

    public GuiButtonBookCategory(int x, int y, IBookCategory category, IPressable action) {
        super(x, y, 32, 32, null, action);
        this.category = category;
    }

    @Override
    public void render(MatrixStack transform, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(x, y, getBlitOffset());    //TODO correct?
            GlStateManager.scalef(2F, 2F, 2F);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            //RenderHelper.enableGUIStandardItemLighting(); TODO: Gui Item Light
            GlStateManager.enableRescaleNormal();
            GuiUtil.drawItemStack(fontRenderer, category.getStack(), 0, 0);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public ToolTip getToolTip(int mouseX, int mouseY) {
        ToolTip toolTip = new ToolTip();
        toolTip.add(category.getLocalizedName());
        return toolTip;
    }

    @Override
    public boolean isToolTipVisible() {
        return true;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    @Override
    public boolean isRelativeToGui() {
        return false;
    }
}
