package forestry.book.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.book.IBookCategory;
import forestry.api.book.IBookEntry;
import forestry.api.book.IForesterBook;
import forestry.book.gui.buttons.GuiButtonBack;
import forestry.book.gui.buttons.GuiButtonEntry;
import forestry.book.gui.buttons.GuiButtonPage;

@SideOnly(Side.CLIENT)
public class GuiForestryBookEntries extends GuiForesterBook {
	private final IBookCategory category;
	private int entryIndex = 0;

	public GuiForestryBookEntries(IForesterBook book, IBookCategory category) {
		super(book);
		this.category = category;
	}

	@Override
	public void initGui() {
		super.initGui();
		int offset = entryIndex * 24;
		addEntryButtons(offset, LEFT_PAGE_START_X, LEFT_PAGE_START_Y);
		addEntryButtons(offset + 12, RIGHT_PAGE_START_X, RIGHT_PAGE_START_Y);
		window.init(guiLeft, guiTop);
		window.clear();
	}

	private void addEntryButtons(int indexStart, int xStart, int yStart) {
		List<IBookEntry> entries = new ArrayList<>(category.getEntries());
		if (indexStart >= entries.size()) {
			return;
		}
		final int maxIndex;
		if (entries.size() > indexStart + 12) {
			maxIndex = indexStart + 12;
		} else {
			maxIndex = entries.size();
		}
		int yOffset = 0;
		for (int i = indexStart; i < maxIndex; i++) {
			IBookEntry entry = entries.get(i);
			addButton(new GuiButtonEntry(buttonList.size(), guiLeft + xStart, guiTop + yStart + yOffset, entry));
			yOffset += fontRenderer.FONT_HEIGHT + 2;
		}
	}

	@Override
	protected void initButtons(GuiButtonPage leftButton, GuiButtonPage rightButton, GuiButtonBack backButton) {
		leftButton.visible = entryIndex > 0;
		rightButton.visible = category.getEntries().size() > (entryIndex + 1) * 24;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button instanceof GuiButtonPage) {
			GuiButtonPage pageButton = (GuiButtonPage) button;
			if (pageButton.left) {
				entryIndex--;
			} else {
				entryIndex++;
			}
			initGui();
		} else if (button instanceof GuiButtonBack) {
			displayCategories();
		} else if (button instanceof GuiButtonEntry) {
			GuiButtonEntry entry = (GuiButtonEntry) button;
			mc.displayGuiScreen(new GuiForestryBookPages(book, category, entry.entry, null));
		}
	}

	private void displayCategories() {
		mc.displayGuiScreen(new GuiForestryBookCategories(book));
	}

	@Override
	protected String getTitle() {
		return category.getLocalizedName();
	}
}
