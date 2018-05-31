package forestry.core.gui;

import javax.annotation.Nullable;
import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import forestry.api.genetics.IGeneticAnalyzer;
import forestry.api.genetics.IGeneticAnalyzerProvider;
import forestry.api.gui.IGuiElement;
import forestry.core.gui.buttons.GuiToggleButton;
import forestry.core.gui.elements.GeneticAnalyzer;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.slots.SlotAnalyzer;
import forestry.core.inventory.watchers.ISlotChangeWatcher;
import forestry.core.tiles.ITitled;

public abstract class GuiAnalyzerProvider<C extends Container> extends GuiForestryTitled<C> implements IGeneticAnalyzerProvider, ISlotChangeWatcher {
	/* Attributes - Constants */
	protected static final Drawable SELECTED_COMB_SLOT = new Drawable(GeneticAnalyzer.TEXTURE, 163, 0, 22, 22);
	protected static final Drawable TOGGLE_BUTTON = new Drawable(GeneticAnalyzer.TEXTURE, 35, 166, 18, 20);
	/* Attributes - Global */
	protected static boolean analyzerVisible = false;

	/* Attributes - Final */
	public final IGeneticAnalyzer analyzer;
	//The slot that contains the "energy" (honey drops) of the analyzer.
	@Nullable
	private final SlotAnalyzer slotAnalyzer;
	//Position of the button that toggles the analyzer.
	private final int buttonX;
	private final int buttonY;
	//The distance from the left side of the gui to the analyzer.
	private final int screenDistance;
	//The count of slots that the analyzer can select.
	private final int slots;
	//The slot that the analyzer selects at the creation of the gui.
	private final int firstSlot;
	//True if the error logic has any active error state.
	protected boolean deactivated;
	/* Attributes - State */
	//True if the visibility of the analyser changed.
	private boolean dirtyAnalyzer = false;
	//True if the individual or the error state changed.
	private boolean dirty = true;

	/* Constructors */
	public GuiAnalyzerProvider(String texture, C container, ITitled titled, int buttonX, int buttonY, int slots, int firstSlot) {
		this(texture, container, titled, buttonX, buttonY, 0, false, slots, firstSlot);
	}

	public GuiAnalyzerProvider(String texture, C container, ITitled titled, int buttonX, int buttonY, int screenDistance, boolean hasBoarder, int slots, int firstSlot) {
		super(texture, container, titled);
		this.buttonX = buttonX;
		this.buttonY = buttonY;
		this.screenDistance = screenDistance;
		this.slots = slots;
		this.firstSlot = firstSlot;

		this.analyzer = GuiElementFactory.INSTANCE.createAnalyzer(window, -189 - screenDistance, 0, hasBoarder, this);
		updateVisibility();

		SlotAnalyzer slotAnalyzer = null;
		if (container instanceof ContainerAnalyzerProvider) {
			ContainerAnalyzerProvider containerAnalyzer = (ContainerAnalyzerProvider) container;
			Slot analyzerSlot = containerAnalyzer.getAnalyzerSlot();
			if (analyzerSlot instanceof SlotAnalyzer) {
				((SlotAnalyzer) analyzerSlot).setGui(this);
				slotAnalyzer = (SlotAnalyzer) analyzerSlot;
			}
		}
		this.slotAnalyzer = slotAnalyzer;
	}

	/* Methods */
	protected boolean hasErrors() {
		return false;
	}

	private void updateVisibility() {
		analyzer.setVisible(!deactivated && analyzerVisible);
	}

	protected abstract void drawSelectedSlot(int selectedSlot);

	/* Methods - Implement GuiScreen */
	@Override
	public void initGui() {
		super.initGui();

		if (analyzer.isVisible()) {
			this.guiLeft = (this.width - this.xSize + analyzer.getWidth() + (screenDistance)) / 2;
		}
		window.init(guiLeft, guiTop + (ySize - 166) / 2);
		/*analyzer.setXOffset(guiLeft);
		analyzer.setYOffset(guiTop + (ySize - 166) / 2);*/

		/*GuiButtonToggle toggle = new GuiButtonToggle(0, guiLeft + buttonX, guiTop + buttonY, 18, 20, false);
		toggle.initTextureValues(0, 192, 0, 20, GeneticAnalyzer.TEXTURE);
		buttonList.add(toggle);*/
		addButton(new GuiToggleButton(0, guiLeft + buttonX, guiTop + buttonY, 18, 20, TOGGLE_BUTTON)).enabled = ((ContainerAnalyzerProvider) inventorySlots).getAnalyzerSlot() != null;
		dirty = true;

		if (slotAnalyzer != null) {
			IGuiElement element = analyzer.getItemElement();
			slotAnalyzer.setPosition(element.getAbsoluteX() - guiLeft + 6, element.getAbsoluteY() - guiTop + 9);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		boolean ledger = hasErrors();
		if (!deactivated && ledger || !ledger && deactivated) {
			deactivated = ledger;
			updateVisibility();
			dirtyAnalyzer = true;
		}
		if (dirtyAnalyzer) {
			buttonList.clear();
			initGui();
			dirtyAnalyzer = false;
		}
		if (dirty) {
			analyzer.update();
			dirty = false;
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
		//analyzer.drawTooltip(this, mouseX, mouseY);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button instanceof GuiToggleButton) {
			analyzerVisible = !analyzerVisible;
			updateVisibility();
			dirtyAnalyzer = true;
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		//analyzer.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		/*if (analyzer.keyTyped(typedChar, keyCode)) {
			return;
		}*/
		super.keyTyped(typedChar, keyCode);
	}

	/* Methods - Implement GuiContainer */
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		//analyzer.draw(0, 0, mouseX, mouseY);
		if (analyzer.isVisible()) {
			int selectedSlot = analyzer.getSelected();
			if (selectedSlot >= 0) {
				drawSelectedSlot(selectedSlot);
			}
		}
	}

	@Override
	protected boolean hasClickedOutside(int mouseX, int mouseY, int guiLeft, int guiTop) {
		return !analyzer.isMouseOver(mouseX, mouseY) && super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop);
	}

	/* Methods - Implement ISlotChangeWatcher*/
	@Override
	public void onSlotChanged(IInventory inventory, int slot) {
		if (slot == analyzer.getSelected()) {
			dirty = true;
		}
	}

	/* Methods - Implement IGeneticAnalyzerProvider */
	@Override
	public int getSelectedSlot(int index) {
		// + 1 Because the first slot is the energy slot.
		return 1 + index;
	}

	@Override
	public int getSlotCount() {
		return slots;
	}

	@Override
	public int getFirstSlot() {
		return firstSlot;
	}

}
