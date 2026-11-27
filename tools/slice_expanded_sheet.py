# Python 3, tools/slice_expanded_sheet.py, Windows 11 x64
# Slices the 2x4 expanded sprite sheet into 8 crisp 34x17 ANSI half-block CLI sprites.
import sys
from pathlib import Path
from PIL import Image

sys.path.insert(0, str(Path(__file__).resolve().parent))
from img2cli import to_halfblock, crop_and_resize
from slice_and_convert import remove_magenta_safe

SRC = Path(r"C:\Users\acebr\.gemini\antigravity\brain\f2a9f678-3b58-452c-b38d-a978528e0fb1\retro_rpg_sprite_sheet_1795778680549.jpg")
PROJ = Path(r"C:\Users\acebr\Documents\Z2NDYR\Clash_Of_Minds-OOP101-Group1")
ASSETS = PROJ / "assets"
SPRITES = PROJ / "sprites"
ART_DIR = Path(r"C:\Users\acebr\.gemini\antigravity\brain\f2a9f678-3b58-452c-b38d-a978528e0fb1")


def main() -> int:
    ASSETS.mkdir(parents=True, exist_ok=True)
    SPRITES.mkdir(parents=True, exist_ok=True)

    sheet = Image.open(SRC)
    sheet.save(ASSETS / "expanded_spritesheet.jpg")
    w, h = sheet.size
    mid_y = int(h * 0.50)

    # Exact 2x4 grid slices (with safe inner margins between columns)
    col_bounds = [0, int(w * 0.255), int(w * 0.505), int(w * 0.765), w]
    layout = [
        ("student_2",             (col_bounds[0], 0,     col_bounds[1], mid_y)),
        ("student_3",             (col_bounds[1], 0,     col_bounds[2], mid_y)),
        ("student_4",             (col_bounds[2], 0,     col_bounds[3], mid_y)),
        ("gameover_icon",         (col_bounds[3], 0,     col_bounds[4], mid_y)),
        ("professor_loopwell",    (col_bounds[0], mid_y, col_bounds[1], h)),
        ("professor_stacksmann",  (col_bounds[1], mid_y, col_bounds[2], h)),
        ("professor_nullpointer", (col_bounds[2], mid_y, col_bounds[3], h)),
        ("victory_icon",          (col_bounds[3], mid_y, col_bounds[4], h)),
    ]

    tiles = []
    for name, box in layout:
        cropped = sheet.crop(box)
        clean = remove_magenta_safe(cropped)
        bbox = clean.getbbox()
        if bbox:
            clean = clean.crop(bbox)
        clean.save(ASSETS / f"{name}_gen.png")

        sprite_txt = to_halfblock(clean, cols=34, rows=17, crisp=True)
        out_txt = SPRITES / f"{name}.txt"
        out_txt.write_text(sprite_txt, encoding="utf-8")
        print(f"converted {name}: {out_txt} ({len(sprite_txt)} bytes)")

        canvas = crop_and_resize(clean, 34, 34, crisp=True)
        tile = Image.new("RGBA", (34, 34), (16, 16, 22, 255))
        tile.alpha_composite(canvas)
        tiles.append(tile.resize((34 * 6, 34 * 6), Image.Resampling.NEAREST))

    tw, th = 34 * 6, 34 * 6
    pad = 10
    qa = Image.new("RGBA", (4 * (tw + pad) + pad, 2 * (th + pad) + pad), (10, 10, 14, 255))
    for i, t in enumerate(tiles):
        r, c = divmod(i, 4)
        qa.paste(t, (pad + c * (tw + pad), pad + r * (th + pad)))
    qa_path = ART_DIR / "expanded_qa.png"
    qa.save(qa_path)
    print(f"qa sheet: {qa_path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
