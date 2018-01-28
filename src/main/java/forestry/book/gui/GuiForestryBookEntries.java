package forestry.book.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;

import forestry.api.book.IBookCategory;
import forestry.api.book.IBookEntry;
import forestry.api.book.IForesterBook;
import forestry.book.gui.buttons.GuiButtonBack;
import forestry.book.gui.buttons.GuiButtonEntry;
import forestry.book.gui.buttons.GuiButtonPage;
import forestry.core.gui.elements.ElementManager;

public class GuiForestryBookEntries extends GuiForesterBook {
	private final IBookCategory category;
	private final ElementManager<GuiForesterBook> elementManager;
	private int entryIndex = 0;

	public GuiForestryBookEntries(IForesterBook book, IBookCategory category) {
		super(book);
		this.category = category;
		this.elementManager = new ElementManager<>(this);
	}

	@Override
	public void initGui() {
		super.initGui();
		int offset = entryIndex * 24;
		int yOffset = 0;
		List<IBookEntry> entries = new ArrayList<>(category.getEntries());
		for(IBookEntry entry : entries.subList(offset, entries.size() > offset + 12 ? offset + 12 : entries.size())){
			addButton(new GuiButtonEntry(yOffset, guiLeft + LEFT_PAGE_START_X, guiTop + LEFT_PAGE_START_Y + yOffset * (fontRenderer.FONT_HEIGHT + 2), entry));
			yOffset++;
		}
		offset+=12;
		yOffset = 0;
		if(entries.size() > offset) {
			for(IBookEntry entry : entries.subList(offset, entries.size() > offset + 12 ? offset + 12 : entries.size())){
				addButton(new GuiButtonEntry(yOffset, guiLeft + RIGHT_PAGE_START_X, guiTop + LEFT_PAGE_START_Y + yOffset * (fontRenderer.FONT_HEIGHT + 2), entry));
				yOffset++;
			}
		}
		elementManager.init(guiLeft, guiTop);
		elementManager.clear();
	}

	@Override
	protected void initButtons(GuiButtonPage leftButton, GuiButtonPage rightButton, GuiButtonBack backButton) {
		leftButton.visible = entryIndex > 0;
		rightButton.visible = category.getEntries().size() > (entryIndex + 1) * 24;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button instanceof GuiButtonPage){
			GuiButtonPage pageButton = (GuiButtonPage) button;
			if(pageButton.left){
				entryIndex--;
			}else{
				entryIndex++;
			}
			initGui();
		}else if(button instanceof GuiButtonBack){
			displayCategories();
		}else if(button instanceof GuiButtonEntry){
			GuiButtonEntry entry = (GuiButtonEntry) button;
			mc.displayGuiScreen(new GuiForestryBookPages(book, category, entry.entry, null));
		}
	}

	@Override
	protected List<String> getTooltip(int mouseX, int mouseY) {
		return elementManager.getTooltip(mouseX, mouseY);
	}

	@Override
	protected void drawPages(int mouseX, int mouseY) {
		elementManager.draw(mouseX, mouseY);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		elementManager.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		elementManager.mouseClickMove(mouseX, mouseY, clickedMouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		elementManager.mouseReleased(mouseX, mouseY, state);
	}

	private void displayCategories(){
		mc.displayGuiScreen(new GuiForestryBookCategories(book));
	}

	@Override
	protected String getTitle() {
		return category.getLocalizedName();
	}
}
