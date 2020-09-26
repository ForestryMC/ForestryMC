package forestry.book.gui.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.book.data.IndexEntry;
import forestry.book.gui.GuiForesterBook;
import forestry.book.gui.GuiForestryBookPages;
import forestry.core.gui.elements.LabelElement;
import forestry.core.gui.elements.layouts.VerticalLayout;
import forestry.core.gui.elements.lib.events.GuiEvent;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
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

    private class IndexEntryElement extends LabelElement {

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

        //TODO ITextComponent
        @Override
        public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
            boolean mouseOver = isMouseOver();
            String preFix = mouseOver ? TextFormatting.GOLD + " > " : TextFormatting.DARK_GRAY + "- ";
            FONT_RENDERER.drawString(transform, preFix + component.getString(), 0, 0, 0);
        }

        @Override
        public boolean canMouseOver() {
            return true;
        }
    }
}
