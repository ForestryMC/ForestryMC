package forestry.book.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.text.StringTextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.book.gui.GuiForesterBook;

@OnlyIn(Dist.CLIENT)
public class GuiButtonPage extends Button {
	public boolean left;

	public GuiButtonPage(int x, int y, boolean left, IPressable action) {
		super(x, y, 18, 10, StringTextComponent.EMPTY, action);
		this.left = left;
	}

	@Override
	public void render(MatrixStack transform, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

			TextureManager manager = Minecraft.getInstance().textureManager;
			manager.bind(GuiForesterBook.TEXTURE);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.enableAlphaTest();
			RenderSystem.disableBlend();
			blit(transform, x, y, isHovered ? 18 : 0, 181 + (left ? 10 : 0), 18, 10);
			RenderSystem.enableBlend();
			RenderSystem.disableAlphaTest();
		}
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return false;
	}
}
