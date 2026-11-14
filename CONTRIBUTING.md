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
- **Zelf iets gemaakt:** maak een fork, werk op een eigen branch en stuur een pull request.

### Voor je een pull request stuurt

```bash
./gradlew build
```

Dat bouwt de mod en draait de tests van de spelregels. Raak je iets in het spel zelf, draai dan ook:

```bash
./gradlew :fabric:runClientGameTest
```

Een paar afspraken:

- Spelregels (datums, seizoen, schoen, praten) horen in `core/`, zonder Minecraft-code, met een test erbij.
- Loot, recepten, modellen en item-plaatjes komen uit `tools/maak_assets.py`. Pas dat script aan, niet de gemaakte bestanden.
- Namen en commentaar in de code zijn in het Nederlands.
- Houd het vriendelijk en geschikt voor kinderen: dit is een mod van een vader en zijn zoon.

Door een bijdrage in te sturen ga je ermee akkoord dat die onder dezelfde MIT-licentie valt.
