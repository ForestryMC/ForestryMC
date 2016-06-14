/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.factory.blocks;

import java.util.Map;

import net.minecraft.item.ItemStack;

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;
import forestry.core.items.ItemBlockNBT;

public class BlockRegistryFactory extends BlockRegistry {
	public final BlockFactoryTESR bottler;
	public final BlockFactoryTESR carpenter;
	public final BlockFactoryTESR centrifuge;
	public final BlockFactoryTESR fermenter;
	public final BlockFactoryTESR moistener;
	public final BlockFactoryTESR squeezer;
	public final BlockFactoryTESR still;
	public final BlockFactoryTESR rainmaker;

	public final BlockFactoryPlain fabricator;
	public final BlockFactoryPlain raintank;
	public final BlockFactoryPlain worktable;

	private final Map<BlockDistillVatType, BlockDistillVat> distillVatBlockMap;

	public BlockRegistryFactory() {
		bottler = new BlockFactoryTESR(BlockTypeFactoryTesr.BOTTLER);
		registerBlock(bottler, new ItemBlockForestry(bottler), "bottler");

		carpenter = new BlockFactoryTESR(BlockTypeFactoryTesr.CARPENTER);
		registerBlock(carpenter, new ItemBlockForestry(carpenter), "carpenter");

		centrifuge = new BlockFactoryTESR(BlockTypeFactoryTesr.CENTRIFUGE);
		registerBlock(centrifuge, new ItemBlockForestry(centrifuge), "centrifuge");

		fermenter = new BlockFactoryTESR(BlockTypeFactoryTesr.FERMENTER);
		registerBlock(fermenter, new ItemBlockForestry(fermenter), "fermenter");

		moistener = new BlockFactoryTESR(BlockTypeFactoryTesr.MOISTENER);
		registerBlock(moistener, new ItemBlockForestry(moistener), "moistener");

		squeezer = new BlockFactoryTESR(BlockTypeFactoryTesr.SQUEEZER);
		registerBlock(squeezer, new ItemBlockForestry(squeezer), "squeezer");

		still = new BlockFactoryTESR(BlockTypeFactoryTesr.STILL);
		registerBlock(still, new ItemBlockForestry(still), "still");

		rainmaker = new BlockFactoryTESR(BlockTypeFactoryTesr.RAINMAKER);
		registerBlock(rainmaker, new ItemBlockForestry(rainmaker), "rainmaker");

		fabricator = new BlockFactoryPlain(BlockTypeFactoryPlain.FABRICATOR);
		registerBlock(fabricator, new ItemBlockNBT(fabricator), "fabricator");

		raintank = new BlockFactoryPlain(BlockTypeFactoryPlain.RAINTANK);
		registerBlock(raintank, new ItemBlockNBT(raintank), "raintank");

		worktable = new BlockFactoryPlain(BlockTypeFactoryPlain.WORKTABLE);
		registerBlock(worktable, new ItemBlockNBT(worktable), "worktable");

		distillVatBlockMap = BlockDistillVat.create();
		for (BlockDistillVat block : distillVatBlockMap.values()) {
			registerBlock(block, new ItemBlockForestry(block), "distillvat." + block.getDistillVatType());
		}

}
	public ItemStack getDistillVatBlock(BlockDistillVatType type, int stacksize) {
		BlockDistillVat distillVatBlock = distillVatBlockMap.get(type);
		return new ItemStack(distillVatBlock, stacksize);
	}

}
