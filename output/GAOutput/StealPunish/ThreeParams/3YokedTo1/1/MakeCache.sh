#!/usr/local/bin/bash
#$ -cwd
#PBS -e /dev/null
#PBS -o /dev/null

/gpfs/main/home/amm4/java/jdk1.8.0_05/bin/java -jar SP_FSS_10to10.jar "/gpfs/main/home/amm4/git/generalResearch/output/GAOutput/StealPunish/ThreeParams/3YokedTo1/BT50" $SGE_TASK_ID .1 .5 100
