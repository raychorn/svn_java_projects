JAVAC = /usr/users/tech_1/hessmann/misc/java/javab1/bin/javac -g
#JAVAC = /usr/local/hotjava/bin/javac
#JAVAC = /usr/local/hotjava/bin/javac -O
#JAVAC = javac 

JAVADOC = /usr/users/tech_1/hessmann/misc/java/javab1/bin/javadoc
#JAVADOC = /usr/local/hotjava/bin/javadoc
#JAVADOC = javadoc

.SUFFIXES:
.SUFFIXES: .java .class

%.class: %.java
	$(JAVAC) $*.java
	@chmod a+r *.class

OBJ = Floor.class Foot.class Step.class Sound.class Figur.class \
	Button.class MEXButtonList.class Dance.class


all: $(OBJ)

doc:
	$(JAVADOC) *.java
	chmod a+r *.html

