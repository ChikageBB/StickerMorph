import sys
from pathlib import Path
from rlottie_python import LottieAnimation

def main():
    if len(sys.argv) != 3:
        print("usage: tgs_to_frames.py <input.tgs> <output_dir>", file=sys.stderr)
        sys.exit(2)

    in_tgs = sys.argv[1]
    out_dir = Path(sys.argv[2])
    out_dir.mkdir(parents=True, exist_ok=True)

    anim = LottieAnimation.from_tgs(in_tgs)
    total = anim.lottie_animation_get_totalframe()
    fps = anim.lottie_animation_get_framerate()

    for i in range(total):
        anim.render_pillow_frame(frame_num=i).save(out_dir / f"frame_{i:05d}.png")

    print(f"{fps}")

if __name__ == "__main__":
    main()