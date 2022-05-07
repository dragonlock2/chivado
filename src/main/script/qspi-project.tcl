if {$argc != 2} {
    puts "Expected: <part num> <num cpu>"
    exit
}

set part_num [lindex $argv 0]
set num_cpu  [lindex $argv 1]

create_project qspi qspi -part $part_num
update_ip_catalog

add_files -fileset constrs_1 -norecurse qspi.xdc
add_files -norecurse qspi.v

import_files -force -norecurse
import_files -fileset constrs_1 -force -norecurse qspi.xdc

set_property top qspi [current_fileset]
set_property top_file qspi.v [current_fileset]
update_compile_order -fileset sources_1

launch_runs synth_1 -jobs $num_cpu
wait_on_run synth_1
launch_runs impl_1 -to_step write_bitstream -jobs $num_cpu
wait_on_run impl_1
