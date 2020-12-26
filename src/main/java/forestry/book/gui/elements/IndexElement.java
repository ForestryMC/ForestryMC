package forestry.book.gui.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.book.data.IndexEntry;
import forestry.book.gui.GuiForesterBook;
import forestry.book.gui.GuiForestryBookPages;
import forestry.core.gui.elements.LabelElement;
import forestry.core.gui.elements.layouts.VerticalLayout;
import forestry.core.gui.elements.lib.events.GuiEvent;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IndexElement extends VerticalLayout {
    //TODO Unicode
    private static final Style INDEX_STYLE = Style.EMPTY.setColor(Color.fromInt(0x000000));

    public IndexElement(int xPos, int yPos, IndexEntry[] data) {
        super(xPos, yPos, 108);
        for (IndexEntry index : data) {
            add(new IndexEntryElement(index));
        }
    }

    private static class IndexEntryElement extends LabelElement {
        public IndexEntryElement(IndexEntry data) {
            super(0, 0, -1, 9, new StringTextComponent(data.title).mergeStyle(INDEX_STYLE), true);
            setWidth(width + LabelElement.FONT_RENDERER.getStringWidth(" > "));
            addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
                GuiForesterBook bookGui = GuiForesterBook.getGuiScreen();
                if (bookGui instanceof GuiForestryBookPages) {
                    GuiForestryBookPages pagesGui = (GuiForestryBookPages) bookGui;
                    pagesGui.switchPage(data.page);
                }
            });
        }

        @Override
        public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
            boolean mouseOver = isMouseOver();
            IFormattableTextComponent preFix =
                    mouseOver ? new StringTextComponent(" > ").mergeStyle(TextFormatting.GOLD)
                              : new StringTextComponent("- ").mergeStyle(TextFormatting.DARK_GRAY);
            FONT_RENDERER.func_243248_b(transform, preFix.append(component), 0, 0, 0);
        }

        @Override
        public boolean canMouseOver() {
            return true;
        }
    }
}
