package forestry.climatology.gui.ledgers;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import forestry.api.climate.IClimateLogic;
import forestry.climatology.ModuleClimatology;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.ledgers.Ledger;
import forestry.core.gui.ledgers.LedgerManager;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;

public class HabitatformerLedger extends Ledger {
	private final IClimateLogic climateLogic;

	public HabitatformerLedger(LedgerManager manager, IClimateLogic climateLogic) {
		super(manager, "habitatformer");
		maxHeight = 96;
		this.climateLogic = climateLogic;
	}

	@Override
	public void draw(int x, int y) {
		// Draw background
		drawBackground(x, y);
		y += 4;

		int xIcon = x + 3;
		int xBody = x + 10;
		int xHeader = x + 22;

		// Draw icon
		Minecraft minecraft = Minecraft.getMinecraft();
		GuiUtil.drawItemStack(minecraft.fontRenderer, new ItemStack(ModuleClimatology.getBlocks().habitatformer), xIcon, y);
		y += 4;

		if (!isFullyOpened()) {
			return;
		}

		y += drawHeader(Translator.translateToLocal("for.gui.habitatformer"), xHeader, y);
		y += 4;

		y += drawSubheader(Translator.translateToLocal("for.gui.habitatformer.range") + ':', xBody, y);
		y += 3;
		y += drawText(StringUtil.floatAsPercent(climateLogic.getRangeModifier()), xBody, y);
		y += 3;

		y += drawSubheader(Translator.translateToLocal("for.gui.habitatformer.resources") + ':', xBody, y);
		y += 3;
		y += drawText(StringUtil.floatAsPercent(climateLogic.getResourceModifier()), xBody, y);
		y += 3;

		y += drawSubheader(Translator.translateToLocal("for.gui.habitatformer.speed") + ':', xBody, y);
		y += 3;
		drawText(StringUtil.floatAsPercent(climateLogic.getChangeModifier()), xBody, y);
	}

	@Override
	public String getTooltip() {
		return "Ra: " +
			StringUtil.floatAsPercent(climateLogic.getRangeModifier()) +
			" / " +
			"Re: " +
			StringUtil.floatAsPercent(climateLogic.getResourceModifier()) +
			" / " +
			"S: " +
			StringUtil.floatAsPercent(climateLogic.getChangeModifier());
	}
}
