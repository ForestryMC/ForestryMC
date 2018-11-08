package forestry.core.gui.elements;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IDatabasePlugin;
import forestry.api.genetics.IDatabaseTab;
import forestry.api.genetics.IGeneticAnalyzer;
import forestry.api.genetics.IGeneticAnalyzerProvider;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IWindowElement;
import forestry.api.gui.events.GuiEvent;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.buttons.StandardButtonTextureSets;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.core.gui.widgets.IScrollable;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;

import org.lwjgl.input.Keyboard;

public class GeneticAnalyzer extends ElementGroup implements IGeneticAnalyzer, IScrollable {
	/* Textures */
	public static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/analyzer_screen.png");

	/* Drawables */
	public static final Drawable SCROLLBAR_BACKGROUND = new Drawable(TEXTURE, 202, 0, 3, 142);
	public static final Drawable SCROLLBAR_SLIDER = new Drawable(TEXTURE, 205, 0, 3, 5);
	public static final Drawable SELECTION_BAR = new Drawable(GeneticAnalyzer.TEXTURE, 70, 166, 107, 32);
	public static final Drawable ANALYZER_BUTTON = new Drawable(TEXTURE, 163, 40, 22, 25);

	/* Attributes- Final */
	private final IGeneticAnalyzerProvider provider;

	/*Attributes - Gui Elements */
	private final ScrollBarElement scrollBar;
	private final ScrollableElement scrollable;
	private final DatabaseElement scrollableContent;
	private final GeneticAnalyzerTabs tabs;
	private final IGuiElement itemElement;
	private final ButtonElement leftButton;
	private final ButtonElement rightButton;
	private final ButtonElement analyzeButton;

	/* Attributes - State */
	private int selectedSlot = -1;

	public GeneticAnalyzer(IWindowElement window, int xPos, int yPos, boolean rightBoarder, IGeneticAnalyzerProvider provider) {
		super(xPos - (rightBoarder ? 6 : 0), yPos, 189 + (rightBoarder ? 6 : 0), 194);
		window.add(this);
		this.provider = provider;

		//Background Texture
		drawable(32, 0, new Drawable(TEXTURE, 0, 0, 163 + (rightBoarder ? 0 : -6), 166));
		//Text Area
		scrollable = new ScrollableElement(32 + 10, 8, 145, 150);
		add(scrollable);
		scrollableContent = new DatabaseElement(145);
		scrollable.setContent(scrollableContent);
		scrollable.add(scrollableContent);
		//Scrollbar
		scrollBar = new ScrollBarElement(width - 10 - (rightBoarder ? 6 : 0), 12, SCROLLBAR_BACKGROUND, false, SCROLLBAR_SLIDER);
		scrollBar.hide();
		add(scrollBar);
		//Side Tabs
		tabs = new GeneticAnalyzerTabs(0, 5, this);
		add(tabs);
		//Selection Bar at the bottom
		itemElement = drawable((getWidth() + 32 - SELECTION_BAR.uWidth) / 2, 162, SELECTION_BAR);

		leftButton = add(new ButtonElement(itemElement.getX() + 30, itemElement.getY() + 9, StandardButtonTextureSets.LEFT_BUTTON, (button) -> subtract()));
		rightButton = add(new ButtonElement(itemElement.getX() + 64, itemElement.getY() + 9, StandardButtonTextureSets.RIGHT_BUTTON, (button) -> add()));
		analyzeButton = add(new ButtonElement(itemElement.getX() + 80, itemElement.getY() + 2, ANALYZER_BUTTON, (button) -> NetworkUtil.sendToServer(new PacketGuiSelectRequest(0, provider.getSelectedSlot(selectedSlot)))));
		add(new AbstractItemElement(itemElement.getX() + 44, itemElement.getY() + 9) {
			@Override
			protected ItemStack getStack() {
				return provider.getSpecimen(selectedSlot);
			}
		});
		addEventHandler(GuiEvent.KeyEvent.class, event -> {
			int keyCode = event.getKey();
			if ((keyCode == Keyboard.KEY_DOWN || keyCode == Keyboard.KEY_RIGHT) && rightButton.isEnabled()) {
				rightButton.onPressed();
			} else if ((keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_LEFT) && leftButton.isEnabled()) {
				leftButton.onPressed();
			} else if (keyCode == Keyboard.KEY_RETURN && analyzeButton.isEnabled()) {
				analyzeButton.onPressed();
			}
		});
	}

	@Override
	public void init() {
		leftButton.setEnabled(canSubtract());
		rightButton.setEnabled(canAdd());
		updateSelected();
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public IGeneticAnalyzerProvider getProvider() {
		return provider;
	}

	public IGuiElement getItemElement() {
		return itemElement;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update() {
		if (!isVisible()) {
			return;
		}
		ItemStack stack = provider.getSpecimen(selectedSlot);
		ISpeciesRoot root = AlleleManager.alleleRegistry.getSpeciesRoot(stack);
		if (root != null) {
			IDatabasePlugin databasePlugin = root.getSpeciesPlugin();
			if (databasePlugin != null) {
				IIndividual individual = root.getMember(stack);
				if (individual != null) {
					if (individual.isAnalyzed()) {
						tabs.setPlugin(databasePlugin);
						IDatabaseTab tab = tabs.getSelected();
						//Clean the element area
						scrollableContent.clear();
						//Create the new elements
						scrollableContent.init(tab.getMode(), individual, scrollableContent.getWidth() / 2, 0);
						tab.createElements(scrollableContent, individual, stack);
						//Update the scrollbar
						int invisibleArea = scrollable.getInvisibleArea();
						if (invisibleArea > 0) {
							scrollBar.setParameters(this, 0, invisibleArea, 1);
							scrollBar.show();
						} else {
							scrollBar.setValue(0);
							scrollBar.hide();
						}
						return;
					}
					tabs.setPlugin(null);
				}
			}
		}
		//Clean the element area
		scrollableContent.clear();
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		String key = "for.gui.portablealyzer.help";
		//if(state == DatabaseScreenLogic.ScreenState.NO_PLUGIN){
		//key = "for.gui.database.support";
		//}
		List<String> lines = fontRenderer.listFormattedStringToWidth(Translator.translateToLocal(key), scrollable.getWidth());
		for (String text : lines) {
			scrollableContent.label(text);
		}
		//Disable the scrollbar
		scrollBar.hide();
	}

	@Override
	public void drawTooltip(GuiScreen gui, int mouseX, int mouseY) {
		if (!visible) {
			return;
		}
		/*List<String> lines = getTooltip(mouseX, mouseY);
		if (!lines.isEmpty()) {
			GlStateManager.pushMatrix();
			ScaledResolution scaledresolution = new ScaledResolution(gui.mc);
			GuiUtils.drawHoveringText(lines, mouseX, mouseY, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), -1, gui.mc.fontRenderer);
			GlStateManager.popMatrix();
		}*/
	}

	/*@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent, IGuiState state) {
		if(!visible){
			return false;
		}
		return super.mouseClicked(mouseX, mouseY, mouseEvent);
	}*/

	@Override
	public boolean isFocused(int mouseX, int mouseY) {
		return isMouseOver();
	}

	@Override
	public void onScroll(int value) {
		scrollable.onScroll(value);
	}

	private boolean canAdd() {
		return selectedSlot + 1 < getSlotCount();
	}

	private boolean canSubtract() {
		int selectedSlot = this.selectedSlot - 1;
		return selectedSlot < getSlotCount() && selectedSlot >= 0;
	}

	public void add() {
		setSelectedSlot(selectedSlot + 1);
	}

	private void subtract() {
		setSelectedSlot(selectedSlot - 1);
	}

	private int getSlotCount() {
		return provider.getSlotCount();
	}

	public void setSelectedSlot(int selectedSlot) {
		int oldSelected = this.selectedSlot;
		this.selectedSlot = selectedSlot;
		onSelection(selectedSlot, oldSelected != selectedSlot);
	}

	private void onSelection(int selectedSlot, boolean changed) {
		leftButton.setEnabled(canSubtract());
		rightButton.setEnabled(canAdd());
		if (changed) {
			update();
		}
		provider.onSelection(selectedSlot, changed);
	}

	public int getSelectedSlot() {
		return selectedSlot;
	}

	public void updateSelected() {
		/*int index = sorted.indexOf(selected);
		if(index >= 0) {
			setSelectedSlot(sorted.indexOf(selected));
			return;
		}*/
		if (provider.onUpdateSelected()) {
			return;
		}
		int slotCount = getSlotCount();
		if (slotCount <= 0) {
			setSelectedSlot(-1);
			return;
		}
		if (slotCount > selectedSlot && selectedSlot != -1) {
			return;
		}
		setSelectedSlot(provider.getFirstSlot());
	}

	@Override
	public int getSelected() {
		return selectedSlot;
	}
}
