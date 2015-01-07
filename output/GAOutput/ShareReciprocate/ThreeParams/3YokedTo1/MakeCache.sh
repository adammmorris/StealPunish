#!/usr/local/bin/bash
#$ -cwd
#PBS -e /dev/null
#PBS -o /dev/null

/gpfs/main/home/amm4/java/jdk1.8.0_05/bin/java -jar SR_FSS_10to10.jar "/gpfs/main/home/amm4/git/generalResearch/output/GAOutput/ShareReciprocate/ThreeParams/3YokedTo1/BT25" $SGE_TASK_ID .1 .25 100
