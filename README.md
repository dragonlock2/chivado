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

Next we need to install local dependencies.

1. Run `brew install sbt openfpgaloader`.

## Run

First change `PART_NUM`, `NUM_CPU`, and `TOP_MODULE` in `Makefile` as well as `src/main/script/top.xdc` to match your setup. See `src/main/scala` for how to add your own Chisel and configure the top level.

### Build

Since Chisel IO names are not always consistent, you may need to modify `TOP_MODULE` and `src/main/script/top.xdc` again. Run `make build` to generate the top level Verilog in `build/` and check.

Running `make synth` compiles the Chisel down to Verilog, copies the top level, XDC, and Tcl scripts over to the Docker container, and runs synthesis and implementation there. The final Vivado project, including the bitstream and timing reports, is copied back to the local `build/` folder.

### Flashing

Since Docker on MacOS doesn't support USB device passthrough yet, we'll use [openFPGALoader](https://github.com/trabucayre/openFPGALoader). First change `CABLE` in `Makefile` to match your programming cable.

For SRAM flashing, run `make flash`.

For QSPI flash programming, run `make flash_qspi`. WIP

## Extra

### Adding IP (WIP)
