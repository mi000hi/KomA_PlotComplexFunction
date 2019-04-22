# KomA_PlotComplexFunction
plots complex functions

a more detailed readme will follow.

this program is just to get a image on how complex functions move things around in the complex numberarea.

you define a area, most likely around (0+0i), from which we will take and calculate some points with a given complex function. the resulting
location of f(z) will be painted on the big Leinwand2D canvas on the right side, on the left side we plot f(x+iy) = (x, y, something), with
something determining Re(...), Im(...), Radius(...) and Arg(...).

in the settings you are able to change how the function is painted or calculated and you can define some functions in the input
numberarea that you would like to see transformed on the Leinwand2D canvas.

'''INSTALLATION
it should run on windows and maxOS, also the layout will definitely not be perfect because im using i3

install java (https://wiki.archlinux.org/index.php/java): sudo pacman -S jdk-openjdk
install git (https://wiki.archlinux.org/index.php/Git): sudo pacman -S git

change to desired program location, this will create a folder called "KomA_PlotComplexFunction"
git clone https://github.com/mi000hi/KomA_PlotComplexFunction.git

compile the program
  run the ./compile or ./compileWindows.bat file
  or use javac -g -d ./bin/ ./src/*.java
  
run the program
  run the ./run or ./runWindows file
  or use java -cp ./bin/ Gui

HAVE FUN and lets improve :)
