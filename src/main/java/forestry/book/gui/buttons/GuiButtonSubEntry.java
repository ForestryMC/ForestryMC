package forestry.book.gui.buttons;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.text.ITextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.IBookEntry;
import forestry.book.gui.GuiForesterBook;
import forestry.core.gui.GuiUtil;

@OnlyIn(Dist.CLIENT)
public class GuiButtonSubEntry extends Button {
    public final IBookEntry selectedEntry;
    public final IBookEntry subEntry;

    public GuiButtonSubEntry(int x, int y, IBookEntry subEntry, IBookEntry selectedEntry, IPressable action) {
        super(x, y, 24, 21, selectedEntry.getTitle(), action);
        this.subEntry = subEntry;
        this.selectedEntry = selectedEntry;
    }

    @Override
    public void render(MatrixStack transform, int mouseX, int mouseY, float partialTicks) {
        if (!visible) {
            return;
        }
        boolean active = subEntry == selectedEntry;
        TextureManager manager = Minecraft.getInstance().textureManager;
        manager.bindTexture(GuiForesterBook.TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        blit(transform, x, y, 48 + (active ? 24 : 0), 201, 24, 21);

        GlStateManager.translatef(x + 8.0F, y + 4.0F, getBlitOffset());    //TODO
        GlStateManager.scalef(0.85F, 0.85F, 0.85F);
        //RenderHelper.enableGUIStandardItemLighting(); TODO: Gui Item Light
        GlStateManager.enableRescaleNormal();

        GuiUtil.drawItemStack(Minecraft.getInstance().fontRenderer, subEntry.getStack(), 0, 0);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    public List<ITextComponent> getToolTip() {
        return Collections.singletonList(subEntry.getTitle());
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
}
