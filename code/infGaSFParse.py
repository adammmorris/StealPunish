#!/usr/bin/python

import sys

percentKept = 0.02

path_GA = "/gpfs/main/home/amm4/git/generalResearch/output/GAOutput/StealPunish/TwoParams/6/BT0/GA.txt";
path_CSV = "/gpfs/main/home/amm4/git/generalResearch/output/GAOutput/StealPunish/TwoParams/6/BT0/GA_parsed.csv";

def main():
	writeCSVForCommon()
	


def parseGenerations(fpath):
	f = open(fpath, 'r')
	gens = []
	dists = []
	
	#scan until first ga gen hit
	l = f.readline().strip()
	while not l.startswith('*GAGEN'):
		l = f.readline().strip()
	
	g = parseGeneation(f)
	c = 0
	while len(g) > 0:
		gens.append(g)
		
		dist = {}
		for o in g:
			key = paramSRep(o[2])
			dist[key] = o[0]
			
		dists.append(dist)
		if c % 100 == 0:
			print 'parsed gen', c
			
		g = parseGeneation(f)
		c += 1
	f.close()
	
	return gens, dists
	
def parseGeneation(f):
	
	generation = []
	
	while True:
		l1 = f.readline()
		#print 'scanning: #' + l1 + '#'
		if len(l1) == 0:
			break #end of file
		l1 = l1.strip()
		if len(l1) == 0:
			continue #skip blank lines
		if l1.startswith('*GAGEN'):
			break #hit next generation
			
		#if we get here then we are at start of an orgnaism
		fit = float(l1)
		l2 = f.readline().strip()
		ratio = float(l2)
		l3 = f.readline().strip()
		params = [float(e) for e in l3.split(',')]
		
		#clear ++++ buffer
		f.readline()
		
		#add organism
		generation.append((ratio, fit, params))
		
	
	return generation
	
	
	
def paramSRep(params):
	return ' '.join([str(p) for p in params])
	
	
	
	

	

def getCommonGenomes(dists, threshold):
	common = set([])
	c = 0
	for dist in dists:
		for p in dist.items():
			if p[1] > threshold:
				common.add(p[0])
		if c % 100 == 0:
			print 'searched for common in gen', c
		c += 1
	return common



def writeCSVForCommon():
	gens, dists = parseGenerations(path_GA)
	common = getCommonGenomes(dists, percentKept)
	
	
	uncommonLabel = 'other'
	

	colnames = [c for c in common]
	colnames.append(uncommonLabel)
	colnames.sort()

	csvFile = open(path_CSV, 'w')
	
	#write header
	csvFile.write(','.join(colnames) + '\n')
	
	#write content
	c = 0
	for dist in dists:
		cdist = commonDist(dist, common, uncommonLabel)
		csvFile.write(','.join([str(cdist.get(n, 0.)) for n in colnames]) + '\n')
		
		if c % 100 == 0:
			print 'wrote to csv file gen dist', c
		c += 1
	
	csvFile.close()
	
	

def commonDist(dist, common, uncommonLabel):
	cdist = {}
	cdist[uncommonLabel] = 0.
	for p in dist.items():
		if p[0] in common:
			cdist[p[0]] = p[1]
		else:
			cdist[uncommonLabel] += p[1]
			
	return cdist




if __name__ == '__main__':
	main()

