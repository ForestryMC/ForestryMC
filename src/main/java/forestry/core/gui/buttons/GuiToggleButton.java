package forestry.core.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import forestry.core.gui.Drawable;

public class GuiToggleButton extends GuiButton {
	/* attributes - Final */
	private final Drawable[] textures = new Drawable[3];

	public GuiToggleButton(int buttonId, int x, int y, int widthIn, int heightIn, Drawable drawable) {
		super(buttonId, x, y, widthIn, heightIn, "");
		for (int i = 0; i < 3; i++) {
			textures[i] = new Drawable(drawable.textureLocation, drawable.u, drawable.v + drawable.vHeight * i, drawable.uWidth, drawable.vHeight);
		}
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		int hoverState = this.getHoverState(this.hovered);
		Drawable drawable = textures[hoverState];
		drawable.draw(x, y);
	}
}
