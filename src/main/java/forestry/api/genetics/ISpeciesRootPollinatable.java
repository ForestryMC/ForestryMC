package forestry.api.genetics;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Nedelosk
 * @since 5.12.16
 */
public interface ISpeciesRootPollinatable extends ISpeciesRoot {

	ICheckPollinatable createPollinatable(IIndividual individual);

	@Nullable
	IPollinatable tryConvertToPollinatable(@Nullable GameProfile owner, World world, final BlockPos pos, final IIndividual pollen);

}
