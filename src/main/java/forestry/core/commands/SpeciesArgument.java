//package forestry.core.commands;
//
//import java.util.Collection;
//import java.util.concurrent.CompletableFuture;
//
//import com.mojang.brigadier.StringReader;
//import com.mojang.brigadier.arguments.ArgumentType;
//import com.mojang.brigadier.context.CommandContext;
//import com.mojang.brigadier.exceptions.CommandSyntaxException;
//import com.mojang.brigadier.suggestion.Suggestions;
//import com.mojang.brigadier.suggestion.SuggestionsBuilder;
//
//import genetics.api.alleles.IAlleleSpecies;
//import genetics.api.individual.IChromosomeAllele;
//
//public class SpeciesArgument<A extends IAlleleSpecies> implements ArgumentType<A> {
//
//	private IChromosomeAllele<A> type;
//
//	public SpeciesArgument(IChromosomeAllele<A> type) {
//		this.type = type;
//	}
//
//	@Override
//	public A parse(StringReader reader) throws CommandSyntaxException {
//		return reader.r;
//	}
//
//	@Override
//	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
//		return null;
//	}
//
//	@Override
//	public Collection<String> getExamples() {
//		return null;
//	}
//}
