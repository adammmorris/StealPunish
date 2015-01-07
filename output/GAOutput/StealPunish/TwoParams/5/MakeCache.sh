#!/usr/local/bin/bash
#$ -cwd
#PBS -e /dev/null
#PBS -o /dev/null

/gpfs/main/home/amm4/java/jdk1.8.0_05/bin/java -jar SP.jar "/gpfs/main/home/amm4/git/generalResearch/output/GAOutput/StealPunish/TwoParams/SmallGenomeSpace/5/BT0" $SGE_TASK_ID .1 0 100
