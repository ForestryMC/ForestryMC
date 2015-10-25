package forestry.core.utils;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldUtils {

	public static boolean blockExists(World world, BlockPos pos) {
		return pos.getY() >= 0 && pos.getY() < 256
				? world.getChunkProvider().chunkExists(pos.getX() >> 4, pos.getZ() >> 4) : false;
	}

	public static boolean checkChunksExist(World world, int p_72904_1_, int p_72904_2_, int p_72904_3_, int p_72904_4_,
			int p_72904_5_, int p_72904_6_) {
		if (p_72904_5_ >= 0 && p_72904_2_ < 256) {
			p_72904_1_ >>= 4;
			p_72904_3_ >>= 4;
			p_72904_4_ >>= 4;
			p_72904_6_ >>= 4;

			for (int k1 = p_72904_1_; k1 <= p_72904_4_; ++k1) {
				for (int l1 = p_72904_3_; l1 <= p_72904_6_; ++l1) {
					if (!world.getChunkProvider().chunkExists(k1, l1)) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

}
