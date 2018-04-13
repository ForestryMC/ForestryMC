package forestry.core.utils;

import net.minecraft.client.renderer.GlStateManager;

import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.IGuiElement;

public class GuiElementUtil {
	private GuiElementUtil() {
	}

	public static void preRender(IGuiElement element, GuiElementAlignment align){
		IGuiElement parent = element.getParent();
		int xPos = element.getX();
		int yPos = element.getY();
		if(parent != null){
			if(parent.getWidth() > element.getWidth()){
				xPos += (parent.getWidth() - element.getWidth()) * align.getXOffset();
			}
			if (parent.getHeight() > element.getHeight()) {
				yPos += (parent.getHeight() - element.getHeight()) * align.getYOffset();
			}
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(xPos, yPos, 0.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F);

	}

	public static void posRender(IGuiElement element){
		GlStateManager.popMatrix();
	}

}
