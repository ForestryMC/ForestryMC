package forestry.book.gui.elements;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.book.data.IndexEntry;
import forestry.book.gui.GuiForesterBook;
import forestry.book.gui.GuiForestryBookPages;
import forestry.core.gui.elements.WrapperElement;
import forestry.core.gui.elements.layouts.ContainerElement;
import forestry.core.gui.elements.layouts.FlexLayout;
import forestry.core.gui.elements.text.LabelElement;

@OnlyIn(Dist.CLIENT)
public class IndexElement extends ContainerElement {
	//TODO Unicode
	private static final Style INDEX_STYLE = Style.EMPTY.withColor(Color.fromRgb(0x000000));

	public IndexElement(IndexEntry[] data) {
		setSize(108, UNKNOWN_HEIGHT);
		setLayout(FlexLayout.vertical(0));
		for (IndexEntry index : data) {
			//add(new IndexEntryElement(index));
			/*LabelElement element = labelLine("- " + index.title)
					.fitText()
					.setStyle(INDEX_STYLE)
					.create();
			element.addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
				GuiForesterBook bookGui = GuiForesterBook.getGuiScreen();
				if (bookGui instanceof GuiForestryBookPages) {
					GuiForestryBookPages pagesGui = (GuiForestryBookPages) bookGui;
					pagesGui.switchPage(index.page);
				}
			});
			element.addMouseListener(MouseEvent.ENTER, (x, y, button) -> {
				element.setValue(" > " + index.title);
				return true;
			});
			element.addMouseListener(MouseEvent.LEAVE, (x, y, button) -> {
				element.setValue("- " + index.title);
				return true;
			});*/
			//add(element);
			add(new Entry(index));
		}
	}

	private static class Entry extends WrapperElement<LabelElement> {
		private final IndexEntry index;

		public Entry(IndexEntry index) {
			super(new LabelElement.Builder("- " + index.title)
					.fitText()
					.setStyle(INDEX_STYLE)
					.create()
			);
			this.index = index;
		}

		@Override
		public boolean canMouseOver() {
			return true;
		}

		@Override
		public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
			GuiForesterBook bookGui = GuiForesterBook.getGuiScreen();
			if (bookGui instanceof GuiForestryBookPages) {
				GuiForestryBookPages pagesGui = (GuiForestryBookPages) bookGui;
				pagesGui.switchPage(index.page);
				return true;
			}
			return false;
		}

		@Override
		public void onMouseEnter(double mouseX, double mouseY) {
			child.setValue(" > " + index.title);
		}

		@Override
		public void onMouseLeave(double mouseX, double mouseY) {
			child.setValue("- " + index.title);
		}
	}

	/*private class IndexEntryElement extends ComponentText {

		public IndexEntryElement(IndexEntry data) {
			super(0, 0, -1, 9, new StringTextComponent(data.title).withStyle(INDEX_STYLE), true);
			setWidth(width + ComponentText.FONT_RENDERER.width(" > "));
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
		public void drawElement(MatrixStack transform, int mouseX, int mouseY) {
			boolean mouseOver = isMouseOver();
			String preFix = mouseOver ? TextFormatting.GOLD + " > " : TextFormatting.DARK_GRAY + "- ";
			FONT_RENDERER.draw(transform, preFix + component.getString(), 0, 0, 0);
		}

		@Override
		public boolean canMouseOver() {
			return true;
		}
	}*/
}
