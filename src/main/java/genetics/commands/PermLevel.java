package genetics.commands;

import java.util.function.Predicate;

import net.minecraft.commands.CommandSourceStack;

public enum PermLevel implements Predicate<CommandSourceStack> {

    EVERYONE(0), ADMIN(2);
    public final int permLevel;

    PermLevel(int permLevel) {
        this.permLevel = permLevel;
    }

    @Override
    public boolean test(CommandSourceStack commandSource) {
        return commandSource.hasPermission(permLevel);
    }
}
