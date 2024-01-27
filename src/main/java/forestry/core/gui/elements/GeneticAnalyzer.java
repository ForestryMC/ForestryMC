package forestry.core.gui.elements;

import java.util.List;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.lwjgl.glfw.GLFW;

import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.api.genetics.gatgets.IDatabasePlugin;
import forestry.api.genetics.gatgets.IDatabaseTab;
import forestry.api.genetics.gatgets.IGeneticAnalyzer;
import forestry.api.genetics.gatgets.IGeneticAnalyzerProvider;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.buttons.StandardButtonTextureSets;
import forestry.core.gui.elements.layouts.ContainerElement;
import forestry.core.gui.widgets.IScrollable;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.utils.NetworkUtil;

import genetics.api.individual.IIndividual;
import genetics.api.root.IRootDefinition;
import genetics.utils.RootUtils;

@OnlyIn(Dist.CLIENT)
public class GeneticAnalyzer extends ContainerElement implements IGeneticAnalyzer, IScrollable {
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
	private final GuiElement itemElement;
	private final ButtonElement leftButton;
	private final ButtonElement rightButton;
	private final ButtonElement analyzeButton;

	/* Attributes - State */
	private int selectedSlot = -1;

	public GeneticAnalyzer(Window window, int xPos, int yPos, boolean rightBoarder, IGeneticAnalyzerProvider provider) {
		setSize(189 + (rightBoarder ? 6 : 0), 194);
		setPos(xPos - (rightBoarder ? 6 : 0), yPos);
		window.add(this);
		this.provider = provider;

		//Background Texture
		drawable(32, 0, new Drawable(TEXTURE, 0, 0, 163 + (rightBoarder ? 0 : -6), 166));
		//Text Area
		scrollable = new ScrollableElement();
		scrollable.setPreferredBounds(32 + 10, 8, 145, 150);
		add(scrollable);
		scrollableContent = new DatabaseElement(145);
		scrollableContent.setPos(0, 0);
		scrollable.addContent(scrollableContent);
		//Scrollbar
		scrollBar = new ScrollBarElement(SCROLLBAR_BACKGROUND, false, SCROLLBAR_SLIDER);
		scrollBar.setPos(preferredSize.width - 10 - (rightBoarder ? 6 : 0), 12);
		scrollBar.hide();
		add(scrollBar);
		//Side ItemGroups
		tabs = new GeneticAnalyzerTabs(this);
		tabs.setPos(0, 5);
		add(tabs);
		ContainerElement container = pane();
		container.setPos((preferredSize.width + 32 - SELECTION_BAR.uWidth) / 2, 162);
		//Selection Bar at the bottom
		itemElement = container.drawable(SELECTION_BAR);

		leftButton = container.add(new ButtonElement.Builder().pos(itemElement.getX() + 30, itemElement.getY() + 9).textures(StandardButtonTextureSets.LEFT_BUTTON).action((button) -> subtract()).create());
		rightButton = container.add(new ButtonElement.Builder().pos(itemElement.getX() + 64, itemElement.getY() + 9).textures(StandardButtonTextureSets.RIGHT_BUTTON).action((button) -> add()).create());
		analyzeButton = container.add(new ButtonElement.Builder().pos(itemElement.getX() + 80, itemElement.getY() + 2).textures(ANALYZER_BUTTON).action((button) -> NetworkUtil.sendToServer(new PacketGuiSelectRequest(0, provider.getSelectedSlot(selectedSlot)))).create());
		container.add(new AbstractItemElement(itemElement.getX() + 44, itemElement.getY() + 9) {
			@Override
			protected ItemStack getStack() {
				return provider.getSpecimen(selectedSlot);
			}
		});
		/*addEventHandler(GuiEvent.KeyEvent.class, event -> {
			int keyCode = event.getKeyCode();
			if ((keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_RIGHT) && rightButton.isEnabled()) {
				rightButton.onPressed();
			} else if ((keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_LEFT) && leftButton.isEnabled()) {
				leftButton.onPressed();
			} else if (keyCode == GLFW.GLFW_KEY_ENTER && analyzeButton.isEnabled()) {
				analyzeButton.onPressed();
			}
		});*/
	}

	@Override
	public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
		if ((keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_RIGHT) && rightButton.isEnabled()) {
			rightButton.onPressed();
			return true;
		} else if ((keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_LEFT) && leftButton.isEnabled()) {
			leftButton.onPressed();
			return true;
		} else if (keyCode == GLFW.GLFW_KEY_ENTER && analyzeButton.isEnabled()) {
			analyzeButton.onPressed();
			return true;
		}
		return false;
	}

	@Override
	public void init() {
		leftButton.setEnabled(canSubtract());
		rightButton.setEnabled(canAdd());
		updateSelected();
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public IGeneticAnalyzerProvider getProvider() {
		return provider;
	}

	@Override
	public GuiElement getItemElement() {
		return itemElement;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update() {
		if (!isVisible()) {
			return;
		}
		ItemStack stack = provider.getSpecimen(selectedSlot);
		IRootDefinition<IForestrySpeciesRoot> definition = RootUtils.getRoot(stack);
		if (definition.isPresent()) {
			IForestrySpeciesRoot root = definition.get();
			IDatabasePlugin<?> databasePlugin = root.getSpeciesPlugin();
			if (databasePlugin != null) {
				Optional<IIndividual> optionalIndividual = root.create(stack);
				if (optionalIndividual.isPresent()) {
					IIndividual individual = optionalIndividual.get();
					if (individual.isAnalyzed()) {
						tabs.setPlugin(databasePlugin);
						IDatabaseTab tab = tabs.getSelected();
						//Clean the element area
						scrollableContent.clear();
						//Create the new elements
						scrollableContent.init(tab.getMode(), individual, scrollableContent.getWidth() / 2, 0);
						tab.createElements(scrollableContent, individual, stack);
						scrollableContent.forceLayout();
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
		Font fontRenderer = Minecraft.getInstance().font;
		String key = "for.gui.portablealyzer.help";
		//if(state == DatabaseScreenLogic.ScreenState.NO_PLUGIN){
		//key = "for.gui.database.support";
		//}
		List<FormattedCharSequence> lines = fontRenderer.split(Component.translatable(key), scrollable.getPreferredSize().width);
		for (FormattedCharSequence text : lines) {
			scrollableContent.label(text);
		}
		//Disable the scrollbar
		scrollBar.hide();
	}

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

	@Override
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

	@Override
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
