# Sinterklaas-mod

Een Minecraft Java-mod met Sint, Pieten, pakjes en pepernoten. Gemaakt door vader en zoon.

Voor Minecraft Java, in twee versies: **26.2 met Fabric** en **1.20.1 met Forge**. De nieuwste versie download je bij [Releases](https://github.com/CoenBresser/minecraft-sinterklaas-mod/releases/latest); wat er per versie is veranderd staat in `CHANGELOG.md`.

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
- **Pietenpak**: verkleed je als Piet! Voor elke Piet is er een pak in zijn eigen kleuren, met vier delen: **muts** (baret met veer), **jasje**, **broek** en **schoenen**. Het pak beschermt net zo goed als ijzer en repareer je ook met ijzer. Heb je **het hele pak** aan, dan ben je een echte Piet: je loopt sneller en springt hoger (handig op het dak). De delen mogen van verschillende Pieten zijn. Je eigen gezicht blijft gewoon zichtbaar.

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
| Pietenpak (muts, jasje, broek, schoenen) | het ijzeren deel (helm, borstplaat, beenbescherming, laarzen) + wol in de kleur van de Piet + lint |

Welke wol hoort bij welke Piet:

| Piet | Wol |
|---|---|
| Roetveegpiet | blauw |
| Hoofdpiet | zwart |
| Pakjespiet | groen |
| Wegwijspiet | cyaan |
| Koekpiet | bruin |
| Malle Pietje | roze |

## Installeren (voor spelers)

Er zijn twee versies van de mod. Bij de [nieuwste release](https://github.com/CoenBresser/minecraft-sinterklaas-mod/releases/latest) staan voor elke versie een **modpack** (om te importeren in de CurseForge-app) en de losse **jar** (voor een profiel dat je al hebt):

| Minecraft | Modloader | Modpack | Losse jar |
|---|---|---|---|
| 26.2 | Fabric Loader 0.19.5 of nieuwer | `sinterklaas-modpack-<versie>+fabric-mc26.2.zip` | `sinterklaas-fabric-<versie>+mc26.2.jar` |
| 1.20.1 | Forge 47.4.10 of nieuwer voor 1.20.1 | `sinterklaas-modpack-<versie>+forge-mc1.20.1.zip` | `sinterklaas-forge-<versie>+mc1.20.1.jar` |

### Nieuw profiel: het modpack importeren

1. Download het modpack (de zip) dat bij je versie hoort. Pak het **niet** uit.
2. Kies in de CurseForge-app bij Minecraft *Create Custom Profile* en dan *Import*, en wijs de zip aan.
3. Er komt een nieuw profiel bij met de goede Minecraft-versie en modloader, en de mod er al in. Start het met *Play*.

Het Fabric-modpack haalt bij het importeren ook Fabric API binnen, die de mod nodig heeft. Dat downloadt de app zelf van CurseForge, dus je hebt daarbij internet nodig. De Forge-versie heeft verder niets nodig.

> **De Sinterklaas-mod staat niet in de lijst *Mods* van het profiel. Dat is normaal.** De CurseForge-app toont in die lijst alleen mods die hij zelf van CurseForge heeft gedownload, zoals Fabric API. De Sinterklaas-mod staat niet op CurseForge, dus de app laat hem niet zien. Hij zit wel in het profiel en werkt gewoon. Controleren kan zo: klik bij het profiel op de drie puntjes, kies *Open Folder* (*Map openen*) en kijk in de map `mods`. Of start het spel: met Forge staat Sinterklaas in het titelscherm onder *Mods*.

### Bestaand profiel: de jar erbij zetten

Importeren maakt altijd een nieuw profiel. Via de app kun je de mod niet aan een bestaand profiel toevoegen, want hij staat niet op CurseForge. Wil je de mod in een profiel dat je al hebt (bijvoorbeeld bij een bestaande wereld), zet dan de losse jar met de hand in het profiel:

1. Controleer dat het profiel dezelfde Minecraft-versie en modloader heeft als in de tabel.
2. Klik bij het profiel op de drie puntjes en kies *Open Folder* (*Map openen*).
3. Zet de losse jar in de map `mods`. Voor Fabric moet **Fabric API** ook in het profiel staan (via *Add More Content* in de app).
4. Start het profiel met *Play*.

Ook hier staat de mod daarna niet in de lijst *Mods* van de app (zie hierboven), maar werkt hij wel.

Een bestaande wereld blijft gewoon werken: de mod voegt alleen dingen toe.

### Zonder CurseForge

Installeer Fabric (https://fabricmc.net/use/installer/, met Fabric API van https://modrinth.com/mod/fabric-api) of Forge (https://files.minecraftforge.net/) voor jouw Minecraft-versie en zet de jar in je map `mods`:

- Windows: `%appdata%\.minecraft\mods`
- macOS: `~/Library/Application Support/minecraft/mods`
- Linux: `~/.minecraft/mods`

### Samen spelen op een server

De mod moet op de server én bij elke Java-speler staan, in dezelfde versie. Spelers op Bedrock (telefoon, console) die via Geyser meedoen, zien de nieuwe blokken, voorwerpen en figuren van de mod niet.

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
| Namen van voorwerpen | `fabric/src/main/resources/assets/sinterklaas/lang/nl_nl.json` (geldt voor beide versies) |

De loot-tables, recepten, modellen en item-plaatjes worden gemaakt door `tools/maak_assets.py`, voor beide Minecraft-versies tegelijk (recepten schrijven die elk anders op). Pas je iets aan, doe dat dan in dat script en draai het opnieuw; anders overschrijft het script je wijziging.

De zinnen in `teksten.json` zijn nu nog voorlopig (ze beginnen met bijvoorbeeld `[koek 1]`). Vervang ze door je eigen tekst; je mag zoveel zinnen toevoegen als je wilt.

### Bouwen

De twee versies worden apart gebouwd, omdat ze elk andere gereedschappen nodig hebben. Gradle wordt vanzelf gedownload.

| | Minecraft 26.2 (Fabric) | Minecraft 1.20.1 (Forge) |
|---|---|---|
| Map | `fabric/` | `forge-1.20.1/` |
| Nodig | Java 25 (JDK) | Java 17 (JDK) |
| Bouwen | `./gradlew build` | `cd forge-1.20.1 && ./gradlew build` |
| De jar staat in | `fabric/build/libs/` | `forge-1.20.1/build/libs/` |
| Spelen met de mod | `./gradlew :fabric:runClient` | `cd forge-1.20.1 && ./gradlew runClient` |
| Speltests | `./gradlew :fabric:runClientGameTest` (echte client, met screenshots) | `cd forge-1.20.1 && ./gradlew runGameTestServer` (testserver) |

Verder:

| Opdracht | Wat doet het |
|---|---|
| `./gradlew :core:test` | Alleen de tests van de spelregels (4 december, 5 december, tijdzone, schoen, seizoen) |
| `python3 tools/maak_assets.py` | Maakt modellen, plaatjes, loot-tables en recepten opnieuw, voor beide versies |
| `python3 tools/maak_modpacks.py` | Maakt van de gebouwde jars de modpacks voor CurseForge, in `build/modpacks/` |

De eerste keer bouwen duurt een paar minuten: dan wordt Minecraft zelf gedownload en bewerkt.

### Hoe zit het in elkaar

- `core/` — de spelregels, zonder Minecraft-code: datumregel, tijdzone, seizoen, schoen, praatpauzes, Piet-varianten, teksten. Los te testen; beide versies gebruiken precies dezelfde regels.
- `fabric/` — de mod voor Minecraft 26.2: blokken, items, entiteiten, tekenen, commando. Hier staan ook de modellen, blockstates, plaatjes en vertalingen die beide versies delen.
- `forge-1.20.1/` — dezelfde mod voor Minecraft 1.20.1. De Minecraft-code is daar anders geschreven (Minecraft is tussen die versies sterk veranderd); recepten en loot-tables staan er in hun eigen mappen.
- `textures/` — de bronplaatjes (64x64).
- `tools/` — hulpscripts voor afgeleide assets en data, en voor de modpacks.

Iets nieuws in het spel bouw je dus in **beide** mappen; een nieuwe spelregel hoeft maar één keer, in `core/`.

Versies: Minecraft 26.2 met Fabric Loader 0.19.5 en Fabric API 0.161.0+26.2 (Loom 1.19.4, Gradle 9.8.2, Java 25), en Minecraft 1.20.1 met Forge 47.4.10 (ForgeGradle 6, Gradle 8.8, Java 17). Het zijn de versies van de server waarop de mod gespeeld wordt; de Fabric Loader is de nieuwste die de CurseForge-app aanbiedt.

## Licentie en meedoen

De mod valt onder de MIT-licentie (zie `LICENSE`), en dat geldt ook voor de plaatjes. Iedereen mag hem gebruiken, kopiëren en aanpassen.

- **Naam vermelden is verplicht:** laat de copyrightregel en de licentietekst staan, en verwijs het liefst naar https://github.com/CoenBresser/minecraft-sinterklaas-mod.
- **Bijdragen is een verzoek:** heb je iets verbeterd, stuur het dan graag terug. Dat kan alleen met een pull request; hoe dat werkt staat in `CONTRIBUTING.md`.
