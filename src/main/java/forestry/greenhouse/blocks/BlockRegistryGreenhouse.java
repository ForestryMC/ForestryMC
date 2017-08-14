package forestry.greenhouse.blocks;

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;

public class BlockRegistryGreenhouse extends BlockRegistry {

	public final BlockGreenhouse greenhouseBlock;
	public final BlockClimatiser climatiserBlock;
	public final BlockGreenhouseWindow window;
	public final BlockGreenhouseWindow roofWindow;

	public BlockRegistryGreenhouse() {
		greenhouseBlock = new BlockGreenhouse();
		registerBlock(greenhouseBlock, new ItemBlockForestry(greenhouseBlock), "greenhouse");
		climatiserBlock = new BlockClimatiser();
		registerBlock(climatiserBlock, new ItemBlockForestry(climatiserBlock), "climatiser");

		window = new BlockGreenhouseWindow(false);
		registerBlock(window, new ItemBlockForestry(window), "greenhouse.window");

		roofWindow = new BlockGreenhouseWindow(true);
		registerBlock(roofWindow, new ItemBlockForestry(roofWindow), "greenhouse.window_up");
	}

}
