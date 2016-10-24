package forestry.api.genetics;

import com.mojang.authlib.GameProfile;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @since 5.12.16
 * @author Nedelosk
 */
public interface ISpeciesRootPollinatable extends ISpeciesRoot {

	ICheckPollinatable createPollinatable(IIndividual individual);
	
	IPollinatable tryConvertToPollinatable(GameProfile owner, World world, final BlockPos pos, final IIndividual pollen);
	
}
