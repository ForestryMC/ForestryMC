package forestry.sorting.gui.widgets;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

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
    public void draw(MatrixStack transform, int startY, int startX) {
        int x = xPos + startX;
        int y = yPos + startY;
        IFilterLogic logic = gui.getLogic();
        IFilterRuleType rule = logic.getRule(facing);
        draw(manager.gui, rule, transform, y, x);
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        if (this.gui.selection.isSame(this)) {
            textureManager.bindTexture(SelectionWidget.TEXTURE);
            gui.blit(transform, x - 1, y - 1, 212, 0, 18, 18);
        }
    }

    @Override
    public Collection<IFilterRuleType> getEntries() {
        return ENTRIES;
    }

    @Override
    public void draw(GuiForestry gui, IFilterRuleType selectable, MatrixStack transform, int y, int x) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bindTexture(selectable.getTextureMap());

        TextureAtlasSprite sprite = selectable.getSprite();
        AbstractGui.blit(transform, x, y, gui.getBlitOffset(), 16, 16, sprite);
    }

    @Override
    public ITextComponent getName(IFilterRuleType selectable) {
        return new TranslationTextComponent("for.gui.filter." + selectable.getUID());
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
