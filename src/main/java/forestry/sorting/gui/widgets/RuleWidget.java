package forestry.sorting.gui.widgets;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import forestry.api.core.tooltips.ToolTip;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.api.genetics.filter.IFilterLogic;
import forestry.api.genetics.filter.IFilterRuleType;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.utils.SoundUtil;
import forestry.sorting.gui.GuiGeneticFilter;
import forestry.sorting.gui.ISelectableProvider;

public class RuleWidget extends Widget implements ISelectableProvider<IFilterRuleType> {
	private static final ImmutableSet<IFilterRuleType> ENTRIES = createEntries();

	private final Direction facing;
	private final GuiGeneticFilter gui;

	public RuleWidget(WidgetManager manager, int xPos, int yPos, Direction facing, GuiGeneticFilter gui) {
		super(manager, xPos, yPos);
		this.facing = facing;
		this.gui = gui;
	}

	@Override
	public void draw(PoseStack transform, int startY, int startX) {
		int x = xPos + startX;
		int y = yPos + startY;
		IFilterLogic logic = gui.getLogic();
		IFilterRuleType rule = logic.getRule(facing);
		draw(manager.gui, rule, transform, y, x);

		if (this.gui.selection.isSame(this)) {
			RenderSystem.setShaderTexture(0, SelectionWidget.TEXTURE);
			gui.blit(transform, x - 1, y - 1, 212, 0, 18, 18);
		}
	}

	@Override
	public Collection<IFilterRuleType> getEntries() {
		return ENTRIES;
	}

	@Override
	public void draw(GuiForestry gui, IFilterRuleType selectable, PoseStack transform, int y, int x) {
		RenderSystem.setShaderTexture(0, selectable.getTextureMap());

		TextureAtlasSprite sprite = selectable.getSprite();
		GuiComponent.blit(transform, x, y, gui.getBlitOffset(), 16, 16, sprite);
	}

	@Override
	public Component getName(IFilterRuleType selectable) {
		return Component.translatable("for.gui.filter." + selectable.getUID());
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
	public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {
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
