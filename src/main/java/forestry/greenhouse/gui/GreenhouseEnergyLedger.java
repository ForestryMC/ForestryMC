package forestry.greenhouse.gui;

import forestry.core.gui.ledgers.Ledger;
import forestry.core.gui.ledgers.LedgerManager;
import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.Translator;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;

public class GreenhouseEnergyLedger extends Ledger {

	final IGreenhouseControllerInternal controller;
	
	public GreenhouseEnergyLedger(LedgerManager ledgerManager, IGreenhouseControllerInternal controller) {
		super(ledgerManager, "power");
		maxHeight = 48;
		this.controller = controller;
	}

	@Override
	public void draw(int x, int y) {
		// Draw background
		drawBackground(x, y);

		// Draw icon
		drawSprite(TextureManagerForestry.getInstance().getDefault("misc/energy"), x + 3, y + 4);

		if (!isFullyOpened()) {
			return;
		}

		drawHeader(Translator.translateToLocal("for.gui.energy"), x + 22, y + 8);

		drawSubheader(Translator.translateToLocal("for.gui.stored") + ':', x + 22, y + 20);
		drawText(controller.getEnergyManager().getEnergyStored() + " RF", x + 22, y + 32);
	}

	@Override
	public String getTooltip() {
		return Translator.translateToLocal("for.gui.energy") + ": " + controller.getEnergyManager().getEnergyStored() + " RF/t";
	}
}
