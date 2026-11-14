# Sinterklaas-mod

Een Minecraft Java-mod met Sint, Pieten, pakjes en pepernoten. Gemaakt door vader en zoon.

**Versie 0.5.0** voor Minecraft Java 26.2 (Fabric).

## Wat zit erin

- **Sinterklaas** en **zes Pieten** (Roetveegpiet, Hoofdpiet, Pakjespiet, Wegwijspiet, Koekpiet, Malle Pietje), elk met een eigen spawn-ei. Ze zijn vriendelijk en lopen rond.
- **Seizoen**: van **15 november t/m 5 december** verschijnen Sint en de Pieten vanzelf bij dorpen, en hoe dichter bij 5 december, hoe meer. Op **6 december vertrekt iedereen** weer (ook wie uit een spawn-ei kwam). Spawn-eieren werken het hele jaar; wie na 5 december uit een ei komt, blijft tot de 6 december van het jaar erna.
- **Praten**: Sint en de Pieten zeggen af en toe iets in de chat en geven antwoord als je op ze rechtsklikt.
- **Pakjes**: een verrassing! Rechtsklik op een neergezet pakje om het te openen.
  - Een gewoon pakje mag open van **5 december t/m 23 december**. Daarbuiten zegt het spel: *"5 december is het nog niet, je moet even wachten, Piet"*.
  - Een **pakje uit de schoen** mag altijd open.
  - Meestal zitten er pepernoten of koekjes in. Zeldzaam: een chocoladeletter, een mijter, een zakje pepernoten of een spawn-ei van een Piet. Heel zeldzaam: een gedichtje in de chat.
- **Schoen**: zet een schoen neer en leg er met een rechtsklik een **wortel voor het paard** in. De volgende ochtend (in Minecraft-tijd, dus ook na slapen) ligt er een pakje in; rechtsklik om het te pakken. De wortel is dan op: voor een nieuw pakje moet er een nieuwe wortel in. Zet je je schoen **zonder wortel**, dan ligt er de volgende ochtend alleen een briefje van Piet in...
- **Pepernoten** en een **bord**: leg tot vier pepernoten op een bord (rechtsklik met een pepernoot). Met een lege hand pak je er weer één af.
- **Mijter**: draag hem op je hoofd. Hij is lekker hoog en beschermt net zo goed als een ijzeren helm.

### Hoe kom je aan pakjes?

1. Schoen zetten, met een wortel erin.
2. Pieten laten er af en toe één vallen (elke 10 tot 20 minuten iets: een pakje of een paar pepernoten).
3. Zelf maken: papier + pepernoot + lint.
4. Zeldzaam in kisten in dorpen, kerkers en mijnschachten.

### Recepten

| Wat | Hoe |
|---|---|
| Pepernoten (8) | tarwe + suiker + cacaobonen |
| Lint (2) | draad + rode kleurstof |
| Pakje | papier + pepernoot + lint |
| Zakje pepernoten | 8 pepernoten + papier (en weer terug: zakje → 8 pepernoten) |
| Chocoladeletter (2) | 2 cacaobonen + suiker + emmer melk |
| Schoen | draad linksboven, 2 leer eronder |
| Bord (2) | klei, blauwe kleurstof, klei op een rij |

## Installeren (voor spelers)

Je hebt nodig: **Minecraft Java Edition 26.2**.

1. Installeer **Fabric Loader** voor Minecraft 26.2 met de installer van https://fabricmc.net/use/installer/
   (kies versie 26.2 en klik op *Install*).
2. Download **Fabric API** voor 26.2 (versie 0.161.0+26.2 of nieuwer voor 26.2) via https://modrinth.com/mod/fabric-api
3. Zet deze twee bestanden in je map `mods`:
   - `fabric-api-...+26.2.jar`
   - `sinterklaas-fabric-0.5.0+mc26.2.jar`

   De map `mods` vind je hier:
   - Windows: `%appdata%\.minecraft\mods`
   - macOS: `~/Library/Application Support/minecraft/mods`
   - Linux: `~/.minecraft/mods`
4. Start de Minecraft Launcher, kies het profiel **fabric-loader-26.2** en klik op *Spelen*.

Minecraft 26.2 gebruikt Java 25. De launcher regelt dat zelf; je hoeft niets apart te installeren.

Samen spelen op een server? Dan moet de mod (en Fabric API) op de server én bij elke speler staan.

### Uitproberen

Maak een wereld in **Creative**. Open je inventaris en zoek het tabblad **Sinterklaas**. Of typ in de chat:

```
/give @s sinterklaas:pakje
/give @s sinterklaas:schoen
/summon sinterklaas:piet
/summon sinterklaas:sint
```

### Testen met een andere datum (alleen voor beheerders)

De datum is altijd de datum van de server in **Nederlandse tijd** (Europe/Amsterdam); in singleplayer is dat je eigen computer.
Met cheats aan (of als op op een server) kun je doen alsof het een andere dag is:

```
/sinterklaas datum                laat zien welke datum nu geldt
/sinterklaas datum 2026-12-04     pakjes blijven dicht
/sinterklaas datum 2026-12-05     pakjes mogen open
/sinterklaas datum reset          terug naar de echte datum
```

De testdatum geldt voor het openen van pakjes én voor het seizoen van Sint en de Pieten, en verdwijnt als je de wereld afsluit.

**Let op:** zet je de testdatum op 6 december of later, dan vertrekken alle Pieten en Sint die er op dat moment zijn. Ze komen niet terug als je de datum daarna reset.

## Zelf aanpassen (voor makers)

| Wat wil je veranderen? | Welk bestand |
|---|---|
| Wat Sint en de Pieten zeggen, de gedichtjes en het briefje in de schoen | `core/src/main/resources/sinterklaas/teksten.json` |
| Wat er in een pakje zit en hoe vaak | `fabric/src/main/resources/data/sinterklaas/loot_table/gameplay/pakje_inhoud.json` |
| Wat een Piet laat vallen | `.../loot_table/gameplay/piet_cadeau.json` |
| De kans op een pakje in kisten | `.../loot_table/chests/kist_extra.json` |
| De plaatjes van Sint, Pieten, pakje, schoen en bord | map `textures/` |
| Van welke dag tot welke dag pakjes open mogen | `core/src/main/java/nl/sinterklaas/core/PakjesRegels.java` |
| Wanneer het seizoen begint en hoe snel het drukker wordt | `core/src/main/java/nl/sinterklaas/core/SeizoenRegels.java` |
| Namen van voorwerpen | `fabric/src/main/resources/assets/sinterklaas/lang/nl_nl.json` |

De loot-tables, recepten, modellen en item-plaatjes worden gemaakt door `tools/maak_assets.py`. Pas je iets blijvend aan, doe dat dan in dat script (anders overschrijft het script je wijziging als je het opnieuw draait).

De zinnen in `teksten.json` zijn nu nog voorlopig (ze beginnen met bijvoorbeeld `[koek 1]`). Vervang ze door je eigen tekst; je mag zoveel zinnen toevoegen als je wilt.

### Bouwen

Nodig: Java 25 (JDK). Gradle wordt vanzelf gedownload.

```bash
./gradlew build
```

De jar staat daarna in `fabric/build/libs/`.

| Opdracht | Wat doet het |
|---|---|
| `./gradlew build` | Bouwt de jar en draait de tests van de spelregels |
| `./gradlew :core:test` | Alleen de tests van de spelregels (4 december, 5 december, tijdzone, schoen, seizoen) |
| `./gradlew :fabric:runClient` | Start Minecraft met de mod om te spelen |
| `./gradlew :fabric:runClientGameTest` | Automatische proef in een echte client: controleert 76 dingen en maakt screenshots |
| `python3 tools/maak_assets.py` | Maakt modellen, plaatjes, loot-tables en recepten opnieuw |

### Hoe zit het in elkaar

- `core/` — de spelregels, zonder Minecraft-code: datumregel, tijdzone, seizoen, schoen, praatpauzes, Piet-varianten, teksten. Los te testen en herbruikbaar op een andere loader.
- `fabric/` — alles wat met Minecraft/Fabric praat: blokken, items, entiteiten, tekenen, commando.
- `textures/` — de bronplaatjes (64x64).
- `tools/` — hulpscript voor afgeleide assets en data.

Versies: Minecraft 26.2, Fabric Loader 0.20.1, Fabric API 0.161.0+26.2, Loom 1.19.4, Gradle 9.8.2.

## Licentie en meedoen

De mod valt onder de MIT-licentie (zie `LICENSE`), en dat geldt ook voor de plaatjes. Iedereen mag hem gebruiken, kopiëren en aanpassen.

- **Naam vermelden is verplicht:** laat de copyrightregel en de licentietekst staan, en verwijs het liefst naar https://github.com/CoenBresser/minecraft-sinterklaas-mod.
- **Bijdragen is een verzoek:** heb je iets verbeterd, stuur het dan graag terug met een pull request. Hoe dat werkt staat in `CONTRIBUTING.md`.
