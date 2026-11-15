# Meedoen

Leuk dat je iets met de Sinterklaas-mod wilt doen!

De mod valt onder de MIT-licentie (zie `LICENSE`). Je mag hem gebruiken, kopiëren en aanpassen.
Daarbij vragen we twee dingen.

## 1. Vermeld waar het vandaan komt (verplicht)

De MIT-licentie vraagt dat de copyrightregel en de licentietekst blijven staan in elke kopie of
aangepaste versie. Zet er daarnaast graag een verwijzing bij, bijvoorbeeld:

> Gebaseerd op de Sinterklaas-mod: https://github.com/CoenBresser/minecraft-sinterklaas-mod

Dat geldt voor de code én voor de plaatjes in `textures/`.

## 2. Stuur je verbeteringen terug (een verzoek, geen plicht)

Heb je iets verbeterd of toegevoegd? Deel het dan het liefst hier, zodat iedereen er iets aan heeft:

- **Idee of fout gevonden:** maak een issue aan op GitHub.
- **Zelf iets gemaakt:** dat gaat altijd met een pull request. Rechtstreeks naar `main` pushen kan niet.

### Zo stuur je een pull request

1. Maak een fork van de repository en daarin een eigen branch, bijvoorbeeld `feat/staf-voor-sint`.
2. Maak je wijziging en controleer hem:

```bash
./gradlew build                              # Minecraft 26.3 (Java 25)
cd forge-1.20.1 && ./gradlew build           # Minecraft 1.20.1 (Java 17)
```

Dat bouwt de mod en draait de tests van de spelregels. Raak je iets in het spel zelf, draai dan ook de speltests:

```bash
./gradlew :forge-26.3:runGameTestServer
cd forge-1.20.1 && ./gradlew runGameTestServer
```

3. Open een pull request naar `main`. GitHub draait dan dezelfde controles automatisch (`verify` en `speltest`).
4. Na een akkoord wordt je pull request gemerged.

### Commitberichten

We schrijven commitberichten in het Nederlands, in de vorm van [Conventional Commits](https://www.conventionalcommits.org/nl/). Daaruit worden het versienummer en `CHANGELOG.md` automatisch gemaakt.

| Begin | Waarvoor | Gevolg voor de versie |
|---|---|---|
| `feat:` | iets nieuws in de mod | volgende versie (0.5.0 → 0.6.0) |
| `fix:` | een fout opgelost | kleine versie (0.5.0 → 0.5.1) |
| `docs:`, `test:`, `ci:`, `chore:`, `refactor:`, `build:` | uitleg, tests, onderhoud | geen nieuwe versie nodig |

Je mag erbij zetten over welk onderdeel het gaat, bijvoorbeeld:

```
feat(schoen): leg een wortel in de schoen voor het paard
fix(pakje): laat het deksel niet door de strik steken
```

### Hoe een nieuwe versie uitkomt

Dat hoeft niemand met de hand te doen:

1. Na elke merge naar `main` houdt release-please een pull request bij met de naam "chore(main): release ...". Daarin staan het nieuwe versienummer en de bijgewerkte changelog.
2. Wordt die pull request gemerged, dan maakt GitHub de release aan, bouwt de jar en hangt die aan de release.

Het versienummer in `gradle.properties` wordt daarbij vanzelf bijgewerkt; pas het dus niet zelf aan.

Een paar afspraken:

- Spelregels (datums, seizoen, schoen, praten) horen in `core/`, zonder Minecraft-code, met een test erbij.
- De mod bestaat voor twee Minecraft-versies (`forge-26.3/` en `forge-1.20.1/`). Iets nieuws in het spel hoort in allebei, met een speltest in allebei.
- Loot, recepten, modellen en item-plaatjes komen uit `tools/maak_assets.py`. Pas dat script aan, niet de gemaakte bestanden.
- Namen en commentaar in de code zijn in het Nederlands.
- Houd het vriendelijk en geschikt voor kinderen: dit is een mod van een vader en zijn zoon.

Door een bijdrage in te sturen ga je ermee akkoord dat die onder dezelfde MIT-licentie valt.
