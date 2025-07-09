#
# Steve Buer
# Olympic College - Winter 2025
# CS& 141 - Java I Final Project
#

all: Tuner.class

Tuner.class: Tuner.java LNetwork.java

	javac Tuner.java

run:

	java Tuner

doc:

	javadoc -d javadoc LNetwork.java

jar:

	jar -cmvf manifest.mf Tuner.jar Tuner.class LNetwork.class Complex.class

clean:

	rm -f *.class Tuner.jar
