#!/usr/local/bin/bash
#$ -cwd
#PBS -e /dev/null
#PBS -o /dev/null

/gpfs/main/home/amm4/java/jdk1.8.0_05/bin/java -jar Chicken.jar "/gpfs/main/home/amm4/git/generalResearch/output/RSBOutput/Chicken/Chicken5" $SGE_TASK_ID
