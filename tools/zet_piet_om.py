#!/usr/bin/env python3
"""Zet een Piet-plaatje in het formaat van de pillager om naar een gewone speler-skin (64x64).

Eenmalig gebruikt toen de Pieten van het pillager-model overgingen op het mensfiguur van Sint.
Draai vanuit de projectmap:  python3 tools/zet_piet_om.py textures/piet_*.png
Het plaatje wordt overschreven; de oude versie staat nog in de git-geschiedenis.

Wat er gebeurt, per onderdeel ('doos' in het model):
- hoofd 8x10x8 -> 8x8x8: de bovenste rij haar en de onderste rij kin vallen weg
- baret (hoedlaag) 8x12x8 -> 8x8x8: dezelfde rijen als het hoofd, zodat haar en baret blijven passen
- romp 8x12x6 -> 8x12x4, met de lange jas eroverheen getekend (een speler heeft geen losse jas
  die tot over de benen valt); de onderste 8 rijen van de jas komen bovenaan de benen
- arm en been 4x12x4: gelijk; de linkerarm en het linkerbeen zijn het gespiegelde rechter
- de grote neus van de pillager past niet op een speler en valt weg
"""
import sys
from PIL import Image


def vlak(u, v, w, h, d):
    """De zes vlakken van een doos met 'box-UV' op (u, v): naam -> (x, y, breedte, hoogte)."""
    return {
        "boven": (u + d, v, w, d), "onder": (u + d + w, v, w, d),
        "rechts": (u, v + d, d, h), "voor": (u + d, v + d, w, h),
        "links": (u + d + w, v + d, d, h), "achter": (u + 2 * d + w, v + d, w, h),
    }


def knip(im, r):
    x, y, w, h = r
    return im.crop((x, y, x + w, y + h))


def rijen(stuk, welke):
    """Een nieuw plaatje met alleen de gekozen rijen (in die volgorde)."""
    uit = Image.new("RGBA", (stuk.width, len(welke)))
    for ny, oy in enumerate(welke):
        uit.paste(stuk.crop((0, oy, stuk.width, oy + 1)), (0, ny))
    return uit


def kolommen(stuk, welke):
    uit = Image.new("RGBA", (len(welke), stuk.height))
    for nx, ox in enumerate(welke):
        uit.paste(stuk.crop((ox, 0, ox + 1, stuk.height)), (nx, 0))
    return uit


def over(onder, boven):
    """Teken 'boven' over 'onder' heen, alleen waar 'boven' niet doorzichtig is."""
    uit = onder.copy()
    uit.alpha_composite(boven)
    return uit


HOOFDRIJEN = [1, 2, 3, 4, 5, 6, 7, 8]          # van de 10 rijen: zonder de bovenste en de onderste
DIEPTE_6_NAAR_4 = [0, 2, 3, 5]                  # zijkant van de romp: 6 kolommen -> 4 (van voor naar achter)
TOP_6_NAAR_4 = [0, 2, 3, 5]                     # boven- en onderkant van de romp: 6 rijen -> 4


def zet_om(pad):
    oud = Image.open(pad).convert("RGBA")
    nieuw = Image.new("RGBA", (64, 64))

    # hoofd en baret
    for (u, v, h), (nu, nv), kies in (((0, 0, 10), (0, 0), HOOFDRIJEN), ((32, 0, 12), (32, 0), HOOFDRIJEN)):
        a, b = vlak(u, v, 8, h, 8), vlak(nu, nv, 8, 8, 8)
        for naam in ("boven", "onder"):
            nieuw.paste(knip(oud, a[naam]), b[naam][:2])
        for naam in ("rechts", "voor", "links", "achter"):
            nieuw.paste(rijen(knip(oud, a[naam]), kies), b[naam][:2])

    # romp met de jas eroverheen
    romp, jas = vlak(16, 20, 8, 12, 6), vlak(0, 38, 8, 20, 6)
    doel = vlak(16, 16, 8, 12, 4)
    for naam in ("voor", "achter", "rechts", "links"):
        stuk = over(knip(oud, romp[naam]), rijen(knip(oud, jas[naam]), range(12)))
        if naam in ("rechts", "links"):
            stuk = kolommen(stuk, DIEPTE_6_NAAR_4)
        nieuw.paste(stuk, doel[naam][:2])
    for naam in ("boven", "onder"):
        stuk = knip(oud, romp[naam])
        if naam == "boven":
            stuk = over(stuk, knip(oud, jas["boven"]))
        nieuw.paste(rijen(stuk, TOP_6_NAAR_4), doel[naam][:2])

    # rechterbeen, met onderaan de jas eroverheen (voor: linkerhelft van de jas, zijkant: rechterkant van de jas)
    been = {n: knip(oud, r) for n, r in vlak(0, 22, 4, 12, 4).items()}
    jasonder = {n: rijen(knip(oud, jas[n]), range(12, 20)) for n in ("voor", "achter", "rechts", "links")}
    def met_jas(stuk, jasstuk):
        laag = Image.new("RGBA", stuk.size)
        laag.paste(jasstuk, (0, 0))
        return over(stuk, laag)
    # De rechterkant van Piet zit links op de voorkant van het plaatje en rechts op de achterkant.
    rechts = dict(been)
    rechts["voor"] = met_jas(been["voor"], jasonder["voor"].crop((0, 0, 4, 8)))
    rechts["achter"] = met_jas(been["achter"], jasonder["achter"].crop((4, 0, 8, 8)))
    rechts["rechts"] = met_jas(been["rechts"], kolommen(jasonder["rechts"], DIEPTE_6_NAAR_4))
    links = dict(been)
    links["voor"] = met_jas(been["voor"], jasonder["voor"].crop((4, 0, 8, 8)).transpose(Image.FLIP_LEFT_RIGHT))
    links["achter"] = met_jas(been["achter"], jasonder["achter"].crop((0, 0, 4, 8)).transpose(Image.FLIP_LEFT_RIGHT))
    links["rechts"] = met_jas(been["rechts"], kolommen(jasonder["links"], DIEPTE_6_NAAR_4).transpose(Image.FLIP_LEFT_RIGHT))
    zet_ledemaat(nieuw, rechts, (0, 16), spiegel=False)
    zet_ledemaat(nieuw, links, (16, 48), spiegel=True)

    # armen
    arm = {n: knip(oud, r) for n, r in vlak(40, 46, 4, 12, 4).items()}
    zet_ledemaat(nieuw, arm, (40, 16), spiegel=False)
    zet_ledemaat(nieuw, arm, (32, 48), spiegel=True)

    nieuw.save(pad)
    print(f"  omgezet: {pad}")


def zet_ledemaat(nieuw, vlakken, plek, spiegel):
    """Zet een arm of been (4x12x4) neer. Gespiegeld: elk vlak omgedraaid en links/rechts gewisseld,
    zoals Minecraft het doet bij een 'mirror'-doos; zo ziet de linkerkant eruit als het spiegelbeeld."""
    doel = vlak(plek[0], plek[1], 4, 12, 4)
    for naam, r in doel.items():
        bron = naam
        if spiegel and naam in ("rechts", "links"):
            bron = "links" if naam == "rechts" else "rechts"
        stuk = vlakken[bron]
        if spiegel:
            stuk = stuk.transpose(Image.FLIP_LEFT_RIGHT)
        nieuw.paste(stuk, r[:2])


if __name__ == "__main__":
    for pad in sys.argv[1:]:
        zet_om(pad)
