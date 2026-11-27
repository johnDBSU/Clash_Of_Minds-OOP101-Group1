# Python 3, tools/slice_and_convert.py, Windows 11 x64
# Slices the 5-character AI sprite sheet and converts each to 34x17 crisp ANSI half-block CLI sprites.
import sys
from pathlib import Path
from PIL import Image

sys.path.insert(0, str(Path(__file__).resolve().parent))
from img2cli import to_halfblock, crop_and_resize

SRC = Path(r"C:\Users\acebr\.gemini\antigravity\brain\f2a9f678-3b58-452c-b38d-a978528e0fb1\java_rpg_character_spritesheet_1795777793164.jpg")
PROJ = Path(r"C:\Users\acebr\Documents\Z2NDYR\Clash_Of_Minds-OOP101-Group1")
ASSETS = PROJ / "assets"
SPRITES = PROJ / "sprites"
ART_DIR = Path(r"C:\Users\acebr\.gemini\antigravity\brain\f2a9f678-3b58-452c-b38d-a978528e0fb1")


def remove_magenta_safe(img: Image.Image, tolerance: int = 75) -> Image.Image:
    """Flood-fill remove #FF00FF bg + clean 2px magenta fringe without touching purple clothing."""
    rgba = img.convert("RGBA")
    px = rgba.load()
    w, h = rgba.size

    bg_r, bg_g, bg_b = 255, 0, 255
    visited = [[False] * w for _ in range(h)]
    stack = []
    for x in range(w):
        stack.append((x, 0))
        stack.append((x, h - 1))
    for y in range(h):
        stack.append((0, y))
        stack.append((w - 1, y))

    while stack:
        x, y = stack.pop()
        if x < 0 or x >= w or y < 0 or y >= h or visited[y][x]:
            continue
        r, g, b, a = px[x, y]
        if a == 0 or (
            abs(r - bg_r) <= tolerance
            and abs(g - bg_g) <= tolerance
            and abs(b - bg_b) <= tolerance
        ):
            visited[y][x] = True
            px[x, y] = (0, 0, 0, 0)
            stack.append((x + 1, y))
            stack.append((x - 1, y))
            stack.append((x, y + 1))
            stack.append((x, y - 1))

    # Clear enclosed vivid #FF00FF pockets (e.g. between arm and body) without touching dark purple
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            if a > 0 and r >= 205 and b >= 205 and g <= 75 and abs(r - b) <= 35:
                px[x, y] = (0, 0, 0, 0)

    # 2-pass edge fringe scrub: remove dark/mid magenta anti-aliasing pixels adjacent to transparency
    for _ in range(2):
        to_clear = []
        for y in range(h):
            for x in range(w):
                r, g, b, a = px[x, y]
                if a == 0:
                    continue
                if r >= 75 and b >= 75 and g <= 85 and (r - g) >= 35 and (b - g) >= 35 and abs(r - b) <= 45:
                    # Only clear if touching a transparent pixel
                    for dx, dy in ((-1, 0), (1, 0), (0, -1), (0, 1)):
                        nx, ny = x + dx, y + dy
                        if 0 <= nx < w and 0 <= ny < h and px[nx, ny][3] == 0:
                            to_clear.append((x, y))
                            break
        for x, y in to_clear:
            px[x, y] = (0, 0, 0, 0)

    return rgba


def main() -> int:
    ASSETS.mkdir(parents=True, exist_ok=True)
    SPRITES.mkdir(parents=True, exist_ok=True)

    sheet = Image.open(SRC)
    sheet.save(ASSETS / "roster_spritesheet.jpg")
    w, h = sheet.size

    slices = [
        ("quick_coder", (0, 0, int(w * 0.33), int(h * 0.50))),
        ("wise_scholar", (int(w * 0.33), 0, int(w * 0.67), int(h * 0.50))),
        ("lucky_rookie", (int(w * 0.67), 0, w, int(h * 0.50))),
        ("student", (0, int(h * 0.50), int(w * 0.50), h)),
        ("professor", (int(w * 0.50), int(h * 0.50), w, h)),
    ]

    tiles = []
    for name, box in slices:
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
        tiles.append(tile.resize((34 * 8, 34 * 8), Image.Resampling.NEAREST))

    tw, th = 34 * 8, 34 * 8
    pad = 12
    qa = Image.new("RGBA", (len(tiles) * (tw + pad) + pad, th + pad * 2), (10, 10, 14, 255))
    for i, t in enumerate(tiles):
        qa.paste(t, (pad + i * (tw + pad), pad))
    qa_path = ART_DIR / "roster_final_qa.png"
    qa.save(qa_path)
    print(f"qa sheet: {qa_path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
