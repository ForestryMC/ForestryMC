package forestry.book.gui.elements;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.book.data.IndexEntry;
import forestry.book.gui.GuiForesterBook;
import forestry.book.gui.GuiForestryBookPages;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.elements.layouts.ContainerElement;
import forestry.core.gui.elements.layouts.FlexLayout;
import forestry.core.gui.elements.text.LabelElement;

@OnlyIn(Dist.CLIENT)
public class IndexElement extends ContainerElement {
	private static final Style INDEX_STYLE = Style.EMPTY.withColor(Color.fromRgb(0x000000));

	public IndexElement(IndexEntry[] data) {
		setSize(108, UNKNOWN_HEIGHT);
		setLayout(FlexLayout.vertical(0));
		for (IndexEntry index : data) {
			add(new Entry(index));
		}
	}

	private static class Entry extends ContainerElement {
		private final IndexEntry index;
		private final LabelElement child;

		public Entry(IndexEntry index) {
			child = labelLine("- " + index.title)
					.fitText()
					.setStyle(INDEX_STYLE)
					.create();
			this.index = index;
		}

		@Override
		protected void drawElement(MatrixStack transform, int mouseX, int mouseY) {
			GuiUtil.enableUnicode();
			super.drawElement(transform, mouseX, mouseY);
			GuiUtil.resetUnicode();
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

}
