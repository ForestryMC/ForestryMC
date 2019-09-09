package forestry.sorting.gui.widgets;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import forestry.core.config.Constants;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.gui.widgets.WidgetScrollBar;
import forestry.core.utils.Translator;
import forestry.sorting.gui.GuiGeneticFilter;
import forestry.sorting.gui.ISelectableProvider;

public class SelectionWidget extends Widget {
	public static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/filter_selection.png");
	final WidgetScrollBar scrollBar;
	@Nullable
	private SelectionLogic logic;
	final GuiGeneticFilter gui;

	public SelectionWidget(WidgetManager manager, int xPos, int yPos, WidgetScrollBar scrollBar, GuiGeneticFilter gui) {
		super(manager, xPos, yPos);
		this.width = 212;
		this.height = 88;
		this.scrollBar = scrollBar;
		this.gui = gui;
	}

	public <S> void setProvider(@Nullable ISelectableProvider<S> provider) {
		if (provider == null) {
			logic = null;
		} else {
			this.logic = new SelectionLogic<>(this, provider);
		}
	}

	public boolean isSame(ISelectableProvider provider) {
		return logic != null && logic.isSame(provider);
	}

	@Nullable
	public SelectionLogic getLogic() {
		return logic;
	}

	@Override
	public void draw(int startX, int startY) {
		if (logic == null) {
			return;
		}
		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		textureManager.bindTexture(TEXTURE);
		manager.gui.blit(startX + xPos, startY + yPos, 0, 0, width, height);
		logic.draw();

		manager.minecraft.fontRenderer.drawString(Translator.translateToLocal("for.gui.filter.seletion"), startX + xPos + 12, startY + yPos + 4, manager.gui.getFontColor().get("gui.title"));
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return logic != null && super.isMouseOver(mouseX, mouseY);
	}

	@Nullable
	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		if (logic == null) {
			return null;
		}
		return logic.getToolTip(mouseX, mouseY);
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {
		if (logic == null) {
			return;
		}
		logic.select(mouseX, mouseY);
	}

	public void filterEntries(String filter) {
		if (logic == null) {
			return;
		}
		logic.filterEntries(filter);
	}
}
