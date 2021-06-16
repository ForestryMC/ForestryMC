package forestry.book.gui.elements;

import javax.annotation.Nullable;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.book.data.TextData;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.elements.GuiElement;

//TODO Move to component system
@OnlyIn(Dist.CLIENT)
public class TextDataElement extends GuiElement {

	private final List<TextData> textElements = new ArrayList<>();
	@Nullable
	private Dimension layoutSize;

	public TextDataElement(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
	}

	@Override
	public Dimension getLayoutSize() {
		if (layoutSize == null) {
			Dimension size = new Dimension(super.getLayoutSize());
			if (size.height < 0) {
				int height = 0;
				FontRenderer fontRenderer = Minecraft.getInstance().font;
				GuiUtil.enableUnicode();
				boolean lastEmpty = false;
				for (TextData data : textElements) {
					if (data.text.equals("\n")) {
						if (lastEmpty) {
							height += fontRenderer.lineHeight;
						}
						lastEmpty = true;
						continue;
					}
					lastEmpty = false;

					if (data.paragraph) {
						height += fontRenderer.lineHeight * 1.6D;
					}

					String modifiers = "";

					modifiers += TextFormatting.getByName(data.color);

					if (data.bold) {
						modifiers += TextFormatting.BOLD;
					}
					if (data.italic) {
						modifiers += TextFormatting.ITALIC;
					}
					if (data.underlined) {
						modifiers += TextFormatting.UNDERLINE;
					}
					if (data.strikethrough) {
						modifiers += TextFormatting.STRIKETHROUGH;
					}
					if (data.obfuscated) {
						modifiers += TextFormatting.OBFUSCATED;
					}
					height += fontRenderer.wordWrapHeight(modifiers + data.text, getWidth());
				}
				size.height = height;
				GuiUtil.resetUnicode();
			}
			layoutSize = size;
		}
		return layoutSize;
	}

	@Override
	public void drawElement(MatrixStack transform, int mouseX, int mouseY) {
		FontRenderer fontRenderer = Minecraft.getInstance().font;
		GuiUtil.enableUnicode();
		int x = 0;
		int y = 0;
		for (TextData data : textElements) {
			if (data.text.equals("\n")) {
				x = 0;
				y += fontRenderer.lineHeight;
				continue;
			}

			if (data.paragraph) {
				x = 0;
				y += fontRenderer.lineHeight * 1.6D;
			}

			String text = getFormattedString(data);
			List<IReorderingProcessor> split = fontRenderer.split(new StringTextComponent(text), getWidth());
			for (int i = 0; i < split.size(); i++) {
				IReorderingProcessor component = split.get(i);
				int textLength;
				if (data.dropShadow) {
					textLength = fontRenderer.drawShadow(transform, component, x, y, 0);
				} else {
					textLength = fontRenderer.draw(transform, component, x, y, 0);
				}
				if (i == split.size() - 1) {
					x += textLength;
				} else {
					y += fontRenderer.lineHeight;
				}
			}
		}
		GuiUtil.resetUnicode();
	}

	private String getFormattedString(TextData data) {
		StringBuilder modifiers = new StringBuilder();

		modifiers.append(TextFormatting.getByName(data.color));

		if (data.bold) {
			modifiers.append(TextFormatting.BOLD);
		}
		if (data.italic) {
			modifiers.append(TextFormatting.ITALIC);
		}
		if (data.underlined) {
			modifiers.append(TextFormatting.UNDERLINE);
		}
		if (data.strikethrough) {
			modifiers.append(TextFormatting.STRIKETHROUGH);
		}
		if (data.obfuscated) {
			modifiers.append(TextFormatting.OBFUSCATED);
		}
		modifiers.append(data.text);
		return modifiers.toString();
	}

	public void addData(TextData textData) {
		textElements.add(textData);
		setHeight(-1);
	}
}
