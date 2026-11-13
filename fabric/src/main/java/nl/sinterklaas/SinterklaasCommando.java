package nl.sinterklaas;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.time.LocalDate;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import nl.sinterklaas.core.SinterklaasKlok;

/**
 * Testschakelaar voor beheerders (ops):
 *   /sinterklaas datum               laat de datum zien die nu geldt
 *   /sinterklaas datum 2026-12-05    doet alsof het die dag is
 *   /sinterklaas datum reset         terug naar de echte datum
 */
public final class SinterklaasCommando {
	static void registreer(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("sinterklaas")
			.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(Commands.literal("datum")
				.executes(c -> toon(c.getSource()))
				.then(Commands.literal("reset").executes(c -> reset(c.getSource())))
				.then(Commands.argument("datum", StringArgumentType.word())
					.executes(c -> zet(c.getSource(), StringArgumentType.getString(c, "datum"))))));
	}

	private static int toon(CommandSourceStack bron) {
		SinterklaasKlok klok = SinterklaasMod.KLOK;
		String sleutel = klok.heeftTestDatum() ? "commando.sinterklaas.datum.nu_test" : "commando.sinterklaas.datum.nu";
		bron.sendSuccess(() -> Component.translatable(sleutel, klok.vandaag().toString()), false);
		return 1;
	}

	private static int zet(CommandSourceStack bron, String tekst) {
		Optional<LocalDate> datum = SinterklaasKlok.leesDatum(tekst);
		if (datum.isEmpty()) {
			bron.sendFailure(Component.translatable("commando.sinterklaas.datum.fout"));
			return 0;
		}
		SinterklaasMod.KLOK.zetTestDatum(datum.get());
		bron.sendSuccess(() -> Component.translatable("commando.sinterklaas.datum.gezet", datum.get().toString()), true);
		return 1;
	}

	private static int reset(CommandSourceStack bron) {
		SinterklaasMod.KLOK.reset();
		bron.sendSuccess(() -> Component.translatable("commando.sinterklaas.datum.reset", SinterklaasMod.KLOK.vandaag().toString()), true);
		return 1;
	}

	private SinterklaasCommando() {}
}
