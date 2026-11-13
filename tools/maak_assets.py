#!/usr/bin/env python3
"""Maakt alle afgeleide assets en data uit ./textures.
Draai vanuit de projectmap:  python3 tools/maak_assets.py

- blokmodellen (pakje, schoen, bord) opgebouwd uit dozen met vanilla 'box-UV'
- spawn-ei-plaatjes waarvan de kleuren uit het kostuum komen
- eenvoudige (voorlopige) item-plaatjes; vervang ze gerust door eigen pixelart
- blockstates, item-definities, loot-tables en recepten
"""
import json, os
from collections import Counter
from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TEX = os.path.join(ROOT, "textures")
RES = os.path.join(ROOT, "fabric/src/main/resources")
ASSETS = os.path.join(RES, "assets/sinterklaas")
DATA = os.path.join(RES, "data/sinterklaas")
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
    schrijf(f"{ASSETS}/models/block/schoen.json",
            {"parent": "minecraft:block/block", "textures": tex, "elements": schoen, "display": weergave})
    # Gevuld: een klein pakje steekt uit de schoen (zijkant van de doos met lint als plaatje).
    zij = [12 / 4, 12 / 4, 24 / 4, 21 / 4]
    boven = [12 / 4, 0, 24 / 4, 12 / 4]
    mini = {"from": [5.5, 10, 8.5], "to": [10.5, 14.5, 13.5],
            "faces": {k: {"uv": (boven if k in ("up", "down") else zij), "texture": "#p"}
                      for k in ("up", "down", "north", "south", "east", "west")}}
    schrijf(f"{ASSETS}/models/block/schoen_gevuld.json",
            {"parent": "minecraft:block/block", "textures": tex, "elements": schoen + [mini], "display": weergave})
    varianten = {}
    for richting, y in (("north", 0), ("east", 90), ("south", 180), ("west", 270)):
        for gevuld in ("false", "true"):
            m = {"model": "sinterklaas:block/schoen" + ("_gevuld" if gevuld == "true" else "")}
            if y:
                m["y"] = y
            varianten[f"facing={richting},gevuld={gevuld}"] = m
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
    """Mijter als klein 3D-model, zodat hij zichtbaar op het hoofd staat."""
    rood = [0, 0, 4, 4]
    def blok(van, tot, voor):
        f = {k: {"uv": rood, "texture": "#t"} for k in ("up", "down", "east", "west")}
        f["north"] = {"uv": voor, "texture": "#t"}
        f["south"] = {"uv": voor, "texture": "#t"}
        return {"from": van, "to": tot, "faces": f}
    # Een echte mijter is hoog en loopt spits toe: bijna twee keer zo hoog als het hoofd breed is.
    elementen = [
        blok([3.5, 0, 4], [12.5, 4, 12], [0, 11, 16, 16]),      # rand met gouden band
        blok([3.5, 4, 4.5], [12.5, 11, 11.5], [0, 5, 16, 11]),  # breed middenstuk met kruis
        blok([4.5, 11, 5], [11.5, 15, 11], [1, 3, 15, 5]),
        blok([5.5, 15, 5.5], [10.5, 18, 10.5], [3, 2, 13, 3]),
        blok([6.5, 18, 6], [9.5, 20.5, 10], [5, 1, 11, 2]),
        blok([7.25, 20.5, 6.5], [8.75, 22.5, 9.5], [7, 0, 9, 1]),  # punt
    ]
    schrijf(f"{ASSETS}/models/item/mijter.json", {
        "parent": "minecraft:block/block",
        "textures": {"t": "sinterklaas:item/mijter", "particle": "sinterklaas:item/mijter"},
        "elements": elementen,
        "display": {
            "head": {"rotation": [0, 0, 0], "translation": [0, 14.5, 0], "scale": [1.05, 1.05, 1.05]},
            "gui": {"rotation": [20, 200, 0], "translation": [0, -2, 0], "scale": [0.7, 0.7, 0.7]},
            "ground": {"translation": [0, 3, 0], "scale": [0.5, 0.5, 0.5]},
        }})
    schrijf(f"{ASSETS}/items/mijter.json", {"model": {"type": "minecraft:model", "model": "sinterklaas:item/mijter"}})

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
    mijter = {"r": (190, 20, 30, 255), "g": (236, 190, 60, 255), "d": (150, 12, 22, 255)}
    teken("mijter", [
        "rrrrrrrggrrrrrrr", "rrrrrrrggrrrrrrr", "rrrrrrrggrrrrrrr", "rrrrrrrggrrrrrrr",
        "rrrrrrrggrrrrrrr", "rrrrrrrggrrrrrrr", "rrrrrrrggrrrrrrr", "rrrrggggggggrrrr",
        "rrrrggggggggrrrr", "rrrrrrrggrrrrrrr", "rrrrrrrggrrrrrrr", "dddddddggddddddd",
        "gggggggggggggggg", "gggggggggggggggg", "rrrrrrrrrrrrrrrr", "dddddddddddddddd"], mijter)

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
    schoen["pools"].append({"rolls": 1, "entries": [{"type": "minecraft:item", "name": "sinterklaas:schoenpakje"}],
        "conditions": [{"condition": "minecraft:block_state_property", "block": "sinterklaas:schoen",
                        "properties": {"gevuld": "true"}}]})
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
    schrijf(f"{DATA}/recipe/bord.json", {"type": "minecraft:crafting_shaped", "category": "misc",
            "key": {"K": "minecraft:clay_ball", "B": "minecraft:blue_dye"}, "pattern": ["KBK"],
            "result": {"id": "sinterklaas:bord", "count": 2}})

if __name__ == "__main__":
    pakje_modellen(); schoen_modellen(); bord_modellen(); mijter_model(); item_plaatjes()
    for v in VARIANTEN:
        spawn_ei(f"piet_{v}.png", f"piet_{v}")
    spawn_ei("sint.png", "sint")
    loot(); recepten()
    print("klaar")
