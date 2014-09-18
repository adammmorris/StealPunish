#!/usr/local/bin/bash
#$ -cwd
#PBS -e /dev/null
#PBS -o /dev/null

/gpfs/main/home/amm4/java/jdk1.8.0_05/bin/java -jar SR_FSS.jar "/gpfs/main/home/amm4/git/generalResearch/output/GAOutput/ShareReciprocate/ShareReciprocate3_3" .1 $SGE_TASK_ID .25
