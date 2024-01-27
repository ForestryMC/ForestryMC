package forestry.core.gui.elements.text;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;

class ComponentText extends AbstractTextElement<Component, ComponentText> {

	public ComponentText(Component component) {
		super(component);
	}

	@Override
	public LabelElement setValue(Object text) {
		if (text instanceof Component) {
			this.text = (Component) text;
		} else if (text instanceof String) {
			this.text = Component.literal((String) text);
		}
		requestLayout();
		return this;
	}

	@Override
	public void drawElement(PoseStack transform, int mouseX, int mouseY) {
		if (shadow) {
			FONT_RENDERER.drawShadow(transform, text, 0, 0, 0);
		} else {
			FONT_RENDERER.draw(transform, text, 0, 0, 0);
		}
	}

	@Override
	protected int calcWidth(Font font) {
		return font.width(text);
	}

}
