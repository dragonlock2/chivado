# chivado

Deploy Chisel on Xilinx FPGAs using Docker and Vivado. Intended for use on MacOS since Vivado isn't supported without a VM.

## Setup

First we need to build the Docker image with Vivado.

1. Download the [Xilinx Unified Installer SFD](https://www.xilinx.com/support/download.html) from Xilinx's website and place it in the repo root. Modify the `INSTALLER` argument and `echo` line in `Dockerfile` to match its name and version.
1. Increase the disk image size limit in Docker to >350GB. The image should end up taking ~100GB.
1. Enable the experimental features in the Docker daemon for `--squash` support.
1. Run `make setup`.
1. Optionally run `docker system prune` to reclaim all that space.
1. Optionally delete the Xilinx Unified Installer file to save space.

Next we need to install SBT.

1. Run `brew install sbt`.

## Run

First change `PART_NUM` and `NUM_CPU` in `Makefile` as well as `src/main/script/top.xdc` to match your setup.

### Build

Running `make synth` compiles the Chisel down to Verilog, copies the top level, XDC, and Tcl scripts over to the Docker container, and runs synthesis and implementation there. The final Vivado project, including the bitstream and timing reports, is copied back to the local `build/` folder.

### Flashing

Flashing to SRAM can be done with `make flash_sram`. For QSPI flash, first set `QSPI_PART_NUM` in `Makefile` and then run `make flash_qspi`.
