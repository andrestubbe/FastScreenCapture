# FastScreenCapture 0.1.1 [ALPHA] — Ultra-Fast Uncompressed Screen Capture CLI & Global Hotkey Daemon for Java

[![Status](https://img.shields.io/badge/status-0.1.1-brightgreen.svg)](https://github.com/andrestubbe/FastScreenCapture/releases/tag/0.1.1)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-0.1.1-green.svg)](https://jitpack.io/#andrestubbe/FastScreenCapture)

---

**⚡ Bit-perfect uncompressed screen grabs and direct 60 FPS video recording.** Direct DirectX 11 DXGI GPU capture, zero GC pressure, direct RAM pipe to FFmpeg, and native system-wide hotkeys.

`FastScreenCapture` was born out of frustration while recording high-frequency 3D particle benchmarks for **FastAnimation**: standard tools like **ShareX** and the native **Windows 11 Snipping Tool** introduced heavy compression blur, frame drops, and severe moiré interference on fine particle lines.

Instead of burning CPU on heavy compression or writing 30 GB of uncompressed bitmaps to the SSD, `FastScreenCapture` extracts pristine frames straight from DXGI Desktop Duplication and streams raw BGRA buffers directly into an optimized FFmpeg pipe — keeping CPU usage near 0% with zero dropped frames.

Watch Demo (YouTube) | Watch JMH Benchmark (YouTube)

<p align="center">
  <b>▶️ Watch the 60 FPS Demo Video on YouTube:</b> <a href="https://youtu.be/CBbNffXXvVc">https://youtu.be/CBbNffXXvVc</a>
</p>

<p align="center">
  <a href="https://youtu.be/CBbNffXXvVc" target="_blank" rel="noopener noreferrer">
    <img src="docs/screenshot.png" alt="FastScreenCapture Demo Video" width="800" />
  </a>
</p>

---

## Quick Start

### 1. Background Daemon (Recommended)
```cmd
FastScreenCapture.bat --daemon
```
Runs quietly in the background without UI lag or focus interruption:
- **`[F9]`**: **Toggle 60 FPS Video Recording** (starts/stops lossless MP4 stream via FFmpeg pipe with instant `+faststart` playback).
- **`[F10]`**: **Instant Bit-Perfect Screenshot** (uncompressed 32-bit BMP straight to `grabs/`).
- **Acoustic feedback**: High tone (1200 Hz) confirms recording start; low tone (450 Hz) confirms recording stop.

### 2. Instant Desktop Grab via CLI Launcher
```cmd
FastScreenCapture.bat
```
Captures the entire desktop at hardware resolution and writes a bit-perfect uncompressed `.bmp` into `grabs/`.

### 3. Programmatic Video & Screenshot API
```cmd
FastScreenCapture.bat --record 60 --fps 60 --out fastanimation_demo.mp4
```

---

## Table of Contents

- [Why FastScreenCapture?](#why-FastScreenCapture)
- [Key Features](#key-features)
- [Real-World Use Cases](#real-world-use-cases)
- [Architecture & Pipeline](#architecture--pipeline)
- [Performance Benchmarks](#performance-benchmarks)
- [Installation](#installation)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [License](#license)
- [Related Projects](#related-projects)

---

## Why FastScreenCapture?

Traditional capture tools are poorly suited for latency-critical tasks, high-frequency analysis, and bit-accurate image verification:

1. **Sluggish UI & Desktop Freezes**: Snipping Tool and ShareX interrupt desktop interaction, causing UI stutter and focus drops.
2. **Forced CPU Compression Overhead**: Enforcing PNG/JPEG encoding burns 20–100 ms of CPU time per frame and creates compression artifacts.
3. **Severe GC Stalls**: Legacy Java capture tools allocate tens of megabytes per frame on the JVM heap.

**FastScreenCapture** bypasses these limitations:
- **Direct DXGI Desktop Duplication**: Raw GPU framebuffer extraction in <1 ms via `FastScreen`.
- **Bit-Perfect 100% Raw BMP**: Lossless uncompressed files instantly openable in any viewer or editor.
- **Global Low-Latency Hooks**: Instantaneous event response via `FastHotkey`.
- **Zero JVM GC Allocations**: Zero-copy off-heap memory path straight to disk.

---

## Key Features

- ⚡ **Instant Capture Throughput** — Captures and commits full desktop frames in milliseconds.
- 🎯 **Bit-Perfect Quality** — 100% uncompressed raw BGRA/ARGB data without lossy artifacts.
- ⌨️ **Global Native Hotkeys** — Background daemon mode listening for `F1`–`F12` or `PrintScreen`.
- 📁 **Direct-to-Disk DMA** — Streamlined Little-Endian BMP serialization bypassing OS memory bloat.
- 🔗 **FastJava Synergy** — Powered by `FastScreen`, `FastHotkey`, and `FastCore`.

---

## Real-World Use Cases

- ⚡ **Zero-Lag Screenshot Hotkey Daemon**: Replace bloated screenshot utilities (ShareX, Windows Snipping Tool) with a background process that commits full-resolution desktop captures in <6 ms without UI pauses or window flashes.
- 🎯 **Bit-Exact Visual Regression & UI Testing**: Capture pristine, 100% uncompressed frames during automated UI / rendering test suites (e.g., Selenium, JavaFX, OpenGL, Vulkan) where PNG compression artifacts or color subsampling would cause false positive diffs.
- 🏎️ **High-Speed Gameplay & Event Bursting**: Burst-capture 5–60 consecutive uncompressed frames (`--burst 10`) at full monitor refresh rates during rapid in-game action, physics anomalies, or micro-stutters.
- 🤖 **Dataset Generation for Vision & OCR Pipelines**: Rapidly collect hundreds of raw screen patches per second without GPU/CPU encoding bottlenecks for training OCR, layout parsing, or object detection models.
- 📊 **Financial & High-Frequency Trading Telemetry**: Archive pixel-accurate order book states, charts, and millisecond ticker snapshots without introducing thread latency to execution algorithms.

---

## Architecture & Pipeline

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                 Windows OS Global Hotkey Daemon (FastHotkey)                │
│                 [F10] Single Screenshot  |  [F9] Toggle Video               │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │ Instant Key Event (<0.1ms)
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                       FastScreenCapture Daemon Engine                       │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │ Direct DXGI Extraction (<1ms)
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                                 FastScreen                                  │
│                  (DirectX 11 DXGI GPU Desktop Duplication)                  │
└──────────────────┬───────────────────────────────────────┬──────────────────┘
                   │                                       │
    [F10] Single Frame                             [F9] 60 FPS Stream
                   ▼                                       ▼
┌─────────────────────────────────────┐ ┌─────────────────────────────────────┐
│            FastBmpWriter            │ │     Zero-Copy Direct Pipe (RAM)     │
│    (54-Byte Top-Down DIB Header)    │ │    Direct Memory -> FFmpeg stdin    │
└──────────────────┬──────────────────┘ └──────────────────┬──────────────────┘
                   │ Direct NIO Channel Write              │ Lossless H.264 +faststart
                   ▼                                       ▼
┌─────────────────────────────────────┐ ┌─────────────────────────────────────┐
│       Bit-Perfect Raw BMP           │ │     Instant-Play 60 FPS MP4         │
│     (NVMe Storage: grabs/*.bmp)     │ │   (NVMe Storage: grabs/*.mp4)       │
└─────────────────────────────────────┘ └─────────────────────────────────────┘
```

---

## Performance Benchmarks

### 1. Direct-to-Disk Raw BMP Writer (JMH)
Measured on official JMH benchmark suite (`run-benchmark.bat`):

```text
Benchmark                                     Mode  Cnt   Score   Error   Units
Benchmark.benchmarkFastScreenCaptureBmpWriter thrpt    3   0.221          ops/ms
```
* **Throughput**: **>220 full frames / sec** uncompressed 32-bit (800×600) written directly to disk.
* **Heap GC Pressure**: **0 bytes** temporary allocations on the JVM heap.

### 2. Lossless 60 FPS FFmpeg Pipe Streaming
Measured during continuous 60-second real-time capture of the [FastAnimation](https://github.com/andrestubbe/FastAnimation) demo (1440×960 @ 60 FPS):

```text
Metric                               Value          Impact
DXGI Desktop Acquisition Latency     < 0.8 ms       Sub-millisecond frame availability
Direct Memory Pipe Write Latency     < 0.3 ms       Immediate transfer to encoder stdin
Sustained Stream Throughput          60.0 FPS       100% stable framerate, 0 dropped frames
Average Process CPU Overhead         < 1.0 %        Barely registers during intense 3D simulations
Disk I/O Churn                       0 MB/s temp    Zero intermediate disk writes; pure RAM pipe
```

> [!NOTE]
> **Environment & Setup**: Measured on an 11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz (4C/8T), Windows 11 Home, OpenJDK 21 LTS. By bypassing both the JVM garbage collector and disk-caching bottlenecks, `FastScreenCapture` records smooth 60 FPS MP4 video while compute-heavy simulations run at full hardware speed.

---

## API Quick Reference

### Java API Reference

#### 1. Video Recording API (`FastScreenCapture`)

| Class | Method | Return Type | Description |
|:---|:---|:---:|:---|
| `FastScreenCapture` | `recordVideo(String out, int x, int y, int w, int h, int sec, int fps)` | `void` | Streams lossless 60 FPS video directly into an FFmpeg pipe without disk I/O bottlenecks (`+faststart` MP4). |

#### 2. Raw Bitmap Writer API (`FastBmpWriter`)

| Class | Method | Return Type | Description |
|:---|:---|:---:|:---|
| `FastBmpWriter` | `writeBmp(String path, int w, int h, int[] pixels)` | `void` | Writes 32-bit ARGB/BGRA pixel array directly to uncompressed Windows BMP file (top-down DIB). |
| `FastBmpWriter` | `writeDirectBgra(String path, int w, int h, ByteBuffer buffer)` | `void` | **Zero-Copy**: Streams direct off-heap native buffer straight to disk via NIO FileChannel. |

### CLI Options Reference

| Switch | Long Option | Argument | Description | Default |
|:---|:---|:---:|:---|:---:|
| `-c` | `--cursor` | - | Renders the exact, active system cursor into the capture | `false` |
| - | `--record` | `[seconds]` | Direct lossless 60 FPS video recording via FFmpeg pipe | `60` |
| - | `--fps` | `<fps>` | Target recording frame rate | `60` |
| `-d` | `--daemon` | - | Runs continuously in background (hotkeys: `F9` Video, `F10` Screenshot) | `false` |
| `-k` | `--hotkey` | `<KEY>` | Screenshot hotkey in daemon mode (`F1`–`F12`, `PRINTSCREEN`) | `F10` |
| `-o` | `--out` | `<path>` | Custom output file path (`.bmp` or `.mp4`) | `grabs/grab_...` |
| `-r` | `--rect` | `<x,y,w,h>` | Explicit capture region coordinates | Full screen |
| `-b` | `--burst` | `<count>` | Consecutive frame burst count | `1` |
| `-h` | `--help` | - | Prints usage instructions and switches | - |

---

## FFmpeg Requirement & Quick Setup

To enable high-speed direct-pipe video recording without disk I/O bottlenecks, `FastScreenCapture` requires FFmpeg. FastScreenCapture will automatically detect FFmpeg in your system `PATH` or in standard WinGet directories.

Install FFmpeg in one command via Windows Package Manager:

```cmd
winget install Gyan.FFmpeg
```

Alternatively, download the official Windows build directly from [gyan.dev/ffmpeg/builds](https://www.gyan.dev/ffmpeg/builds/) (e.g. `ffmpeg-release-essentials.zip`) and add its `bin` folder to your system `PATH`.

---

## Installation

### Option 1: Maven (`pom.xml`)

Add the JitPack repository and the dependencies to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <!-- FastScreenCapture Engine -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastScreenCapture</artifactId>
        <version>0.1.1</version>
    </dependency>

    <!-- Underlying Native & Zero-Copy Hardware Engines -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastScreen</artifactId>
        <version>0.1.4</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fasthotkey</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastImage</artifactId>
        <version>0.1.4</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastSIMD</artifactId>
        <version>0.1.3</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastMemory</artifactId>
        <version>0.1.1</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastPointer</artifactId>
        <version>0.1.1</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastCore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (`build.gradle`)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastScreenCapture:0.1.1'
    implementation 'com.github.andrestubbe:FastScreen:0.1.4'
    implementation 'com.github.andrestubbe:fasthotkey:0.1.0'
    implementation 'com.github.andrestubbe:FastImage:0.1.4'
    implementation 'com.github.andrestubbe:FastSIMD:0.1.3'
    implementation 'com.github.andrestubbe:FastMemory:0.1.1'
    implementation 'com.github.andrestubbe:FastPointer:0.1.1'
    implementation 'com.github.andrestubbe:FastCore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

1. 📸 [**FastScreenCapture-0.1.1.jar**](https://github.com/andrestubbe/FastScreenCapture/releases/tag/0.1.1) (The CLI & Capture Engine)
2. 🖥️ [**FastScreen-0.1.4.jar**](https://github.com/andrestubbe/FastScreen/releases/tag/0.1.4) (DirectX 11 DXGI Desktop Capture & Native Engine)
3. ⌨️ [**FastHotkey-0.1.0.jar**](https://github.com/andrestubbe/FastHotkey/releases/tag/0.1.0) (Low-Latency Win32 System Hotkeys)
4. 🖼️ [**FastImage-0.1.4.jar**](https://github.com/andrestubbe/FastImage/releases/tag/0.1.4) (Zero-Copy Frame Container & SIMD Filters)
5. ⚡ [**FastSIMD-0.1.3.jar**](https://github.com/andrestubbe/FastSIMD/releases/tag/0.1.3) (Vectorized SIMD Math Core)
6. 🧠 [**FastMemory-0.1.1.jar**](https://github.com/andrestubbe/FastMemory/releases/tag/0.1.1) (High-Performance Off-Heap Memory Manager)
7. 📍 [**FastPointer-0.1.1.jar**](https://github.com/andrestubbe/FastPointer/releases/tag/0.1.1) (Zero-Overhead Direct Memory Pointer Bridge)
8. ⚙️ [**FastCore-0.1.0.jar**](https://github.com/andrestubbe/FastCore/releases/tag/0.1.0) (Mandatory Native Library & DLL Auto-Loader)

> [!IMPORTANT]
> FastScreenCapture requires all 8 JARs above on the classpath when running standalone without a package manager. All native `.dll` binaries are extracted and loaded automatically into RAM on first run by `FastCore`.

---

## Documentation

* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: The engineering rationale for uncompressed zero-latency captures.
* **[REFERENCE.md](docs/REFERENCE.md)**: Complete CLI switches and Java API reference.
* **[CHANGELOG.md](docs/CHANGELOG.md)**: Full release history and version notes.
* **[ROADMAP.md](docs/ROADMAP.md)**: Upcoming features, 60+ FPS video streaming, and ecosystem milestones.

---

## Platform Support

| Platform | Status |
|---|---|
| Windows 10/11 (x64) | ✅ Fully Supported (DirectX 11 DXGI + Win32 Hooks) |
| Linux | 🚧 Planned |
| macOS | 🚧 Planned |

---

## License

MIT License — See [LICENSE](LICENSE) for details.

---

## Related Projects

- [FastScreen](https://github.com/andrestubbe/FastScreen) — Ultra-Fast DirectX Screen Capture Engine
- [FastAnimation](https://github.com/andrestubbe/FastAnimation) — High-Performance Zero-GC 60/120 FPS Animation Engine
- [FastCamera](https://github.com/andrestubbe/FastCamera) — Hardware-Accelerated Native Camera Capture & Streaming
- [FastHotkey](https://github.com/andrestubbe/FastHotkey) — Low-Latency Global System Hotkeys
- [FastImage](https://github.com/andrestubbe/FastImage) — SIMD-Accelerated Off-Heap Image Engine
- [FastDWM](https://github.com/andrestubbe/FastDWM) — Native Windows Desktop Window Manager & VSync Engine
- [FastSIMD](https://github.com/andrestubbe/FastSIMD) — AVX2/AVX-512 Vectorized Math Acceleration
- [FastPointer](https://github.com/andrestubbe/FastPointer) — Safe Zero-Overhead Direct Memory Pointer Bridge
- [FastMemory](https://github.com/andrestubbe/FastMemory) — High-Performance Off-Heap Memory Buffers
- [FastCore](https://github.com/andrestubbe/FastCore) — Native Library Loader for Java

---
**Part of the FastJava Ecosystem** — *Making the JVM faster. ⚡*
