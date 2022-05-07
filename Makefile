PART_NUM := xc7s15csga225-1
NUM_CPU := 6

DOCKER_NAME := vivado
CONTAINER_NAME := vivado-run
CONTAINER_ROOT := /root
PROJ_NAME := project
TOP_MODULE := top
TOP_FILE := top.v
XDC_FILE := top.xdc
SCRIPTS := src/main/script
BUILD_DIR := build/
RUN_CMD := docker exec -it $(CONTAINER_NAME) bash -i -c

.PHONY: build synth flash setup

build:
	echo "WIP!"

synth: build
	docker run --rm -dit --net=host --name $(CONTAINER_NAME) $(DOCKER_NAME)
	docker cp $(BUILD_DIR)/$(TOP_FILE) $(CONTAINER_NAME):$(CONTAINER_ROOT)
	docker cp $(SCRIPTS)/$(XDC_FILE) $(CONTAINER_NAME):$(CONTAINER_ROOT)
	docker cp $(SCRIPTS)/make-project.tcl $(CONTAINER_NAME):$(CONTAINER_ROOT)
	docker cp $(SCRIPTS)/build-project.tcl $(CONTAINER_NAME):$(CONTAINER_ROOT)
	$(RUN_CMD) 'vivado -mode batch -source make-project.tcl -tclargs $(PART_NUM) $(PROJ_NAME) $(TOP_MODULE) $(TOP_FILE) $(XDC_FILE)'
	$(RUN_CMD) 'vivado -mode batch -source build-project.tcl -tclargs $(PROJ_NAME)/$(PROJ_NAME).xpr $(NUM_CPU)'
	docker cp $(CONTAINER_NAME):$(CONTAINER_ROOT)/$(PROJ_NAME) $(BUILD_DIR)
	docker container kill $(CONTAINER_NAME)

flash:
	echo "WIP!"

setup:
	docker build --squash -t $(DOCKER_NAME) .

# TODO sbt installation
# TODO chisel project
# TODO generate verilog
# TODO test flashing bitstream to ram (openocd or usb pass somehow)
# TODO test flashing bitstream to flash
