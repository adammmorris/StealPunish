#!/usr/local/bin/bash
#$ -cwd
#PBS -e /dev/null
#PBS -o /dev/null

/gpfs/main/home/amm4/java/jdk1.8.0_05/bin/java -jar bimatrix_Chicken.jar "/gpfs/main/home/amm4/git/generalResearch/RSBOutput/Chicken5" $SGE_TASK_ID
