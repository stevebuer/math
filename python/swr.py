#!/usr/bin/python3
#
# calculate SWR from complex impedance
# Steve Buer, N7MKO -- 06.2024
#

debug = False

z_o = 50+0j
z_l = 25+15j

gamma = (z_l - z_o) / (z_l + z_o)

vswr = (1 + abs(gamma)) / (1 - abs(gamma))

if debug:
    print("gamma:", gamma)
    print("abs(gamma):", abs(gamma))

print("Load:", z_l)
print("Z0:", z_o)
print("VSWR:", round(vswr,1))
