package genetics.api.root;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.nbt.CompoundNBT;

import genetics.api.individual.IGenome;
import genetics.api.individual.IGenomeWrapper;
import genetics.api.individual.IIndividual;

public class SimpleIndividualRoot<I extends IIndividual> extends IndividualRoot<I> {
	protected final Class<? extends I> individualClass;

	public SimpleIndividualRoot(IRootContext<I> context, Class<? extends I> individualClass) {
		super(context);
		this.individualClass = individualClass;
		createDefault();
	}

	@Override
	public I create(CompoundNBT compound) {
		try {
			Constructor<? extends I> constructor = individualClass.getConstructor(CompoundNBT.class);
			constructor.setAccessible(true);
			return constructor.newInstance(compound);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("The individual class of the root with the uid \'" + uid + "\' doesn't has default constructor with the following parameter types (CompoundNBT). " +
				"Please create a constructor for these types or implement the create(CompoundNBT) method of the root.", e);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Failed to instantiate an instance of the individual class of the root with the uid \'" + uid + "\'.", e);
		}
	}

	@Override
	public I create(IGenome genome) {
		try {
			Constructor<? extends I> constructor = individualClass.getConstructor(IGenome.class);
			constructor.setAccessible(true);
			return constructor.newInstance(genome);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("The individual class of the root with the uid \'" + uid + "\' doesn't has default constructor with the following parameter types (IGenome). " +
				"Please create a constructor for these types or implement the create(IGenome) method of the root.", e);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Failed to instantiate an instance of the individual class of the root with the uid \'" + uid + "\'.", e);
		}
	}

	@Override
	public I create(IGenome genome, IGenome mate) {
		try {
			Constructor<? extends I> constructor = individualClass.getConstructor(IGenome.class, IGenome.class);
			constructor.setAccessible(true);
			return constructor.newInstance(genome, mate);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("The individual class of the root with the uid \'" + uid + "\' doesn't has default constructor with the following parameter types (IGenome,IGenome). " +
				"Please create a constructor for these types or implement the create(IGenome,IGenome) method of the root.", e);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Failed to instantiate an instance of the individual class of the root with the uid \'" + uid + "\'.", e);
		}
	}

	@Override
	public Class<? extends I> getMemberClass() {
		return individualClass;
	}

	@Override
	public IGenomeWrapper createWrapper(IGenome genome) {
		return () -> genome;
	}
}
