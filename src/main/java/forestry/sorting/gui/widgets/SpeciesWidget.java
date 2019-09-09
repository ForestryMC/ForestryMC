package forestry.sorting.gui.widgets;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.root.IRootDefinition;

import forestry.api.genetics.IAlleleForestrySpecies;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IFilterLogic;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.utils.SoundUtil;
import forestry.sorting.gui.GuiGeneticFilter;
import forestry.sorting.gui.ISelectableProvider;

public class SpeciesWidget extends Widget implements ISelectableProvider<IAlleleSpecies> {
	private final static ImmutableMap<IAlleleSpecies, ItemStack> ITEMS = createEntries();
	private final ImmutableSet<IAlleleSpecies> entries;

	private final Direction facing;
	private final int index;
	private final boolean active;
	private final GuiGeneticFilter gui;

	public SpeciesWidget(WidgetManager manager, int xPos, int yPos, Direction facing, int index, boolean active, GuiGeneticFilter gui) {
		super(manager, xPos, yPos);
		this.facing = facing;
		this.index = index;
		this.active = active;
		this.gui = gui;
		ImmutableSet.Builder<IAlleleSpecies> entries = ImmutableSet.builder();
		for (IRootDefinition definition : GeneticsAPI.apiInstance.getRoots().values()) {
			if (!definition.isRootPresent() || !(definition.get() instanceof IForestrySpeciesRoot)) {
				continue;
			}
			IForestrySpeciesRoot root = (IForestrySpeciesRoot) definition.get();
			IBreedingTracker tracker = root.getBreedingTracker(manager.minecraft.world, manager.minecraft.player.getGameProfile());
			for (String uid : tracker.getDiscoveredSpecies()) {
				IAllele allele = GeneticsAPI.apiInstance.getAlleleRegistry().getAllele(uid).orElse(null);
				if (allele instanceof IAlleleSpecies) {
					IAlleleSpecies species = (IAlleleSpecies) allele;
					entries.add(species);
				}
			}
		}
		this.entries = entries.build();
	}

	@Override
	public void draw(int startX, int startY) {
		int x = xPos + startX;
		int y = yPos + startY;
		IFilterLogic logic = gui.getLogic();
		IAlleleSpecies allele = (IAlleleSpecies) logic.getGenomeFilter(facing, index, active);
		if (allele != null) {
			GuiUtil.drawItemStack(manager.gui, ITEMS.getOrDefault(allele, ItemStack.EMPTY), x, y);
		}
		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		if (this.gui.selection.isSame(this)) {
			textureManager.bindTexture(SelectionWidget.TEXTURE);
			gui.blit(x - 1, y - 1, 212, 0, 18, 18);
		}
	}

	@Override
	public ImmutableSet<IAlleleSpecies> getEntries() {
		return entries;
	}

	@Override
	public void onSelect(@Nullable IAlleleSpecies selectable) {
		IFilterLogic logic = gui.getLogic();
		if (logic.setGenomeFilter(facing, index, active, selectable)) {
			logic.sendToServer(facing, (short) index, active, selectable);
		}
		if (gui.selection.isSame(this)) {
			gui.onModuleClick(this);
		}
		SoundUtil.playButtonClick();
	}

	@Override
	public void draw(GuiForestry gui, IAlleleSpecies selectable, int x, int y) {
		GuiUtil.drawItemStack(gui, ITEMS.getOrDefault(selectable, ItemStack.EMPTY), x, y);
	}

	@Override
	public String getName(IAlleleSpecies selectable) {
		return selectable.getDisplayName().getFormattedText();
	}

	@Nullable
	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		IFilterLogic logic = gui.getLogic();
		IAlleleForestrySpecies allele = (IAlleleForestrySpecies) logic.getGenomeFilter(facing, index, active);
		if (allele == null) {
			return null;
		}
		ToolTip tooltip = new ToolTip();
		tooltip.add(getName(allele));
		return tooltip;
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {
		ItemStack stack = gui.getMinecraft().player.inventory.getItemStack();
		if (!stack.isEmpty()) {
			Optional<IIndividual> optional = GeneticsAPI.apiInstance.getRootHelper().getIndividual(stack);
			if (optional.isPresent()) {
				IIndividual individual = optional.get();
				IGenome genome = individual.getGenome();
				onSelect(mouseButton == 0 ? genome.getPrimary() : genome.getSecondary());
				return;
			}
		}
		if (mouseButton == 1) {
			onSelect(null);
		} else {
			SoundUtil.playButtonClick();
			gui.onModuleClick(this);
		}
	}

	private static ImmutableMap<IAlleleSpecies, ItemStack> createEntries() {
		ImmutableMap.Builder<IAlleleSpecies, ItemStack> entries = ImmutableMap.builder();
		for (IRootDefinition definition : GeneticsAPI.apiInstance.getRoots().values()) {
			if (!definition.isRootPresent() || !(definition.get() instanceof IForestrySpeciesRoot)) {
				continue;
			}
			IForestrySpeciesRoot<IIndividual> root = (IForestrySpeciesRoot<IIndividual>) definition.get();
			for (IIndividual individual : root.getIndividualTemplates()) {
				IAlleleSpecies species = individual.getGenome().getPrimary();
				ItemStack itemStack = root.getTypes().createStack(root.templateAsIndividual(root.getTemplates().getTemplate(species.getRegistryName().toString())), root.getIconType());
				entries.put(species, itemStack);
			}
		}
		return entries.build();
	}
}
