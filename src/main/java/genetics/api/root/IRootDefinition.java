package genetics.api.root;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import genetics.api.IGeneticApiInstance;

/**
 * A optional that describes a {@link IIndividualRoot}.
 * <p>
 * You can get the optional of an {@link IIndividualRoot} by calling
 * {@link IGeneticApiInstance#getRoot(String)} or {@link IIndividualRootBuilder#getDefinition()} at
 * the definition builder of the definition.
 *
 * @param <R> @param <R> The type of the root of the individual.
 */
public interface IRootDefinition<R extends IIndividualRoot> {
    Optional<R> maybe();

    /**
     * Returns the described definition of this optional.
     *
     * @return The described definition of this optional.
     * @throws NoSuchElementException if the definition is null.
     */
    R get();

    <T extends IIndividualRoot> T cast();

    R orElse(R other);

    /**
     * Return {@code true} if there is a definition present, otherwise {@code false}.
     *
     * @return {@code true} if there is a definition present, otherwise {@code false}
     */
    boolean isPresent();

    boolean test(Predicate<? super R> predicate);

    Optional<R> filter(Predicate<? super R> predicate);

    void ifPresent(Consumer<R> consumer);

    <U> Optional<U> map(Function<? super R, ? extends U> mapper);
}
