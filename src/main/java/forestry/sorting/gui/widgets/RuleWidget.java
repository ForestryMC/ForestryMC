package forestry.sorting.gui.widgets;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.EnumFacing;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFilterLogic;
import forestry.api.genetics.IFilterRuleType;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.utils.SoundUtil;
import forestry.core.utils.Translator;
import forestry.sorting.gui.GuiGeneticFilter;
import forestry.sorting.gui.ISelectableProvider;

public class RuleWidget extends Widget implements ISelectableProvider<IFilterRuleType> {
	private static final ImmutableSet<IFilterRuleType> ENTRIES = createEntries();

	private final EnumFacing facing;
	private final GuiGeneticFilter gui;

	public RuleWidget(WidgetManager manager, int xPos, int yPos, EnumFacing facing, GuiGeneticFilter gui) {
		super(manager, xPos, yPos);
		this.facing = facing;
		this.gui = gui;
	}

	@Override
	public void draw(int startX, int startY) {
		int x = xPos + startX;
		int y = yPos + startY;
		IFilterLogic logic = gui.getLogic();
		IFilterRuleType rule = logic.getRule(facing);
		draw(manager.gui, rule, x, y);
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		if (this.gui.selection.isSame(this)) {
			textureManager.bindTexture(SelectionWidget.TEXTURE);
			gui.drawTexturedModalRect(x - 1, y - 1, 212, 0, 18, 18);
		}
	}

	@Override
	public Collection<IFilterRuleType> getEntries() {
		return ENTRIES;
	}

	@Override
	public void draw(GuiForestry gui, IFilterRuleType selectable, int x, int y) {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		textureManager.bindTexture(selectable.getTextureMap());

		TextureAtlasSprite sprite = selectable.getSprite();
		gui.drawTexturedModalRect(x, y, sprite, 16, 16);
	}

	@Override
	public String getName(IFilterRuleType selectable) {
		return Translator.translateToLocal("for.gui.filter." + selectable.getUID());
	}

	@Override
	public void onSelect(IFilterRuleType selectable) {
		IFilterLogic logic = gui.getLogic();
		if (logic.setRule(facing, selectable)) {
			logic.sendToServer(facing, selectable);
		}
		if (gui.selection.isSame(this)) {
			gui.onModuleClick(this);
		}
		SoundUtil.playButtonClick();
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 1) {
			onSelect(AlleleManager.filterRegistry.getDefaultRule());
		} else {
			SoundUtil.playButtonClick();
			gui.onModuleClick(this);
		}
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		IFilterLogic logic = gui.getLogic();
		IFilterRuleType rule = logic.getRule(facing);
		ToolTip tooltip = new ToolTip();
		tooltip.add(getName(rule));
		return tooltip;
	}

	private static ImmutableSet<IFilterRuleType> createEntries() {
		ImmutableSet.Builder<IFilterRuleType> entries = ImmutableSet.builder();
		for (IFilterRuleType rule : AlleleManager.filterRegistry.getRules()) {
			entries.add(rule);
		}
		return entries.build();
	}
}
