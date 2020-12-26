package forestry.book.gui.buttons;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import forestry.api.core.tooltips.IToolTipProvider;
import forestry.api.core.tooltips.ToolTip;
import forestry.book.gui.GuiForesterBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class GuiButtonPage extends Button implements IToolTipProvider {
    public final boolean left;

    public GuiButtonPage(int x, int y, boolean left, IPressable action) {
        super(x, y, 18, 10, null, action);
        this.left = left;
    }

    @Override
    public void render(MatrixStack transform, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            this.isHovered = mouseX >= this.x
                             && mouseY >= this.y
                             && mouseX < this.x + this.width
                             && mouseY < this.y + this.height;

            TextureManager manager = Minecraft.getInstance().textureManager;
            manager.bindTexture(GuiForesterBook.TEXTURE);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlphaTest();
            GlStateManager.disableBlend();
            blit(transform, x, y, isHovered ? 18 : 0, 181 + (left ? 10 : 0), 18, 10);
            GlStateManager.enableBlend();
            GlStateManager.disableAlphaTest();
        }
    }

    @Nullable
    @Override
    public ToolTip getToolTip(int mouseX, int mouseY) {
        return null;
    }

    @Override
    public boolean isToolTipVisible() {
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }
}
