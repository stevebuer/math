#!/usr/bin/python3
#
# Convert frequency table to list of raw data
#
# Steve Buer, 2024
#

import csv

debug = False

with open('frequency.csv') as csvfile:
    reader = csv.reader(csvfile)
    for row in reader:
        if debug:
            print("type:", row[0], "count: ", row[1])
        label = row[0]
        count = int(row[1])
        while count != 0:
            print(label)
            count -= 1
