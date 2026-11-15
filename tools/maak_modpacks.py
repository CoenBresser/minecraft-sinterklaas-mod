#!/usr/bin/env python3
"""Maakt van de gebouwde mod-jars modpacks die de CurseForge-app kan importeren.
Draai vanuit de projectmap, nadat beide versies gebouwd zijn:  python3 tools/maak_modpacks.py

Een modpack is een zip met een manifest.json (welke Minecraft-versie, welke modloader en welke mods
van CurseForge) en een map overrides/mods met jars die er zo in gaan. Importeren in CurseForge maakt
er een nieuw profiel van, met de goede versies al ingesteld.

De CurseForge-app toont in de lijst Mods van een profiel alleen de mods die hij zelf downloadt, dus
die in "files" van het manifest staan. Wat via overrides/mods meekomt werkt wel, maar staat niet in
die lijst. Daarom:

- Fabric (Minecraft 26.2): Fabric API, die de mod nodig heeft, staat in "files"; de app haalt die
  van CurseForge. De mod zelf staat niet op CurseForge en gaat via overrides/mods.
- Forge (Minecraft 1.20.1): alleen de mod, via overrides/mods; die heeft verder niets nodig.

De zips komen in build/modpacks/.
"""
import glob, json, os, sys, zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
UIT = os.path.join(ROOT, "build/modpacks")
# Fabric API op CurseForge: https://www.curseforge.com/minecraft/mc-mods/fabric-api
FABRIC_API_CURSEFORGE_PROJECT = 306612

def eigenschappen(pad):
    """Leest een gradle.properties als woordenboek."""
    uit = {}
    with open(pad) as f:
        for regel in f:
            regel = regel.strip()
            if regel and not regel.startswith("#") and "=" in regel:
                sleutel, waarde = regel.split("=", 1)
                uit[sleutel.strip()] = waarde.strip()
    return uit

def mod_jar(map_):
    """De ene mod-jar in een build/libs-map (geen -sources of -dev)."""
    jars = [j for j in glob.glob(os.path.join(ROOT, map_, "build/libs/*.jar"))
            if not j.endswith(("-sources.jar", "-dev.jar"))]
    if len(jars) != 1:
        sys.exit(f"Verwacht precies één jar in {map_}/build/libs, gevonden: {jars or 'geen'}. Eerst bouwen?")
    return jars[0]

def curseforge_mod(project, bestand):
    """Een mod die de CurseForge-app zelf downloadt (een regel in "files" van het manifest)."""
    return {"projectID": project, "fileID": int(bestand), "required": True}

def modpack(bestand, naam, versie, minecraft, modloader, jars, curseforge_mods=()):
    manifest = {
        "minecraft": {"version": minecraft, "modLoaders": [{"id": modloader, "primary": True}]},
        "manifestType": "minecraftModpack",
        "manifestVersion": 1,
        "name": naam,
        "version": versie,
        "author": "De makers van de Sinterklaas-mod",
        # Mods die de app van CurseForge haalt; alleen die staan daarna in de lijst Mods van het profiel.
        "files": list(curseforge_mods),
        "overrides": "overrides",
    }
    pad = os.path.join(UIT, bestand)
    with zipfile.ZipFile(pad, "w", zipfile.ZIP_DEFLATED) as z:
        z.writestr("manifest.json", json.dumps(manifest, indent=2) + "\n")
        for jar in jars:
            z.write(jar, "overrides/mods/" + os.path.basename(jar))
    print(f"  {os.path.relpath(pad, ROOT)}: Minecraft {minecraft}, {modloader}, {len(jars)} jar(s), "
          f"{len(curseforge_mods)} mod(s) van CurseForge")

if __name__ == "__main__":
    hoofd = eigenschappen(os.path.join(ROOT, "gradle.properties"))
    forge = eigenschappen(os.path.join(ROOT, "forge-1.20.1/gradle.properties"))
    versie = hoofd["mod_version"]
    os.makedirs(UIT, exist_ok=True)

    modpack(f"sinterklaas-modpack-{versie}+fabric-mc{hoofd['minecraft_version']}.zip",
            f"Sinterklaas (Fabric {hoofd['minecraft_version']})", versie,
            hoofd["minecraft_version"], f"fabric-{hoofd['loader_version']}",
            [mod_jar("fabric")],
            [curseforge_mod(FABRIC_API_CURSEFORGE_PROJECT, hoofd["fabric_api_curseforge_file"])])
    modpack(f"sinterklaas-modpack-{versie}+forge-mc{forge['minecraft_version']}.zip",
            f"Sinterklaas (Forge {forge['minecraft_version']})", versie,
            forge["minecraft_version"], f"forge-{forge['forge_version']}",
            [mod_jar("forge-1.20.1")])
    print("klaar")
