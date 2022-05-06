# chivado

Deploy Chisel on Xilinx FPGAs using Docker and Vivado. Intended for use on MacOS since Vivado isn't supported without a VM.

## Setup

1. Download the [Xilinx Unified Installer 2022.1 SFD](https://www.xilinx.com/support/download.html) from Xilinx's website. Modify the `INSTALLER` argument in `Dockerfile` to match its location.
1. Increase the disk image size limit in Docker to >350GB. The image should end up taking ~100GB.
1. Enable the experimental features in the Docker daemon for `--squash` support.
1. Run `make setup`.
1. Optionally run `docker system prune` to reclaim all that space.
1. Optionally delete the Xilinx Unified Installer file to save space.

## Deploy
