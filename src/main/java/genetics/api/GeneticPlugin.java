package genetics.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraftforge.eventbus.api.EventPriority;

/**
 * This annotation lets Genetics detect mod plugins.
 * All {@link IGeneticPlugin} must have this annotation and a constructor with no arguments.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneticPlugin {
	/**
	 * The priority of the plugin. Plugins will be sorted with respect to this priority level.
	 */
	EventPriority priority() default EventPriority.NORMAL;

	String modId() default "";
}
