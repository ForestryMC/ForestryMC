package forestry.core.gui;

import javax.annotation.Nullable;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import forestry.api.genetics.gatgets.IGeneticAnalyzer;
import forestry.api.genetics.gatgets.IGeneticAnalyzerProvider;
import forestry.core.gui.buttons.GuiToggleButton;
import forestry.core.gui.elements.GeneticAnalyzer;
import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.slots.SlotAnalyzer;
import forestry.core.inventory.ItemInventoryAlyzer;
import forestry.core.inventory.watchers.ISlotChangeWatcher;
import forestry.core.tiles.ITitled;

public abstract class GuiAnalyzerProvider<C extends AbstractContainerMenu> extends GuiForestryTitled<C> implements IGeneticAnalyzerProvider, ISlotChangeWatcher {
	/* Attributes - Constants */
	protected static final Drawable SELECTED_COMB_SLOT = new Drawable(GeneticAnalyzer.TEXTURE, 163, 0, 22, 22);
	protected static final Drawable TOGGLE_BUTTON = new Drawable(GeneticAnalyzer.TEXTURE, 35, 166, 18, 20);
	/* Attributes - Global */
	private static boolean analyzerVisible = false;

	/* Attributes - Final */
	public final IGeneticAnalyzer analyzer;
	//The slot that contains the "energy" (honey drops) of the analyzer.
	@Nullable
	private SlotAnalyzer slotAnalyzer;
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
	//If the analyzer slot has to bee newly aligned
	private boolean slotDirty;

	/* Constructors */
	public GuiAnalyzerProvider(String texture, C container, Inventory inv, ITitled titled, int buttonX, int buttonY, int slots, int firstSlot) {
		this(texture, container, inv, titled, buttonX, buttonY, 0, false, slots, firstSlot);
	}

	public GuiAnalyzerProvider(String texture, C container, Inventory inv, ITitled titled, int buttonX, int buttonY, int screenDistance, boolean hasBorder, int slots, int firstSlot) {
		super(texture, container, inv, Component.translatable(titled.getUnlocalizedTitle()));
		this.buttonX = buttonX;
		this.buttonY = buttonY;
		this.screenDistance = screenDistance;
		this.slots = slots;
		this.firstSlot = firstSlot;

		this.analyzer = GuiElementFactory.INSTANCE.createAnalyzer(window, -189 - screenDistance, 0, hasBorder, this);
		updateVisibility();

		SlotAnalyzer analyzerSlot = null;
		if (container instanceof IContainerAnalyzerProvider containerAnalyzer) {
			Slot slot = containerAnalyzer.getAnalyzerSlot();
			if (slot instanceof SlotAnalyzer) {
				((SlotAnalyzer) slot).setVisibleCallback(analyzer::isVisible);
				analyzerSlot = (SlotAnalyzer) slot;
			}
		}
		this.slotAnalyzer = analyzerSlot;
		slotDirty = true;
	}

	/* Methods */
	protected boolean hasErrors() {
		return false;
	}

	private void updateVisibility() {
		analyzer.setVisible(!deactivated && analyzerVisible);
	}

	protected abstract void drawSelectedSlot(PoseStack transform, int selectedSlot);

	/* Methods - Implement GuiScreen */
	@Override
	public void init() {
		super.init();

		if (analyzer.isVisible()) {
			this.leftPos = (this.width - this.imageWidth + ((GuiElement) analyzer).getWidth() + (screenDistance)) / 2;
		}
		window.init(leftPos, topPos + (imageHeight - 166) / 2);

		addRenderableWidget(new GuiToggleButton(leftPos + buttonX, topPos + buttonY, 18, 20, TOGGLE_BUTTON, new Handler())).visible = ((IContainerAnalyzerProvider) container).getAnalyzerSlot() != null;
		dirty = true;

		slotDirty = true;
	}

	@Override
	public void render(PoseStack transform, int mouseX, int mouseY, float partialTicks) {
		boolean ledger = hasErrors();
		if (!deactivated && ledger || !ledger && deactivated) {
			deactivated = ledger;
			updateVisibility();
			dirtyAnalyzer = true;
		}
		if (dirtyAnalyzer) {
			renderables.clear();
			init();
			dirtyAnalyzer = false;
		}
		if (dirty) {
			analyzer.update();
			dirty = false;
		}
		super.render(transform, mouseX, mouseY, partialTicks);

		//Called after first render, so the item element was laid out
		if (slotDirty) {
			if (slotAnalyzer != null) {
				GuiElement element = analyzer.getItemElement();
				int index = slotAnalyzer.index;
				slotAnalyzer = new SlotAnalyzer((ItemInventoryAlyzer) slotAnalyzer.container, slotAnalyzer.getSlotIndex(), element.getAbsoluteX() - leftPos + 6, element.getAbsoluteY() - topPos + 9);
				slotAnalyzer.index = index;
				slotAnalyzer.setVisibleCallback(analyzer::isVisible);
				container.slots.set(index, slotAnalyzer);
			}
			slotDirty = false;
		}
	}

	class Handler implements Button.OnPress {
		@Override
		public void onPress(Button button) {
			if (button instanceof GuiToggleButton) {
				setAnalyzerVisible(!isAnalyzerVisible());
				updateVisibility();
				dirtyAnalyzer = true;
			}
		}
	}

	/* Methods - Implement GuiContainer */
	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(transform, partialTicks, mouseX, mouseY);
		// RenderSystem.color3f(1.0F, 1.0F, 1.0F);
		if (analyzer.isVisible()) {
			int selectedSlot = analyzer.getSelected();
			if (selectedSlot >= 0) {
				drawSelectedSlot(transform, selectedSlot);
			}
		}
	}

	/* Methods - Implement ISlotChangeWatcher*/
	@Override
	public void onSlotChanged(Container inventory, int slot) {
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

	/* Methods - Gui Globals */
	private static void setAnalyzerVisible(boolean analyzerVisible) {
		GuiAnalyzerProvider.analyzerVisible = analyzerVisible;
	}

	private static boolean isAnalyzerVisible() {
		return analyzerVisible;
	}
}
