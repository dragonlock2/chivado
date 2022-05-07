if {$argc != 5} {
    puts "Expected: <part num> <proj name> <top module> <top filename> <xdc filename>"
    exit
}

set part_num  [lindex $argv 0]
set proj_name [lindex $argv 1]
set top_mod   [lindex $argv 2]
set top_file  [lindex $argv 3]
set xdc_file  [lindex $argv 4]

create_project $proj_name $proj_name -part $part_num
update_ip_catalog

add_files -fileset constrs_1 -norecurse $xdc_file
add_files -norecurse $top_file

import_files -force -norecurse
import_files -fileset constrs_1 -force -norecurse $xdc_file

set_property top $top_mod [current_fileset]
set_property top_file $top_file [current_fileset]
update_compile_order -fileset sources_1
