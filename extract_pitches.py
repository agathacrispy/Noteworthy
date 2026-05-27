"""
Extracts a pitches.csv from a vocals WAV file using autocorrelation pitch detection.

Usage:
    python extract_pitches.py <path_to_vocals.wav> <output_dir>

Example:
    python extract_pitches.py songs/dangerous-woman/vocals.wav songs/dangerous-woman

Requirements:
    pip install scipy numpy
"""

import sys
import math
import csv
import numpy as np
from scipy.io import wavfile

HOP_MS = 23          # interval between frames in ms (~512/22050)
SILENCE_RMS = 0.01   # frames quieter than this are marked -1
MIN_FREQ = 65        # C2 Hz
MAX_FREQ = 1047      # C6 Hz


def hz_to_midi(hz):
    if hz <= 0 or math.isnan(hz):
        return None
    return round(69 + 12 * math.log2(hz / 440.0))


def detect_pitch(frame, sr):
    """Autocorrelation-based pitch detection. Returns MIDI note or -1 for silence."""
    rms = np.sqrt(np.mean(frame ** 2))
    if rms < SILENCE_RMS:
        return -1

    # Autocorrelation via FFT
    n = len(frame)
    fft = np.fft.rfft(frame, n=n * 2)
    corr = np.fft.irfft(fft * np.conj(fft))[:n]
    corr /= (corr[0] + 1e-9)  # normalise

    min_lag = int(sr / MAX_FREQ)
    max_lag = int(sr / MIN_FREQ)
    if max_lag >= n:
        return -1

    peak = int(np.argmax(corr[min_lag:max_lag])) + min_lag
    freq = sr / peak
    midi = hz_to_midi(freq)
    return midi if midi is not None else -1


def extract(vocals_path, output_dir):
    sr, audio = wavfile.read(vocals_path)

    # Collapse to mono float in [-1, 1]
    if audio.ndim > 1:
        audio = audio.mean(axis=1)
    audio = audio.astype(np.float32)
    if np.max(np.abs(audio)) > 1.0:
        audio /= 32768.0

    hop = int(sr * HOP_MS / 1000)
    rows = []

    for i in range(0, len(audio) - hop, hop):
        frame = audio[i:i + hop]
        timestamp_ms = round(i / sr * 1000)
        midi = detect_pitch(frame, sr)
        rows.append((timestamp_ms, midi))

    out_path = f"{output_dir}/pitches.csv"
    with open(out_path, "w", newline="") as f:
        writer = csv.writer(f)
        for timestamp_ms, midi in rows:
            writer.writerow([timestamp_ms, midi])

    print(f"Wrote {len(rows)} pitch frames to {out_path}")


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python extract_pitches.py <vocals.wav> <output_dir>")
        sys.exit(1)
    extract(sys.argv[1], sys.argv[2])
