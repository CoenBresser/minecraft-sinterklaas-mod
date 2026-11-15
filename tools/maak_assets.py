#!/usr/bin/env python3
"""Maakt alle afgeleide assets en data uit ./textures.
Draai vanuit de projectmap:  python3 tools/maak_assets.py

- blokmodellen (pakje, schoen, bord) opgebouwd uit dozen met vanilla 'box-UV'
- spawn-ei-plaatjes waarvan de kleuren uit het kostuum komen
- eenvoudige (voorlopige) item-plaatjes; vervang ze gerust door eigen pixelart
- blockstates, item-definities, loot-tables en recepten
- dezelfde recepten, loot-tables en item-modellen in het formaat van Minecraft 1.20.1 (Forge)
"""
import json, os
from collections import Counter
from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TEX = os.path.join(ROOT, "textures")
RES = os.path.join(ROOT, "fabric/src/main/resources")
ASSETS = os.path.join(RES, "assets/sinterklaas")
DATA = os.path.join(RES, "data/sinterklaas")
# De Forge-versie (Minecraft 1.20.1) deelt modellen, blockstates, plaatjes en vertalingen met de
# Fabric-versie (forge-1.20.1/build.gradle kopieert ze). Alleen wat in 1.20.1 een ander formaat
# heeft, komt hier te staan.
FORGE_RES = os.path.join(ROOT, "forge-1.20.1/src/main/resources")
VARIANTEN = ["roetveeg", "hoofd", "pakjes", "wegwijs", "koek", "malle"]

def schrijf(pad, inhoud):
    os.makedirs(os.path.dirname(pad), exist_ok=True)
    with open(pad, "w") as f:
        json.dump(inhoud, f, indent=1, ensure_ascii=False)
        f.write("\n")

# ---------------------------------------------------------------- modellen

def doos(u, v, w, h, d, van, hergebruik=(), tex="#t", draai=None):
    """Een doos met vanilla box-UV. van=(x,y,z) in blokpixels; (u,v) in textuurpixels (64x64)."""
    x, y, z = van
    def uv(x1, y1, x2, y2):
        return [x1 / 4, y1 / 4, x2 / 4, y2 / 4]
    f = {
        "up":    uv(u + d,         v,     u + d + w,         v + d),
        "down":  uv(u + d + w,     v,     u + d + 2 * w,     v + d),
        "east":  uv(u,             v + d, u + d,             v + d + h),
        "north": uv(u + d,         v + d, u + d + w,         v + d + h),
        "west":  uv(u + d + w,     v + d, u + 2 * d + w,     v + d + h),
        "south": uv(u + 2 * d + w, v + d, u + 2 * d + 2 * w, v + d + h),
    }
    for doel, bron in hergebruik:   # vlakken die niet getekend zijn lenen een ander vlak
        f[doel] = f[bron]
    el = {"from": [x, y, z], "to": [x + w, y + h, z + d],
          "faces": {k: {"uv": val, "texture": tex} for k, val in f.items()}}
    if draai:
        el["rotation"] = draai
    return el

STRIK = (("south", "north"), ("down", "up"), ("west", "east"))

def pakje_modellen():
    tex = {"t": "sinterklaas:block/pakje", "particle": "sinterklaas:block/pakje"}
    dicht = [
        doos(0, 0, 12, 9, 12, (2, 0, 2)),              # doos
        doos(0, 21, 14, 3, 14, (1, 9, 1)),             # deksel
        doos(0, 38, 6, 2, 3, (2, 12, 6.5), STRIK),     # strik links
        doos(0, 38, 6, 2, 3, (8, 12, 6.5), STRIK),     # strik rechts
        doos(16, 38, 4, 3, 2, (6, 12, 7)),             # knoop
    ]
    schrijf(f"{ASSETS}/models/block/pakje.json",
            {"parent": "minecraft:block/block", "textures": tex, "elements": dicht})
    # Open: deksel met strik klapt naar achteren open (scharnier aan de achterkant).
    kiep = {"angle": 45, "axis": "x", "origin": [8, 9, 15]}
    open_ = [
        doos(0, 0, 12, 9, 12, (2, 0, 2)),
        doos(0, 21, 14, 3, 14, (1, 9, 1), draai=kiep),
        doos(0, 38, 6, 2, 3, (2, 12, 6.5), STRIK, draai=kiep),
        doos(0, 38, 6, 2, 3, (8, 12, 6.5), STRIK, draai=kiep),
        doos(16, 38, 4, 3, 2, (6, 12, 7), draai=kiep),
    ]
    schrijf(f"{ASSETS}/models/block/pakje_open.json",
            {"parent": "minecraft:block/block", "textures": tex, "elements": open_})
    for naam in ("pakje", "schoenpakje"):
        schrijf(f"{ASSETS}/blockstates/{naam}.json", {"variants": {
            "open=false": {"model": "sinterklaas:block/pakje"},
            "open=true": {"model": "sinterklaas:block/pakje_open"}}})
        schrijf(f"{ASSETS}/items/{naam}.json",
                {"model": {"type": "minecraft:model", "model": "sinterklaas:block/pakje"}})

def schoen_modellen():
    tex = {"t": "sinterklaas:block/schoen", "p": "sinterklaas:block/pakje", "particle": "sinterklaas:block/schoen"}
    schoen = [
        doos(0, 0, 7, 1, 13, (4.5, 0, 1.5)),     # zool
        doos(0, 14, 6, 3, 11, (5, 1, 2.5)),      # neus met gesp
        doos(0, 28, 6, 5, 5, (5, 4, 8.5)),       # schacht
        doos(24, 28, 7, 2, 6, (4.5, 9, 8)),      # randje bovenaan
    ]
    weergave = {"gui": {"rotation": [30, 225, 0], "translation": [0, 2, 0], "scale": [0.9, 0.9, 0.9]}}
    def blokje(van, tot, textuur, uv=(0, 0, 4, 4)):
        return {"from": van, "to": tot, "faces": {k: {"uv": list(uv), "texture": textuur}
                for k in ("up", "down", "north", "south", "east", "west")}}
    # Pakje: een klein pakje steekt uit de schoen (zijkant van de doos met lint als plaatje).
    zij = [12 / 4, 12 / 4, 24 / 4, 21 / 4]
    boven = [12 / 4, 0, 24 / 4, 12 / 4]
    mini = {"from": [5.5, 10, 8.5], "to": [10.5, 14.5, 13.5],
            "faces": {k: {"uv": (boven if k in ("up", "down") else zij), "texture": "#p"}
                      for k in ("up", "down", "north", "south", "east", "west")}}
    # Wortel: oranje stukje met groen loof steekt uit de schoen.
    wortel = [blokje([7, 9.5, 10], [9, 13, 12], "#o"), blokje([6.5, 13, 9.5], [9.5, 15, 12.5], "#g")]
    # Briefje: een wit velletje steekt schuin uit de schoen.
    briefje = [dict(blokje([6, 9.5, 10.75], [10, 15, 11.25], "#w"),
                    rotation={"angle": 22.5, "axis": "z", "origin": [8, 10, 11]})]
    extra = {"leeg": [], "pakje": [mini], "wortel": wortel, "briefje": briefje}
    tex.update({"o": "minecraft:block/orange_concrete", "g": "minecraft:block/moss_block", "w": "minecraft:block/white_concrete"})
    for inhoud, elementen in extra.items():
        naam = "schoen" if inhoud == "leeg" else f"schoen_{inhoud}"
        schrijf(f"{ASSETS}/models/block/{naam}.json",
                {"parent": "minecraft:block/block", "textures": tex, "elements": schoen + elementen, "display": weergave})
    oud = f"{ASSETS}/models/block/schoen_gevuld.json"
    if os.path.exists(oud):
        os.remove(oud)
    varianten = {}
    for richting, y in (("north", 0), ("east", 90), ("south", 180), ("west", 270)):
        for inhoud in extra:
            m = {"model": "sinterklaas:block/schoen" + ("" if inhoud == "leeg" else f"_{inhoud}")}
            if y:
                m["y"] = y
            varianten[f"facing={richting},inhoud={inhoud}"] = m
    schrijf(f"{ASSETS}/blockstates/schoen.json", {"variants": varianten})
    schrijf(f"{ASSETS}/items/schoen.json", {"model": {"type": "minecraft:model", "model": "sinterklaas:block/schoen"}})

def bord_modellen():
    tex = {"t": "sinterklaas:block/pepernoot", "particle": "sinterklaas:block/pepernoot"}
    bord = doos(0, 18, 14, 1, 14, (1, 0, 1))
    noten = [
        doos(0, 0, 6, 2, 4, (3, 1, 3)),
        doos(0, 8, 4, 2, 6, (10, 1, 4)),
        doos(0, 8, 4, 2, 6, (4, 1, 8.5)),
        doos(0, 0, 6, 2, 4, (8, 1, 10.5)),
    ]
    varianten = {}
    for n in range(5):
        schrijf(f"{ASSETS}/models/block/bord_{n}.json",
                {"parent": "minecraft:block/block", "textures": tex, "elements": [bord] + noten[:n]})
        varianten[f"pepernoten={n}"] = {"model": f"sinterklaas:block/bord_{n}"}
    schrijf(f"{ASSETS}/blockstates/bord.json", {"variants": varianten})
    schrijf(f"{ASSETS}/items/bord.json", {"model": {"type": "minecraft:model", "model": "sinterklaas:block/bord_0"}})

def mijter_model():
    """Mijter met baard als 3D-model. Hij wordt op een hoofd getekend (Sint en spelers).

    Maten: op een hoofd is 1 model-eenheid 0,625 pixel van het figuur, dus een hoofd van
    8 pixels is 12,8 eenheden breed. In dit model zit de bovenkant van het hoofd op y=10 en
    de kin op y=-2,8; het gezicht is de noordkant (z=1,6). De 'head'-verschuiving hieronder
    zet het model zo op het hoofd.
    """
    rood = [0, 0, 4, 4]
    wit = [0, 14, 16, 16]
    def blok(van, tot, voor):
        f = {k: {"uv": rood, "texture": "#t"} for k in ("up", "down", "east", "west")}
        f["north"] = {"uv": voor, "texture": "#t"}
        f["south"] = {"uv": voor, "texture": "#t"}
        return {"from": van, "to": tot, "faces": f}
    def baard(van, tot):
        return {"from": van, "to": tot,
                "faces": {k: {"uv": wit, "texture": "#t"} for k in ("up", "down", "north", "south", "east", "west")}}
    elementen = [
        # de mijter: breed van voren, spits naar boven
        blok([1.2, 8.5, 1.2], [14.8, 12, 14.8], [0, 11, 16, 14]),   # rand met gouden band, om het hoofd
        blok([1.6, 12, 2.4], [14.4, 19, 13.6], [0, 5, 16, 11]),     # breed middenstuk met kruis
        blok([3, 19, 3.2], [13, 24, 12.8], [1, 3, 15, 5]),
        blok([4.5, 24, 4], [11.5, 27.5, 12], [3, 2, 13, 3]),
        blok([6, 27.5, 5], [10, 30, 11], [5, 1, 11, 2]),
        blok([7.25, 30, 6], [8.75, 31.5, 10], [7, 0, 9, 1]),        # punt
        # de baard: bakkebaarden langs de wangen, een brede baard over de kin en een punt op de borst
        baard([0.8, -3.3, 0.6], [1.9, 4.5, 6]),
        baard([14.1, -3.3, 0.6], [15.2, 4.5, 6]),
        baard([0.8, -3.6, 0.3], [15.2, 0.8, 1.7]),
        baard([2.5, -7, 0.3], [13.5, -3.6, 4.4]),
        baard([4, -9.5, 0.5], [12, -7, 4.6]),
        baard([6, -11.5, 0.8], [10, -9.5, 4.6]),
    ]
    schrijf(f"{ASSETS}/models/item/mijter.json", {
        "parent": "minecraft:block/block",
        "textures": {"t": "sinterklaas:item/mijter", "particle": "sinterklaas:item/mijter"},
        "elements": elementen,
        "display": {
            "head": {"rotation": [0, 0, 0], "translation": [0, 4.4, 0], "scale": [1, 1, 1]},
            "gui": {"rotation": [15, 200, 0], "translation": [0, -1, 0], "scale": [0.36, 0.36, 0.36]},
            "ground": {"translation": [0, 2, 0], "scale": [0.3, 0.3, 0.3]},
            "fixed": {"translation": [0, -1, 0], "scale": [0.36, 0.36, 0.36]},
            "thirdperson_righthand": {"rotation": [75, 45, 0], "translation": [0, 1, 0], "scale": [0.2, 0.2, 0.2]},
            "firstperson_righthand": {"rotation": [0, 200, 0], "translation": [0, 0, 0], "scale": [0.22, 0.22, 0.22]},
        }})
    schrijf(f"{ASSETS}/items/mijter.json", {"model": {"type": "minecraft:model", "model": "sinterklaas:item/mijter"}})

def staf_model():
    """De staf van Sint: een lange gouden staf met een krul bovenaan, als 3D-model."""
    goud, donker = [0, 0, 4, 16], [4, 0, 8, 16]
    def blok(van, tot, uv=goud):
        return {"from": van, "to": tot, "faces": {k: {"uv": uv, "texture": "#t"}
                for k in ("up", "down", "north", "south", "east", "west")}}
    elementen = [
        blok([7.25, -16, 7.25], [8.75, 20, 8.75]),        # de lange stok
        blok([6.75, -16, 6.75], [9.25, -14.5, 9.25], donker),  # dopje onderaan
        blok([6.5, 19, 6.5], [9.5, 21, 9.5], donker),     # knop onder de krul
        blok([7.25, 21, 7.25], [8.75, 29, 8.75]),         # omhoog
        blok([7.25, 29, 2.5], [8.75, 30.5, 8.75]),        # bovenlangs naar voren
        blok([7.25, 23.5, 2.5], [8.75, 29, 4]),           # omlaag
        blok([7.25, 23.5, 4], [8.75, 25, 6.5]),           # terug naar binnen
        blok([7.25, 25, 5], [8.75, 27, 6.5]),             # het puntje van de krul
    ]
    hand = {"rotation": [90, 0, 0], "translation": [0, 1, 4], "scale": [0.8, 0.8, 0.8]}
    schrijf(f"{ASSETS}/models/item/staf.json", {
        "parent": "minecraft:block/block",
        "textures": {"t": "sinterklaas:item/staf", "particle": "sinterklaas:item/staf"},
        "elements": elementen,
        "display": {
            "thirdperson_righthand": hand, "thirdperson_lefthand": hand,
            "firstperson_righthand": {"rotation": [0, 90, 0], "translation": [2, 2, 0], "scale": [0.6, 0.6, 0.6]},
            "firstperson_lefthand": {"rotation": [0, 90, 0], "translation": [2, 2, 0], "scale": [0.6, 0.6, 0.6]},
            "gui": {"rotation": [0, 90, 0], "translation": [0, -2.2, 0], "scale": [0.33, 0.33, 0.33]},
            "ground": {"rotation": [90, 0, 0], "translation": [0, 0, 0], "scale": [0.4, 0.4, 0.4]},
            "fixed": {"rotation": [0, 90, 0], "translation": [0, -2.2, 0], "scale": [0.33, 0.33, 0.33]},
        }})
    schrijf(f"{ASSETS}/items/staf.json", {"model": {"type": "minecraft:model", "model": "sinterklaas:item/staf"}})

# ---------------------------------------------------------------- plaatjes

def teken(naam, rijen, kleuren):
    im = Image.new("RGBA", (16, 16))
    for y, rij in enumerate(rijen):
        for x, c in enumerate(rij):
            im.putpixel((x, y), kleuren.get(c, (0, 0, 0, 0)))
    pad = f"{ASSETS}/textures/item/{naam}.png"
    os.makedirs(os.path.dirname(pad), exist_ok=True)
    im.save(pad)

def plat_item(naam):
    schrijf(f"{ASSETS}/items/{naam}.json", {"model": {"type": "minecraft:model", "model": f"sinterklaas:item/{naam}"}})
    schrijf(f"{ASSETS}/models/item/{naam}.json",
            {"parent": "minecraft:item/generated", "textures": {"layer0": f"sinterklaas:item/{naam}"}})

def item_plaatjes():
    bruin = {"d": (92, 52, 20, 255), "b": (140, 84, 36, 255), "l": (184, 124, 60, 255)}
    teken("pepernoot", [
        "................", "................", "................", "................",
        ".....dddddd.....", "....dblllbbd....", "...dbllbbbbbd...", "...dblbbbbbbd...",
        "...dbbbbbbdbd...", "...dbbbbbdbbd...", "...dbbdbbbbbd...", "....dbbbbbbd....",
        ".....dddddd.....", "................", "................", "................"], bruin)
    plat_item("pepernoot")
    choco = {"d": (58, 30, 14, 255), "b": (104, 58, 28, 255), "l": (150, 92, 50, 255)}
    teken("chocoladeletter", [
        "................", "..ddddddddddd...", ".dllllllllllbd..", ".dlbbbbbbbbbbd..",
        ".dlbbdddddddd...", ".dlbbd..........", ".dlbbddddddd....", ".dlbbbbbbbbbd...",
        ".dlbbbbbbbbbd...", ".dddddddddbbd...", "..........dbd...", ".ddddddddddbd...",
        ".dllllllllbbd...", ".dbbbbbbbbbbd...", "..ddddddddddd...", "................"], choco)
    plat_item("chocoladeletter")
    zak = {"j": (150, 110, 60, 255), "J": (190, 150, 90, 255), "r": (200, 30, 40, 255),
           "d": (100, 70, 35, 255), "p": (140, 84, 36, 255)}
    teken("zakje_pepernoten", [
        "................", ".....p.pp.......", "....dpppppd.....", "....dJJJJJd.....",
        ".....rrrrr......", "....drrrrrd.....", "...dJJJJJJJd....", "..dJJjjjjjJJd...",
        "..dJjjjjjjjJd...", "..dJjjpjjjjJd...", "..dJjjjjjpjJd...", "..dJjjjjjjjJd...",
        "..dJJjjjjjJJd...", "...dJJJJJJJd....", "....ddddddd.....", "................"], zak)
    plat_item("zakje_pepernoten")
    lint = {"r": (200, 30, 40, 255), "R": (240, 80, 80, 255), "d": (130, 15, 25, 255)}
    teken("lint", [
        "................", "................", "...ddd....ddd...", "..dRRRd..dRRRd..",
        "..dRrrRddRrrRd..", "..dRrrrRRrrrRd..", "...dRrrrrrrRd...", "....ddrrrrdd....",
        "....drrddrrd....", "...drrd..drrd...", "..drrd....drrd..", "..drd......drd..",
        "..dd........dd..", "................", "................", "................"], lint)
    plat_item("lint")
    papier = {"w": (245, 240, 225, 255), "g": (190, 180, 160, 255), "i": (70, 60, 110, 255), "r": (200, 30, 40, 255)}
    teken("gedichtje", [
        "................", "...ggggggggg....", "..gwwwwwwwwwg...", "..gwiiiiiiiwg...",
        "..gwwwwwwwwwg...", "..gwiiiiiwwwg...", "..gwwwwwwwwwg...", "..gwiiiiiiiwg...",
        "..gwwwwwwwwwg...", "..gwiiiiwwwwg...", "..gwwwwwwwwwg...", "..gwwwwwrrwwg...",
        "..gwwwwwrrwwg...", "...ggggggggg....", "................", "................"], papier)
    plat_item("gedichtje")
    # De onderste twee rijen zijn de baard (wit met een paar lichtgrijze plukjes).
    # De staf: links goud met een lichte streep, rechts donkerder goud voor de knop en het dopje.
    staf = {"g": (236, 190, 60, 255), "l": (252, 226, 120, 255), "d": (186, 138, 34, 255), "D": (150, 108, 24, 255)}
    teken("staf", ["glggdDdd" + "gggggggg"] * 16, staf)
    mijter = {"r": (190, 20, 30, 255), "g": (236, 190, 60, 255), "d": (150, 12, 22, 255),
              "w": (248, 248, 244, 255), "v": (222, 222, 216, 255)}
    teken("mijter", [
        "rrrrrrrggrrrrrrr", "rrrrrrrggrrrrrrr", "rrrrrrrggrrrrrrr", "rrrrrrrggrrrrrrr",
        "rrrrrrrggrrrrrrr", "rrrrrrrggrrrrrrr", "rrrrrrrggrrrrrrr", "rrrrggggggggrrrr",
        "rrrrggggggggrrrr", "rrrrrrrggrrrrrrr", "rrrrrrrggrrrrrrr", "dddddddggddddddd",
        "gggggggggggggggg", "gggggggggggggggg", "wwwvwwwwwvwwwwvw", "wvwwwwvwwwwwvwww"], mijter)

def sint_skin():
    """Tekent textures/sint.png: Sint als gewoon mensfiguur, in het formaat van een speler-skin (64x64).

    Dit plaatje kun je in elke skin-editor voor Minecraft openen en bijwerken. De mijter en de
    lange baard staan er niet op: die zijn 3D en horen bij het voorwerp 'mijter' dat Sint draagt.
    Bestaat het plaatje al, dan blijft het staan (zodat eigen tekenwerk niet wordt overschreven).
    """
    pad = os.path.join(TEX, "sint.png")
    if os.path.exists(pad):
        return
    ROOD, DONKER, GOUD = (176, 22, 36), (150, 14, 28), (236, 190, 60)
    WIT, GRIJS, HUID, HUID2 = (246, 246, 242), (224, 224, 218), (226, 176, 144), (208, 156, 126)
    OOG, MOND, SCHOEN = (40, 30, 30), (150, 60, 60), (52, 32, 22)
    im = Image.new("RGBA", (64, 64))
    def vul(x0, y0, w, h, kleur, kleur2=None):
        for y in range(y0, y0 + h):
            for x in range(x0, x0 + w):
                k = kleur2 if kleur2 and (x * 7 + y * 13) % 5 == 0 else kleur   # een paar donkerdere pixels voor textuur
                im.putpixel((x, y), k + (255,))
    def punt(x, y, kleur):
        im.putpixel((x, y), kleur + (255,))

    # hoofd: bovenkant, onderkant, rechts, voor, links, achter
    vul(8, 0, 8, 8, WIT, GRIJS); vul(16, 0, 8, 8, HUID)
    vul(0, 8, 8, 8, WIT, GRIJS); vul(16, 8, 8, 8, WIT, GRIJS); vul(24, 8, 8, 8, WIT, GRIJS)
    vul(8, 8, 8, 8, HUID, HUID2)
    vul(8, 8, 8, 1, WIT, GRIJS)                       # haar op het voorhoofd
    punt(8, 9, WIT); punt(15, 9, WIT)
    for x in (9, 10, 13, 14): punt(x, 10, WIT)        # wenkbrauwen
    punt(10, 11, OOG); punt(13, 11, OOG); punt(9, 11, WIT); punt(14, 11, WIT)
    punt(11, 12, HUID2); punt(12, 12, HUID2)          # neus
    vul(9, 13, 6, 1, WIT, GRIJS)                      # snor
    vul(8, 14, 8, 2, WIT, GRIJS)                      # begin van de baard
    punt(11, 14, MOND); punt(12, 14, MOND)
    vul(6, 10, 2, 3, HUID); vul(16, 10, 2, 3, HUID)   # stukje wang aan de zijkanten

    # romp: boven, onder, rechts, voor, links, achter
    vul(20, 16, 8, 4, ROOD, DONKER); vul(28, 16, 8, 4, DONKER)
    vul(16, 20, 24, 12, ROOD, DONKER)
    vul(23, 20, 2, 12, GOUD); vul(22, 23, 4, 1, GOUD)     # gouden baan met kruis op de voorkant
    vul(35, 20, 2, 12, GOUD)                              # gouden baan op de rug

    def arm(x0, y0):
        vul(x0 + 4, y0, 4, 4, ROOD, DONKER); vul(x0 + 8, y0, 4, 4, HUID)
        vul(x0, y0 + 4, 16, 12, ROOD, DONKER)
        vul(x0, y0 + 12, 16, 1, GOUD); vul(x0, y0 + 13, 16, 1, WIT)   # gouden bies en witte manchet
        vul(x0, y0 + 14, 16, 2, HUID, HUID2)                           # handen
    arm(40, 16); arm(32, 48)

    def been(x0, y0):
        vul(x0 + 4, y0, 4, 4, ROOD, DONKER); vul(x0 + 8, y0, 4, 4, SCHOEN)
        vul(x0, y0 + 4, 16, 12, ROOD, DONKER)
        vul(x0, y0 + 12, 16, 2, WIT, GRIJS)       # witte rand van het onderkleed
        vul(x0, y0 + 14, 16, 2, SCHOEN)
    been(0, 16); been(16, 48)
    im.save(pad)
    print("  nieuw: textures/sint.png (speler-skin)")

EI = [  # 16x16; . leeg, r rand, b basis, s stip, l lichtje
    "................", "......rrrr......", ".....rbblbr.....", "....rbblbbbr....",
    "....rbbbbsbr....", "...rbsbbbbbbr...", "...rbbbbbbsbr...", "...rbbbsbbbbr...",
    "..rbbbbbbbbbbr..", "..rbsbbbbsbbbr..", "..rbbbbbbbbbbr..", "..rbbbsbbbbsbr..",
    "...rbbbbbbbbr...", "...rrbbbsbbrr...", ".....rrrrrr.....", "................",
]

def kostuumkleuren(pad):
    """Twee meest voorkomende, duidelijk verschillende kleuren van het kostuum (zonder huid, wit en zwart)."""
    im = Image.open(pad).convert("RGBA")
    tel = Counter()
    for y in range(18, 64):
        for x in range(64):
            r, g, b, a = im.getpixel((x, y))
            if a < 200: continue
            if max(r, g, b) - min(r, g, b) < 40: continue
            if r > 200 and 150 < g < 215 and 120 < b < 190: continue
            tel[(r // 24 * 24 + 12, g // 24 * 24 + 12, b // 24 * 24 + 12)] += 1
    basis = tel.most_common(1)[0][0]
    stip = next((k for k, _ in tel.most_common() if sum(abs(p - q) for p, q in zip(k, basis)) > 150), (255, 255, 255))
    return basis, stip

def spawn_ei(textuur, naam):
    basis, stip = kostuumkleuren(os.path.join(TEX, textuur))
    kleuren = {"b": basis + (255,), "s": stip + (255,),
               "r": tuple(int(c * 0.55) for c in basis) + (255,),
               "l": tuple(min(255, int(c * 1.35) + 30) for c in basis) + (255,)}
    teken(naam + "_spawn_egg", EI, kleuren)
    plat_item(naam + "_spawn_egg")
    print(f"  spawn-ei {naam}: basis {basis}, stip {stip}")

def gewoon_piet_ei():
    """Het ei waar een willekeurige Piet uit komt: rood, vol stipjes in allerlei kleuren.

    De stipjes staan op willekeurige plekken, maar met een vaste beginwaarde, zodat het plaatje
    bij elke keer draaien hetzelfde blijft.
    """
    import random
    toeval = random.Random(512)
    STIPKLEUREN = ["1", "2", "3", "4", "5", "6"]
    rijen = [list(rij.replace("s", "b")) for rij in EI]
    vrij = [(x, y) for y, rij in enumerate(rijen) for x, c in enumerate(rij) if c == "b"]
    toeval.shuffle(vrij)
    gezet = []
    for x, y in vrij:
        if len(gezet) == 16: break
        if any(abs(x - a) <= 1 and abs(y - b) <= 1 for a, b in gezet): continue   # stipjes raken elkaar niet
        rijen[y][x] = toeval.choice(STIPKLEUREN)
        gezet.append((x, y))
    basis = (200, 24, 36)
    kleuren = {"b": basis + (255,), "r": (110, 10, 20, 255), "l": (240, 110, 110, 255),
               "1": (255, 255, 255, 255), "2": (250, 210, 60, 255), "3": (70, 130, 240, 255),
               "4": (80, 200, 90, 255), "5": (180, 110, 230, 255), "6": (255, 150, 40, 255)}
    teken("piet_spawn_egg", ["".join(r) for r in rijen], kleuren)
    plat_item("piet_spawn_egg")
    print(f"  spawn-ei piet (willekeurig): rood met {len(gezet)} stipjes")

def sint_ei():
    """Het ei van Sinterklaas heeft de vorm van zijn mijter: rood, met een geel kruis en een gele rand."""
    rijen = [
        "................", ".......rr.......", "......rlbr......", ".....rlbbbr.....",
        "....rlbggbbr....", "....rlbggbbr....", "...rlbbggbbbr...", "...rlggggggbr...",
        "...rlggggggbr...", "...rlbbggbbbr...", "...rlbbggbbbr...", "...rlbbggbbbr...",
        "...rggggggggr...", "...rggggggggr...", "...rrrrrrrrrr...", "................",
    ]
    kleuren = {"b": (190, 20, 30, 255), "l": (220, 50, 55, 255), "r": (110, 8, 18, 255), "g": (240, 196, 60, 255)}
    teken("sint_spawn_egg", rijen, kleuren)
    plat_item("sint_spawn_egg")
    print("  spawn-ei sint: mijter")

# ---------------------------------------------------------------- data

def item(naam, gewicht=1, min_=None, max_=None):
    e = {"type": "minecraft:item", "name": naam, "weight": gewicht}
    if min_ is not None:
        e["functions"] = [{"function": "minecraft:set_count",
                           "count": {"type": "minecraft:uniform", "min": min_, "max": max_}}]
    return e

def loot():
    zelf = lambda naam: {"type": "minecraft:block", "pools": [{
        "rolls": 1, "entries": [{"type": "minecraft:item", "name": f"sinterklaas:{naam}"}],
        "conditions": [{"condition": "minecraft:survives_explosion"}]}]}
    schrijf(f"{DATA}/loot_table/blocks/pakje.json", zelf("pakje"))
    schrijf(f"{DATA}/loot_table/blocks/schoenpakje.json", zelf("schoenpakje"))
    schoen = zelf("schoen")
    # Wie een schoen afbreekt, krijgt ook terug wat erin zat (het briefje gaat verloren).
    for inhoud, wat in (("pakje", "sinterklaas:schoenpakje"), ("wortel", "minecraft:carrot")):
        schoen["pools"].append({"rolls": 1, "entries": [{"type": "minecraft:item", "name": wat}],
            "conditions": [{"condition": "minecraft:block_state_property", "block": "sinterklaas:schoen",
                            "properties": {"inhoud": inhoud}}]})
    schrijf(f"{DATA}/loot_table/blocks/schoen.json", schoen)
    bord = zelf("bord")
    bord["pools"].append({"rolls": 1, "entries": [{"type": "minecraft:item", "name": "sinterklaas:pepernoot",
        "functions": [{"function": "minecraft:set_count", "count": n,
                       "conditions": [{"condition": "minecraft:block_state_property", "block": "sinterklaas:bord",
                                       "properties": {"pepernoten": str(n)}}]} for n in range(2, 5)]}],
        "conditions": [{"condition": "minecraft:inverted", "term": {
            "condition": "minecraft:block_state_property", "block": "sinterklaas:bord",
            "properties": {"pepernoten": "0"}}}]})
    schrijf(f"{DATA}/loot_table/blocks/bord.json", bord)

    # Wat zit er in een pakje? Hoe hoger 'weight', hoe vaker.
    schrijf(f"{DATA}/loot_table/gameplay/pakje_inhoud.json", {
        "type": "minecraft:chest",
        "pools": [
            {"rolls": 1, "entries": [
                # meestal: pepernoten en snoep
                item("sinterklaas:pepernoot", 60, 4, 8),
                item("minecraft:cookie", 25, 2, 4),
                # zeldzaam: eigen Sinterklaas-items
                item("sinterklaas:chocoladeletter", 8),
                item("sinterklaas:zakje_pepernoten", 6),
                item("sinterklaas:mijter", 3),
                item("sinterklaas:piet_spawn_egg", 1),
            ] + [item(f"sinterklaas:piet_{v}_spawn_egg", 1) for v in VARIANTEN]},
            # zeer zeldzaam: een gedichtje of grap (verschijnt in de chat)
            {"rolls": 1, "entries": [item("sinterklaas:gedichtje")],
             "conditions": [{"condition": "minecraft:random_chance", "chance": 0.03}]},
        ]})
    # Wat een Piet af en toe laat vallen.
    schrijf(f"{DATA}/loot_table/gameplay/piet_cadeau.json", {
        "type": "minecraft:gift",
        "pools": [{"rolls": 1, "entries": [item("sinterklaas:pakje", 1), item("sinterklaas:pepernoot", 2, 1, 3)]}]})
    # Extra kans op een pakje in kisten (dorpshuizen, kerkers, mijnschachten).
    schrijf(f"{DATA}/loot_table/chests/kist_extra.json", {
        "type": "minecraft:chest",
        "pools": [{"rolls": 1, "entries": [item("sinterklaas:pakje")],
                   "conditions": [{"condition": "minecraft:random_chance", "chance": 0.1}]}]})

def recepten():
    def vormloos(naam, ingredienten, uit, aantal=1):
        schrijf(f"{DATA}/recipe/{naam}.json", {"type": "minecraft:crafting_shapeless", "category": "misc",
                "ingredients": ingredienten, "result": {"id": uit, "count": aantal}})
    vormloos("pakje", ["minecraft:paper", "sinterklaas:pepernoot", "sinterklaas:lint"], "sinterklaas:pakje")
    vormloos("lint", ["minecraft:string", "minecraft:red_dye"], "sinterklaas:lint", 2)
    vormloos("pepernoot", ["minecraft:wheat", "minecraft:sugar", "minecraft:cocoa_beans"], "sinterklaas:pepernoot", 8)
    vormloos("zakje_pepernoten", ["sinterklaas:pepernoot"] * 8 + ["minecraft:paper"], "sinterklaas:zakje_pepernoten")
    vormloos("pepernoten_uit_zakje", ["sinterklaas:zakje_pepernoten"], "sinterklaas:pepernoot", 8)
    vormloos("chocoladeletter", ["minecraft:cocoa_beans", "minecraft:cocoa_beans", "minecraft:sugar", "minecraft:milk_bucket"],
             "sinterklaas:chocoladeletter", 2)
    schrijf(f"{DATA}/recipe/schoen.json", {"type": "minecraft:crafting_shaped", "category": "misc",
            "key": {"L": "minecraft:leather", "S": "minecraft:string"}, "pattern": ["S ", "LL"],
            "result": {"id": "sinterklaas:schoen", "count": 1}})
    schrijf(f"{DATA}/recipe/staf.json", {"type": "minecraft:crafting_shaped", "category": "misc",
            # Bedacht door Niek: drie goudstaven boven elkaar, en eentje links op de bovenste rij (de krul).
            "key": {"G": "minecraft:gold_ingot"}, "pattern": ["GG", " G", " G"],
            "result": {"id": "sinterklaas:staf", "count": 1}})
    schrijf(f"{DATA}/recipe/bord.json", {"type": "minecraft:crafting_shaped", "category": "misc",
            "key": {"K": "minecraft:clay_ball", "B": "minecraft:blue_dye"}, "pattern": ["KBK"],
            "result": {"id": "sinterklaas:bord", "count": 2}})

# ---------------------------------------------------------------- Forge (Minecraft 1.20.1)

def forge():
    """Zet de data van hierboven om naar het formaat van Minecraft 1.20.1.

    - recepten: map 'recipes', ingrediënten als {"item": ...}, resultaat met "item" in plaats van "id"
    - loot-tables: map 'loot_tables' (de inhoud is gelijk)
    - voorwerpen: 1.20.1 kent de map 'items' niet en zoekt voor elk voorwerp models/item/<naam>.json;
      voor de blokken (pakje, schoen, bord) maken we daar een verwijzing naar het blokmodel.
    """
    def lees(pad):
        with open(pad) as f:
            return json.load(f)
    def wat(naam):
        return {"item": naam}

    for bestand in sorted(os.listdir(f"{DATA}/recipe")):
        recept = lees(f"{DATA}/recipe/{bestand}")
        if "ingredients" in recept:
            recept["ingredients"] = [wat(i) for i in recept["ingredients"]]
        if "key" in recept:
            recept["key"] = {letter: wat(i) for letter, i in recept["key"].items()}
        uit = recept["result"]
        recept["result"] = {"item": uit["id"], "count": uit["count"]}
        schrijf(f"{FORGE_RES}/data/sinterklaas/recipes/{bestand}", recept)

    for map_, _, bestanden in os.walk(f"{DATA}/loot_table"):
        for bestand in sorted(bestanden):
            rel = os.path.relpath(os.path.join(map_, bestand), f"{DATA}/loot_table")
            schrijf(f"{FORGE_RES}/data/sinterklaas/loot_tables/{rel}", lees(os.path.join(map_, bestand)))

    for bestand in sorted(os.listdir(f"{ASSETS}/items")):
        naam = bestand[:-len(".json")]
        model = lees(f"{ASSETS}/items/{bestand}")["model"]["model"]
        if model != f"sinterklaas:item/{naam}":
            schrijf(f"{FORGE_RES}/assets/sinterklaas/models/item/{naam}.json", {"parent": model})

if __name__ == "__main__":
    sint_skin()
    pakje_modellen(); schoen_modellen(); bord_modellen(); mijter_model(); staf_model(); item_plaatjes()
    for v in VARIANTEN:
        spawn_ei(f"piet_{v}.png", f"piet_{v}")
    gewoon_piet_ei()
    sint_ei()
    loot(); recepten()
    forge()
    print("klaar")
