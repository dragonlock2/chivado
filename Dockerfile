FROM ubuntu:jammy AS install

ARG INSTALLER="Xilinx_Unified_2022.1_0420_0327.tar.gz"

RUN apt update && apt upgrade -y

WORKDIR /root/

RUN mkdir install/
COPY ${INSTALLER} ./
RUN tar -xzf ${INSTALLER} --strip-components=1 -C install/
RUN rm ${INSTALLER}

RUN mkdir -p /tools/Xilinx
COPY install_config.txt ./
RUN install/xsetup -a XilinxEULA,3rdPartyEULA -b Install -c install_config.txt
RUN rm -rf install/

# delete history to keep size small
FROM ubuntu:jammy
RUN mkdir -p /tools/Xilinx
COPY --from=install /tools/Xilinx /tools/Xilinx
