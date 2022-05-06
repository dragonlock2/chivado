FROM ubuntu:jammy

ARG INSTALLER="Xilinx_Unified_2022.1_0420_0327.tar.gz"

RUN apt update && apt upgrade -y

WORKDIR /root/

RUN mkdir install/
COPY ${INSTALLER} ./
RUN tar -xzf ${INSTALLER} --strip-components=1 -C install/ && rm ${INSTALLER}

RUN mkdir -p /tools/Xilinx
COPY install_config.txt ./
RUN install/xsetup -a XilinxEULA,3rdPartyEULA -b Install -c install_config.txt
RUN rm -rf install/
