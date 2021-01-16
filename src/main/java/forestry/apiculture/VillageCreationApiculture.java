///*******************************************************************************
// * Copyright (c) 2011-2014 SirSengir.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the GNU Lesser Public License v3
// * which accompanies this distribution, and is available at
// * http://www.gnu.org/licenses/lgpl-3.0.txt
// *
// * Various Contributors including, but not limited to:
// * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
// ******************************************************************************/
//package forestry.apiculture;
//
//import javax.annotation.Nullable;
//import java.util.List;
//import java.util.Random;
//
//import net.minecraft.util.Direction;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.world.gen.feature.StructureIO;
//import net.minecraft.world.gen.feature.StructurePiece;
//import net.minecraft.world.gen.feature.VillagePieces;
//
//import net.minecraftforge.fml.common.registry.VillagerRegistry;
//
//import forestry.apiculture.worldgen.VillageApiaristHouse;
//
//public class VillageCreationApiculture implements VillagerRegistry.IVillageCreationHandler {
//
//	public static void registerVillageComponents() {
//		StructureIO.registerStructureComponent(VillageApiaristHouse.class, "Forestry:BeeHouse");
//	}
////TODO villages and worldgen
//	@Override
//	public VillagePieces.PieceWeight getVillagePieceWeight(Random random, int size) {
//		return new VillagePieces.PieceWeight(VillageApiaristHouse.class, 15, MathHelper.getInt(random, size, 1 + size));
//	}
//
//	@Override
//	public Class<?> getComponentClass() {
//		return VillageApiaristHouse.class;
//	}
//
//	@Override
//	@Nullable
//	public VillagePieces.Village buildComponent(VillagePieces.PieceWeight villagePiece, VillagePieces.Start startPiece, List<StructurePiece> pieces, Random random, int p1, int p2, int p3, Direction facing, int p5) {
//		return VillageApiaristHouse.buildComponent(startPiece, pieces, random, p1, p2, p3, facing, p5);
//	}
//}
