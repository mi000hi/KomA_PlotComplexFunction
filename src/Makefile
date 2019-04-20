
JFLAGS = -g -d ./bin/
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) ./src/$*.java

CLASSES = \
        Gui.java \
        Complex.java \
        Leinwand2D.java \
     	ParentLeinwand3D.java \
	Leinwand3D.java \
	Point3D.java \
	SettingsGui2.java	

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
