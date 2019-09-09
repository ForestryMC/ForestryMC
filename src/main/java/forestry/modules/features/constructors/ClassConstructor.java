package forestry.modules.features.constructors;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import forestry.modules.features.IFeatureConstructor;

public class ClassConstructor<O> implements IFeatureConstructor<O> {
	private final Class<? extends O> objectClass;
	private final Class[] parameterTypes;
	private final Object[] parameters;

	public ClassConstructor(Class<? extends O> objectClass) {
		this(objectClass, new Class[0], new Object[0]);
	}

	public ClassConstructor(Class<? extends O> objectClass, Class[] parameterTypes, Object[] parameters) {
		this.objectClass = objectClass;
		this.parameterTypes = parameterTypes;
		this.parameters = parameters;
	}

	@Override
	public O createObject() {
		try {
			Constructor<? extends O> method = objectClass.getConstructor(parameterTypes);
			return method.newInstance(parameters);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			throw new IllegalArgumentException("Failed to create object based on the class '" + objectClass +
				"', the parameter types '" + Arrays.toString(parameterTypes) +
				"' and the parameters '" + Arrays.toString(parameters) + "'.", e);
		}
	}
}
