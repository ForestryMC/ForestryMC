//package forestry.plugins.compat;
//
//import forestry.core.blocks.BlockBase;
//import forestry.core.blocks.BlockRegistry;
//import forestry.core.items.ItemBlockForestry;
//import forestry.energy.blocks.BlockEngine;
//import forestry.energy.blocks.BlockTypeEngine;
//
//public class BlockRegistryIC2 extends BlockRegistry {
//	public final BlockBase electricalEngine;
//	public final BlockEngine generator;
//
//	public BlockRegistryIC2() {
//		electricalEngine = new BlockEngine(BlockTypeEngine.ELECTRICAL);
//		registerBlock(electricalEngine, new ItemBlockForestry<>(electricalEngine), "engine_electrical");
//
//		generator = new BlockEngine(BlockTypeEngine.GENERATOR);
//		registerBlock(generator, new ItemBlockForestry<>(generator), "engine_generator");
//	}
//}
