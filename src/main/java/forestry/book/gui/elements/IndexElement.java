package forestry.book.gui.elements;

import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.events.GuiEvent;
import forestry.api.gui.style.ITextStyle;
import forestry.api.gui.style.TextStyleBuilder;
import forestry.book.data.IndexEntry;
import forestry.book.gui.GuiForesterBook;
import forestry.book.gui.GuiForestryBookPages;
import forestry.core.gui.elements.LabelElement;
import forestry.core.gui.elements.layouts.VerticalLayout;

@OnlyIn(Dist.CLIENT)
public class IndexElement extends VerticalLayout {
	private static final ITextStyle INDEX_STYLE = new TextStyleBuilder().unicode(true).color(0x000000).build();

	public IndexElement(int xPos, int yPos, IndexEntry[] data) {
		super(xPos, yPos, 108);
		for (IndexEntry index : data) {
			add(new IndexEntryElement(index));
		}
	}

	private class IndexEntryElement extends LabelElement {

		public IndexEntryElement(IndexEntry data) {
			super(0, 0, -1, 9, data.title, GuiElementAlignment.TOP_LEFT, INDEX_STYLE);
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
		public void drawElement(int mouseX, int mouseY) {
			boolean mouseOver = isMouseOver();
			boolean unicode = FONT_RENDERER.getBidiFlag();
			String preFix = mouseOver ? TextFormatting.GOLD + " > " : TextFormatting.DARK_GRAY + "- ";
			FONT_RENDERER.setBidiFlag(style.isUnicode());
			FONT_RENDERER.drawString(preFix + text, 0, 0, style.getColor());
			FONT_RENDERER.setBidiFlag(unicode);
		}

		@Override
		public boolean canMouseOver() {
			return true;
		}
	}
}
