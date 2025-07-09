#!/usr/bin/python3

#
# Test case calculations for L-Network simulator
#
# Steve Buer, N7MKO
# Olympic College CS& 141
#

z_ref = 50+0j

print("Test calculations")

# Gamma (reflection coefficient)

def gamma(z_load):

	gamma = (z_load - z_ref) / (z_load + z_ref)
	
	print("gamma:", gamma)

	return gamma

# SWR from gamma

def swr(gamma):

	swr = (1 + abs(gamma)) / (1 - abs(gamma))
	
	print("swr:", round(swr, 2))

# Tests

test_list = [100+0j, 25+0j, 25-10j,60+10j, 75+15j]

for z_l in test_list:

	swr(gamma(z_l))
