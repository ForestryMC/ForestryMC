package forestry.core.gui.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import forestry.core.gui.elements.lib.ITextElement;
import net.minecraft.util.text.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static forestry.core.gui.elements.LabelElement.FONT_RENDERER;

public class SplitTextElement extends GuiElement implements ITextElement {

    private final List<ITextComponent> lines = new ArrayList<>();

    public SplitTextElement(int xPos, int yPos, int width, IFormattableTextComponent component, Style style) {
        super(xPos, yPos, width, 0);
        setText(component.mergeStyle(style));
        setHeight(lines.size() * FONT_RENDERER.FONT_HEIGHT);
    }

    @Override
    public Collection<ITextComponent> getLines() {
        return lines;
    }

    @Override
    public ITextElement setText(ITextComponent text) {
        lines.clear();
        for (ITextProperties string : FONT_RENDERER.getCharacterManager().func_238362_b_(text, width, Style.EMPTY)) {
            lines.add(new StringTextComponent(string.getString()));
        }

        setHeight(lines.size() * FONT_RENDERER.FONT_HEIGHT);
        return this;
    }

    @Override
    public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
        int posY = 0;
        for (ITextComponent text : lines) {
            int posX = width - FONT_RENDERER.getStringWidth(text.getString());
            posX *= getAlign().getXOffset();
            FONT_RENDERER.func_243248_b(transform, text, posX, posY, 0);
            posY += FONT_RENDERER.FONT_HEIGHT;
        }

        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
    }
}
