# Python 3, tools/img2cli.py, Windows 11 x64 CLI sprite converter
import argparse
import os
import sys
from pathlib import Path
from PIL import Image


def remove_background(img: Image.Image, tolerance: int = 45) -> Image.Image:
    """Convert solid border-matched background pixels to transparent."""
    rgba = img.convert("RGBA")
    px = rgba.load()
    w, h = rgba.size

    # Sample top corners first (bust portraits often fill bottom corners with armor/shoulders)
    top_corners = [px[0, 0], px[w - 1, 0]]
    all_corners = top_corners + [px[0, h - 1], px[w - 1, h - 1]]
    bg = top_corners[0] if (
        abs(top_corners[0][0] - top_corners[1][0]) <= tolerance
        and abs(top_corners[0][1] - top_corners[1][1]) <= tolerance
        and abs(top_corners[0][2] - top_corners[1][2]) <= tolerance
    ) else max(set(all_corners), key=all_corners.count)
    bg_r, bg_g, bg_b, bg_a = bg

    if bg_a == 0:
        return rgba

    # Flood-fill from all border pixels matching the background color
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

    # Clear enclosed chroma-key pockets and dark/light JPEG magenta fringe along sprite edges
    if bg_r >= 200 and bg_b >= 200 and bg_g <= 60:
        for y in range(h):
            for x in range(w):
                r, g, b, a = px[x, y]
                if a > 0 and r >= 65 and b >= 65 and g <= 90 and (r - g) >= 35 and (b - g) >= 35 and abs(r - b) <= 60:
                    px[x, y] = (0, 0, 0, 0)

    return rgba


def crop_and_resize(
    img: Image.Image, target_w: int, target_h: int, crisp: bool = False
) -> Image.Image:
    """Auto-crop transparent margins and fit centered into target_w x target_h."""
    bbox = img.getbbox()
    if bbox:
        img = img.crop(bbox)

    src_w, src_h = img.size
    scale = min(target_w / src_w, target_h / src_h)
    new_w = max(1, int(round(src_w * scale)))
    new_h = max(1, int(round(src_h * scale)))

    # For pixel art, NEAREST preserves flat retro pixels without LANCZOS blur
    resample = Image.Resampling.NEAREST if crisp else Image.Resampling.LANCZOS
    resized = img.resize((new_w, new_h), resample)
    canvas = Image.new("RGBA", (target_w, target_h), (0, 0, 0, 0))
    offset_x = (target_w - new_w) // 2
    offset_y = target_h - new_h  # bottom-align feet to arena floor
    canvas.paste(resized, (offset_x, offset_y), resized)
    return canvas


def to_halfblock(
    img: Image.Image,
    cols: int,
    rows: int,
    alpha_thresh: int = 128,
    crisp: bool = False,
) -> str:
    """Render RGBA image as 24-bit ANSI truecolor half-blocks (2 vertical px per row)."""
    px_h = rows * 2
    canvas = crop_and_resize(img, cols, px_h, crisp=crisp)
    px = canvas.load()
    lines = []

    for row in range(rows):
        y_top = row * 2
        y_bot = y_top + 1
        out = []
        last_seq = ""
        for x in range(cols):
            tr, tg, tb, ta = px[x, y_top]
            br, bg, bb, ba = px[x, y_bot]
            top_on = ta >= alpha_thresh
            bot_on = ba >= alpha_thresh

            if not top_on and not bot_on:
                seq = "\x1b[0m"
                ch = " "
            elif top_on and not bot_on:
                seq = f"\x1b[0;38;2;{tr};{tg};{tb}m"
                ch = "▀"
            elif not top_on and bot_on:
                seq = f"\x1b[0;38;2;{br};{bg};{bb}m"
                ch = "▄"
            else:
                if (tr, tg, tb) == (br, bg, bb):
                    seq = f"\x1b[0;38;2;{tr};{tg};{tb}m"
                    ch = "█"
                else:
                    seq = f"\x1b[38;2;{tr};{tg};{tb};48;2;{br};{bg};{bb}m"
                    ch = "▀"

            if seq != last_seq:
                out.append(seq)
                last_seq = seq
            out.append(ch)

        if last_seq and last_seq != "\x1b[0m":
            out.append("\x1b[0m")
        lines.append("".join(out))

    return "\n".join(lines) + "\n"


def to_braille(img: Image.Image, cols: int, rows: int, luma_thresh: int = 90) -> str:
    """Render RGBA image as monochrome Unicode Braille (2x4 dots per cell)."""
    px_w = cols * 2
    px_h = rows * 4
    canvas = crop_and_resize(img, px_w, px_h)
    px = canvas.load()

    dot_bits = [
        (0, 0, 0x01),
        (0, 1, 0x02),
        (0, 2, 0x04),
        (1, 0, 0x08),
        (1, 1, 0x10),
        (1, 2, 0x20),
        (0, 3, 0x40),
        (1, 3, 0x80),
    ]

    lines = []
    for row in range(rows):
        out = []
        for col in range(cols):
            mask = 0
            for dx, dy, bit in dot_bits:
                r, g, b, a = px[col * 2 + dx, row * 4 + dy]
                if a >= 128:
                    luma = int(0.299 * r + 0.587 * g + 0.114 * b)
                    if luma >= luma_thresh:
                        mask |= bit
            out.append(chr(0x2800 + mask) if mask else " ")
        lines.append("".join(out))

    return "\n".join(lines) + "\n"


def to_shade(img: Image.Image, cols: int, rows: int) -> str:
    """Render RGBA image as monochrome Unicode block shading (░▒▓█)."""
    ramp = " ░▒▓█"
    canvas = crop_and_resize(img, cols, rows)
    px = canvas.load()
    lines = []
    for y in range(rows):
        out = []
        for x in range(cols):
            r, g, b, a = px[x, y]
            if a < 128:
                out.append(" ")
            else:
                luma = int(0.299 * r + 0.587 * g + 0.114 * b)
                idx = min(len(ramp) - 1, max(1, luma * (len(ramp) - 1) // 255))
                out.append(ramp[idx])
        lines.append("".join(out))
    return "\n".join(lines) + "\n"


def render_preview_png(
    img: Image.Image,
    cols: int,
    rows: int,
    out_png: Path,
    scale: int = 12,
    crisp: bool = False,
) -> None:
    """Export a crisp nearest-neighbor PNG preview of the exact pixel grid used in CLI."""
    canvas = crop_and_resize(img, cols, rows * 2, crisp=crisp)
    preview = Image.new("RGBA", (cols, rows * 2), (18, 18, 24, 255))
    preview.alpha_composite(canvas)
    preview = preview.resize((cols * scale, rows * 2 * scale), Image.Resampling.NEAREST)
    preview.save(out_png)


def main() -> int:
    parser = argparse.ArgumentParser(description="Convert character PNG to CLI sprite.")
    parser.add_argument("input", help="Input image path")
    parser.add_argument("-o", "--output", help="Output .txt sprite path")
    parser.add_argument("--mode", choices=["halfblock", "braille", "shade"], default="halfblock")
    parser.add_argument("--cols", type=int, default=30, help="Target terminal columns")
    parser.add_argument("--rows", type=int, default=14, help="Target terminal rows")
    parser.add_argument("--tolerance", type=int, default=50, help="Background color tolerance")
    parser.add_argument("--crisp", action="store_true", help="Use nearest-neighbor resampling for pixel art")
    parser.add_argument("--preview-png", help="Optional PNG preview path of the quantized CLI grid")
    parser.add_argument("--print", dest="do_print", action="store_true", help="Print sprite to stdout")
    args = parser.parse_args()

    src = Path(args.input)
    if not src.is_file():
        print(f"error: file not found: {src}", file=sys.stderr)
        return 1

    img = Image.open(src)
    clean = remove_background(img, tolerance=args.tolerance)

    if args.mode == "halfblock":
        sprite = to_halfblock(clean, args.cols, args.rows, crisp=args.crisp)
    elif args.mode == "braille":
        sprite = to_braille(clean, args.cols, args.rows)
    else:
        sprite = to_shade(clean, args.cols, args.rows)

    if args.output:
        out_path = Path(args.output)
        out_path.parent.mkdir(parents=True, exist_ok=True)
        out_path.write_text(sprite, encoding="utf-8")

    if args.preview_png:
        preview_path = Path(args.preview_png)
        preview_path.parent.mkdir(parents=True, exist_ok=True)
        render_preview_png(clean, args.cols, args.rows, preview_path, crisp=args.crisp)

    if args.do_print:
        sys.stdout.reconfigure(encoding="utf-8")
        sys.stdout.write(sprite)

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
