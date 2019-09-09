package genetics.api.organism;

import javax.annotation.Nullable;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import genetics.api.individual.IIndividual;

/**
 * The IGeneticHandler handles the genetic information of an stack whose item represents a specific {@link IOrganismType}.
 */
public interface IOrganismHandler<I extends IIndividual> {

	/**
	 * Creates a stack that contains the genetic information of the given individual in the NBT-Data.
	 *
	 * @param individual The individual that contains the genetic information
	 */
	ItemStack createStack(I individual);

	/**
	 * Creates a individual with the genetic information that the NBT-Data of the stack contains.
	 */
	Optional<I> createIndividual(ItemStack itemStack);

	/**
	 * Writes the genetic information of the given individual to the NBT-Data of the given stack
	 */
	boolean setIndividual(ItemStack itemStack, I individual);

	@Nullable
	CompoundNBT getIndividualData(ItemStack itemStack);

	void setIndividualData(ItemStack itemStack, CompoundNBT compound);
}
