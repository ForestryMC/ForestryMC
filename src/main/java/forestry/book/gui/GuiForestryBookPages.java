package forestry.book.gui;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;

import forestry.api.book.IBookCategory;
import forestry.api.book.IBookEntry;
import forestry.api.book.IForesterBook;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.book.gui.buttons.GuiButtonBack;
import forestry.book.gui.buttons.GuiButtonPage;
import forestry.book.gui.buttons.GuiButtonSubEntry;
import forestry.core.gui.elements.layouts.ElementGroup;

public class GuiForestryBookPages extends GuiForesterBook {
	private final IBookCategory category;
	private final IBookEntry entry;
	@Nullable
	private final IBookEntry parent;
	private final List<GuiButtonSubEntry> subButtons = new ArrayList<>();
	private List<IGuiElement> pages;
	private int pageIndex = 0;
	private IElementGroup leftPage;
	private IElementGroup rightPage;
	private int nextPage = -1;
	private int lastPage = -1;

	public GuiForestryBookPages(IForesterBook book, IBookCategory category, IBookEntry entry, @Nullable IBookEntry parent) {
		super(book);
		this.category = category;
		this.entry = entry;
		this.parent = parent;
		ElementGroup group = elementManager.group();
		leftPage = group.panel(LEFT_PAGE_START_X, LEFT_PAGE_START_Y, PAGE_WIDTH, PAGE_HEIGHT);
		rightPage = group.panel(RIGHT_PAGE_START_X, RIGHT_PAGE_START_Y, PAGE_WIDTH, PAGE_HEIGHT);
		pages = ImmutableList.copyOf(entry.getPageFactory().load(entry, GuiForesterBook.PAGE_HEIGHT - 13, GuiForesterBook.PAGE_HEIGHT, GuiForesterBook.PAGE_WIDTH));
		setPages(0);
	}

	private void setPages(int index) {
		leftPage.clear();
		rightPage.clear();
		if (index < 0 || index >= pages.size()) {
			pageIndex = 0;
			return;
		}
		leftPage.add(pages.get(index));
		if (pages.size() > index + 1) {
			rightPage.add(pages.get(index + 1));
		}
		pageIndex = index;
	}

	public void switchPage(int page) {
		if (page % 2 == 1) {
			page -= 1;
		}
		this.nextPage = page;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (nextPage >= 0) {
			lastPage = pageIndex;
			setPages(nextPage);
			nextPage = -1;
			initGui();
		}
	}

	@Override
	protected void initButtons(GuiButtonPage leftButton, GuiButtonPage rightButton, GuiButtonBack backButton) {
		leftButton.visible = pageIndex > 0;
		rightButton.visible = pages.size() > (pageIndex + 2);
	}

	@Override
	public void initGui() {
		super.initGui();
		IBookEntry firstEntry = parent != null ? parent : entry;
		subButtons.add(addButton(new GuiButtonSubEntry(buttonList.size(), guiLeft + -24, guiTop + 12, firstEntry, entry)));
		IBookEntry[] subEntries = firstEntry.getSubEntries();
		for (int i = 0; i < subEntries.length; i++) {
			IBookEntry subEntry = subEntries[i];
			subButtons.add(addButton(new GuiButtonSubEntry(buttonList.size(), guiLeft + -24, guiTop + 12 + ((i + 1) * 22), subEntry, entry)));
		}
	}

	@Override
	protected List<String> getTooltip(int mouseX, int mouseY) {
		List<String> tooltip = new LinkedList<>();
		for (GuiButtonSubEntry subEntry : subButtons) {
			if (subEntry.isMouseOver(mouseX, mouseY)) {
				tooltip.addAll(subEntry.getToolTip());
			}
		}
		return tooltip;
	}

	@Override
	protected String getTitle() {
		return entry.getTitle();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button instanceof GuiButtonPage) {
			GuiButtonPage pageButton = (GuiButtonPage) button;
			if (pageButton.left) {
				setPages(pageIndex - 2);
			} else {
				setPages(pageIndex + 2);
			}
			initGui();
			if (lastPage >= 0) {
				lastPage = -1;
			}
		} else if (button instanceof GuiButtonSubEntry) {
			GuiButtonSubEntry subEntry = (GuiButtonSubEntry) button;
			mc.displayGuiScreen(new GuiForestryBookPages(book, category, subEntry.subEntry, parent != null ? parent : entry));
		} else if (button instanceof GuiButtonBack || pages.isEmpty()) {
			if (lastPage >= 0) {
				setPages(lastPage);
				lastPage = -1;
				initGui();
			} else {
				displayEntries();
			}
		}
	}

	private void displayEntries() {
		mc.displayGuiScreen(new GuiForestryBookEntries(book, category));
	}
}
