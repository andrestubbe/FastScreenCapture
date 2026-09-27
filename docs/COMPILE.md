# Building FastScreenCapture ???

Complete build guide for packaging FastScreenCapture, compiling the CLI daemon, and executing benchmarks.

---

## Prerequisites

* **Windows 10 or 11 (64-bit)**
* **JDK 17+** ([Eclipse Adoptium](https://adoptium.net/) or [Oracle JDK](https://www.oracle.com/java/technologies/downloads/))
* **Maven 3.9+**
* **FFmpeg** (optional for 60 FPS video recording): `winget install Gyan.FFmpeg`

---

## Automated Maven Packaging

FastScreenCapture is a high-performance Java and DirectX 11 capture suite powered by `FastScreen` and `FastCore`.

Build and install to your local Maven repository:

```bash
# In the FastScreenCapture repository root:
mvn clean install -DskipTests
```

---

## Launching CLI & Hotkey Daemon

To launch the interactive CLI and global system hotkey daemon:

```cmd
FastScreenCapture.bat --daemon
```

---

## JMH Benchmarking

To build and execute the official JMH throughput benchmark suite:

```cmd
run-benchmark.bat
```

---
**Part of the FastJava Ecosystem** ? *Making the JVM faster. ?*
