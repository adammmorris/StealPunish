#!/usr/local/bin/bash
#$ -cwd
#PBS -e /dev/null
#PBS -o /dev/null

# Set output file
output_file="/gpfs/main/home/amm4/git/generalResearch/output/GAOutput/StealPunish/ThreeParams/3YokedTo1/10/BT0"

# Set run parameters
row=$SGE_TASK_ID
lr=.1
probBackTurned=0

# Set payoff matrix
rwd_init=1.0
rwd_initee=-1.0
rwd_respond=-.5
rwd_respondee=-2.5

# Set params
params_min=-10
params_max=10
params_step=10
nParams=3

/gpfs/main/home/amm4/java/jdk1.8.0_05/bin/java -jar FSS.jar $output_file $row $lr $probBackTurned $rwd_init $rwd_initee $rwd_respond $rwd_respondee $params_min $params_max $params_step $nParams
