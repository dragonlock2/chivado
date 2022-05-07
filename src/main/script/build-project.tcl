if {$argc != 2} {
    puts "Expected: <project name> <num cpu>"
    exit
}

set num_cpu [lindex $argv 1]

open_project [lindex $argv 0]
launch_runs synth_1 -jobs $num_cpu
wait_on_run synth_1
launch_runs impl_1 -to_step write_bitstream -jobs $num_cpu
wait_on_run impl_1
