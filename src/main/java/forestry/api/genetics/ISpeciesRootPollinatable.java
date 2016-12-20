package forestry.api.genetics;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @since 5.12.16
 * @author Nedelosk
 */
public interface ISpeciesRootPollinatable extends ISpeciesRoot {

	ICheckPollinatable createPollinatable(IIndividual individual);

	@Nullable
	IPollinatable tryConvertToPollinatable(@Nullable GameProfile owner, World world, final BlockPos pos, final IIndividual pollen);
	
}
