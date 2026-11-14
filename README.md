# Sinterklaas-mod

Een Minecraft Java-mod met Sint, Pieten, pakjes en pepernoten. Gemaakt door vader en zoon.

Voor Minecraft Java met **Forge**, in twee versies: **26.3** (Forge 66.1.8) en **1.20.1** (Forge 47.4.10). De nieuwste versie download je bij [Releases](https://github.com/CoenBresser/minecraft-sinterklaas-mod/releases/latest); wat er per versie is veranderd staat in `CHANGELOG.md`.

## Wat zit erin

- **Sinterklaas** en **zes Pieten** (Roetveegpiet, Hoofdpiet, Pakjespiet, Wegwijspiet, Koekpiet, Malle Pietje), elk met een eigen spawn-ei. Ze zijn vriendelijk en lopen rond.
- **Seizoen**: van **15 november t/m 5 december** verschijnen Sint en de Pieten vanzelf bij dorpen, en hoe dichter bij 5 december, hoe meer. Op **6 december vertrekt iedereen** weer (ook wie uit een spawn-ei kwam). Spawn-eieren werken het hele jaar; wie na 5 december uit een ei komt, blijft tot de 6 december van het jaar erna.
- **Praten**: Sint en de Pieten zeggen af en toe iets in de chat en geven antwoord als je op ze rechtsklikt.
- **Pakjes**: een verrassing! Rechtsklik op een neergezet pakje om het te openen.
  - Een gewoon pakje mag open van **5 december t/m 23 december**. Daarbuiten zegt het spel: *"5 december is het nog niet, je moet even wachten, Piet"*.
  - Een **pakje uit de schoen** mag altijd open.
  - Meestal zitten er pepernoten of koekjes in. Zeldzaam: een chocoladeletter, een mijter, een zakje pepernoten of een spawn-ei van een Piet. Heel zeldzaam: een gedichtje in de chat.
- **Schoen**: zet een schoen neer en leg er met een rechtsklik een **wortel voor het paard** in. De volgende ochtend (in Minecraft-tijd, dus ook na slapen) ligt er een pakje in; rechtsklik om het te pakken. De wortel is dan op: voor een nieuw pakje moet er een nieuwe wortel in. Zet je je schoen **zonder wortel**, dan ligt er de volgende ochtend alleen een briefje van Piet in...
- **Pepernoten eten**: een pepernoot vult net als een koekje één hongerpoppetje en geeft daarnaast meteen een half hartje terug. Je mag ze altijd eten.
- **Pepernoten** en een **bord**: leg tot vier pepernoten op een bord (rechtsklik met een pepernoot). Met een lege hand pak je er weer één af.
- **Staf**: Sint heeft een gouden staf. Maak er zelf een en rechtsklik: alle Pieten binnen 24 blokken komen 15 seconden lang naar je toe.
- **Mijter**: draag hem op je hoofd. Hij is lekker hoog, beschermt net zo goed als een ijzeren helm, en er zit een lange witte baard aan. Sint draagt hem zelf ook.

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
| Staf | 3 goudstaven boven elkaar, en eentje links daarvan op de bovenste rij |

## Installeren (voor spelers)

Je hebt nodig: **Minecraft Java Edition** met **Forge**. Er zijn twee versies van de mod; kies de jar die bij je Minecraft-versie hoort:

| Minecraft | Forge | Bestand bij de [nieuwste release](https://github.com/CoenBresser/minecraft-sinterklaas-mod/releases/latest) |
|---|---|---|
| 26.3 | 66.1.8 of nieuwer voor 26.3 | `sinterklaas-forge-<versie>+mc26.3.jar` |
| 1.20.1 | 47.4.10 of nieuwer voor 1.20.1 | `sinterklaas-forge-<versie>+mc1.20.1.jar` |

De mod heeft verder niets nodig (geen andere mods of bibliotheken).

### Met CurseForge

1. Maak een profiel (of gebruik een bestaand profiel) met de goede **spelversie** en als modloader **Forge**: `26.3` met `forge-66.1.8`, of `1.20.1` met `forge-47.4.10`.
2. Klik bij het profiel op de drie puntjes en kies *Open Folder* (*Map openen*).
3. Zet de jar die bij je versie hoort in de map `mods`.
4. Start het profiel met *Play*.

Heb je al een wereld in dat profiel (bijvoorbeeld met een stoomboot)? Die blijft gewoon werken: de mod voegt alleen dingen toe.

### Zonder CurseForge

1. Installeer Forge voor jouw Minecraft-versie met de installer van https://files.minecraftforge.net/
2. Zet de jar in je map `mods`:
   - Windows: `%appdata%\.minecraft\mods`
   - macOS: `~/Library/Application Support/minecraft/mods`
   - Linux: `~/.minecraft/mods`
3. Start de Minecraft Launcher, kies het profiel **forge** en klik op *Spelen*.

Samen spelen op een server? Dan moet de mod op de server én bij elke speler staan.

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
| Wat er in een pakje zit en hoe vaak | `tools/maak_assets.py` (functie `loot`) |
| Wat een Piet laat vallen, en de kans op een pakje in kisten | ook `tools/maak_assets.py` (functie `loot`) |
| De plaatjes van Sint, Pieten, pakje, schoen en bord | map `textures/` (`sint.png` is een gewone speler-skin van 64x64) |
| Van welke dag tot welke dag pakjes open mogen | `core/src/main/java/nl/sinterklaas/core/PakjesRegels.java` |
| Wanneer het seizoen begint en hoe snel het drukker wordt | `core/src/main/java/nl/sinterklaas/core/SeizoenRegels.java` |
| Namen van voorwerpen | `forge-26.3/src/main/resources/assets/sinterklaas/lang/nl_nl.json` (geldt voor beide versies) |

De loot-tables, recepten, modellen en item-plaatjes worden gemaakt door `tools/maak_assets.py`, voor beide Minecraft-versies tegelijk (die schrijven loot-tables en recepten elk anders op). Pas je iets aan, doe dat dan in dat script en draai het opnieuw; anders overschrijft het script je wijziging.

De zinnen in `teksten.json` zijn nu nog voorlopig (ze beginnen met bijvoorbeeld `[koek 1]`). Vervang ze door je eigen tekst; je mag zoveel zinnen toevoegen als je wilt.

### Bouwen

De twee versies worden apart gebouwd, omdat ze elk andere gereedschappen nodig hebben. Gradle wordt vanzelf gedownload.

| | Minecraft 26.3 | Minecraft 1.20.1 |
|---|---|---|
| Map | `forge-26.3/` | `forge-1.20.1/` |
| Nodig | Java 25 (JDK) | Java 17 (JDK) |
| Bouwen | `./gradlew build` | `cd forge-1.20.1 && ./gradlew build` |
| De jar staat in | `forge-26.3/build/libs/` | `forge-1.20.1/build/libs/` |
| Spelen met de mod | `./gradlew :forge-26.3:runClient` | `cd forge-1.20.1 && ./gradlew runClient` |
| Speltests op een echte server | `./gradlew :forge-26.3:runGameTestServer` | `cd forge-1.20.1 && ./gradlew runGameTestServer` |

Verder:

| Opdracht | Wat doet het |
|---|---|
| `./gradlew :core:test` | Alleen de tests van de spelregels (4 december, 5 december, tijdzone, schoen, seizoen) |
| `python3 tools/maak_assets.py` | Maakt modellen, plaatjes, loot-tables, recepten en de testbestanden opnieuw |

De eerste keer bouwen duurt een paar minuten: Forge downloadt en bewerkt dan Minecraft zelf.

### Hoe zit het in elkaar

- `core/` — de spelregels, zonder Minecraft-code: datumregel, tijdzone, seizoen, schoen, praatpauzes, Piet-varianten, teksten. Los te testen; beide versies gebruiken precies dezelfde regels.
- `forge-26.3/` — de mod voor Minecraft 26.3: blokken, items, entiteiten, tekenen, commando. Hier staan ook de modellen, blockstates, plaatjes en vertalingen die beide versies delen.
- `forge-1.20.1/` — dezelfde mod voor Minecraft 1.20.1. De Minecraft-code is daar anders geschreven (Minecraft is tussen die versies sterk veranderd); recepten en loot-tables hebben er hun eigen schrijfwijze.
- `textures/` — de bronplaatjes (64x64).
- `tools/` — hulpscript voor afgeleide assets en data.

Iets nieuws in het spel bouw je dus in **beide** mappen; een nieuwe spelregel hoeft maar één keer, in `core/`.

Versies: Minecraft 26.3 met Forge 66.1.8 (ForgeGradle 7, Gradle 9.8.2, Java 25) en Minecraft 1.20.1 met Forge 47.4.10 (ForgeGradle 6, Gradle 8.8, Java 17).

## Licentie en meedoen

De mod valt onder de MIT-licentie (zie `LICENSE`), en dat geldt ook voor de plaatjes. Iedereen mag hem gebruiken, kopiëren en aanpassen.

- **Naam vermelden is verplicht:** laat de copyrightregel en de licentietekst staan, en verwijs het liefst naar https://github.com/CoenBresser/minecraft-sinterklaas-mod.
- **Bijdragen is een verzoek:** heb je iets verbeterd, stuur het dan graag terug. Dat kan alleen met een pull request; hoe dat werkt staat in `CONTRIBUTING.md`.
