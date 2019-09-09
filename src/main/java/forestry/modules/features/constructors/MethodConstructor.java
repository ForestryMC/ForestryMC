package forestry.modules.features.constructors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import forestry.modules.features.IFeatureConstructor;

public class MethodConstructor<O> implements IFeatureConstructor<O> {
	private final Class<? extends O> objectClass;
	private final String methodName;
	private final Class[] parameterTypes;
	private final Object[] parameters;

	public MethodConstructor(Class<? extends O> objectClass, String methodName) {
		this(objectClass, methodName, new Class[0], new Object[0]);
	}

	public MethodConstructor(Class<? extends O> objectClass, String methodName, Class[] parameterTypes, Object[] parameters) {
		this.objectClass = objectClass;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.parameters = parameters;
	}

	@Override
	public O createObject() {
		try {
			Method method = objectClass.getMethod(methodName, parameterTypes);
			Object object = method.invoke(null, parameters);
			if (objectClass.isInstance(object)) {
				return objectClass.cast(object);
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new IllegalArgumentException("Failed to create object based on the class '" + objectClass +
				"', the parameter types '" + Arrays.toString(parameterTypes) +
				"' and the parameters '" + Arrays.toString(parameters) + "'.", e);
		}
		throw new IllegalStateException("The object created with the method '" + methodName + "()' of the class '" + objectClass + "' is not an instance of this class.");
	}
}
