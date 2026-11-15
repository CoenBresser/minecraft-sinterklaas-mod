#!/usr/bin/env python3
"""Controleert of de gemaakte assets kloppen met tools/maak_assets.py.

Draai eerst het script en dan deze controle, vanuit de projectmap:
    python3 tools/maak_assets.py
    python3 tools/controleer_assets.py

De controle kijkt met 'git status' naar alles wat het script veranderd of nieuw gemaakt heeft:
- een nieuw of verwijderd bestand is altijd een verschil;
- een plaatje (.png) telt alleen als verschil als de pixels anders zijn. Een andere versie van
  Pillow of zlib kan voor precies hetzelfde plaatje andere bytes opleveren; dat is geen verschil;
- elk ander bestand (zoals .json) wordt letterlijk vergeleken.

Bij een verschil noemt de controle de bestanden en stopt met foutcode 1.
"""
import io
import subprocess
import sys

from PIL import Image


def git(*args):
    return subprocess.run(["git", *args], check=True, capture_output=True).stdout


def gewijzigd():
    """(status, pad) voor alles wat anders is dan de laatste commit, nieuwe bestanden inbegrepen."""
    uit = git("status", "--porcelain=v1", "-z", "--untracked-files=all").decode()
    delen = uit.split("\0")
    regels, i = [], 0
    while i < len(delen) and delen[i]:
        status, pad = delen[i][:2], delen[i][3:]
        if "R" in status or "C" in status:
            i += 1   # bij hernoemen volgt het oude pad als extra veld
        regels.append((status, pad))
        i += 1
    return regels


def pixels(data):
    with Image.open(io.BytesIO(data)) as im:
        return im.size, im.convert("RGBA").tobytes()


def zelfde_plaatje(pad):
    """Zijn de pixels van het plaatje gelijk aan die in de laatste commit?"""
    try:
        oud = git("show", f"HEAD:{pad}")
        with open(pad, "rb") as f:
            nieuw = f.read()
        return pixels(oud) == pixels(nieuw)
    except (subprocess.CalledProcessError, OSError):
        return False


def main():
    verschillen = []
    for status, pad in gewijzigd():
        if status == "??":
            verschillen.append(f"nieuw:       {pad}")
        elif "D" in status:
            verschillen.append(f"verdwenen:   {pad}")
        elif pad.lower().endswith(".png") and zelfde_plaatje(pad):
            continue   # alleen andere bytes, dezelfde pixels
        else:
            verschillen.append(f"veranderd:   {pad}")
    if verschillen:
        print("De assets kloppen niet met tools/maak_assets.py. Deze bestanden verschillen:")
        for regel in verschillen:
            print("  " + regel)
        print()
        print("Draai python3 tools/maak_assets.py en commit het resultaat.")
        return 1
    print("De assets kloppen met tools/maak_assets.py.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
