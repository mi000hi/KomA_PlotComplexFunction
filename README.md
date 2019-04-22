# KomA_PlotComplexFunction
plots complex functions

a more detailed readme will follow.

this program is just to get a image on how complex functions move things around in the complex numberarea.

you define a area, most likely around (0+0i), from which we will take and calculate some points with a given complex function. the resulting
location of f(z) will be painted on the big Leinwand2D canvas on the right side, on the left side we plot f(x+iy) = (x, y, something), withjava -cp ./bin/ Gui
something determining Re(...), Im(...), Radius(...) and Arg(...).

in the settings you are able to change how the function is painted or calculated and you can define some functions in the input
numberarea that you would like to see transformed on the Leinwand2D canvas.

In the function input field you can use `sin(), cos()` and `exp()`, `z` as the complex input number, `doubles` and `+, -, *, /`
In the input function field for the function to track the transformation, you can use the above, with `x` as function input value,
the options mentioned above and `sqrt()`. The color input format in the field next to the previous used field is `red, green, blue`

Using doubles in some places can cause errors.

# INSTALLATION
it should run on windows and maxOS, also the layout will definitely not be perfect because im using i3

install java (https://wiki.archlinux.org/index.php/java): `sudo pacman -S jdk-openjdk`  
install git (optional) (https://wiki.archlinux.org/index.php/Git): `sudo pacman -S git`

change to desired program location, this will create a folder called "KomA_PlotComplexFunction"  
`git clone https://github.com/mi000hi/KomA_PlotComplexFunction.git`

compile the program  
  run the `./compile` or `./compileWindows.bat` file  
  or use `javac -g -d ./bin/ ./src/*.java`
  
run the program  
  run the `./run` or `./runWindows.bat` file  
  or use `java -cp ./bin/ Gui`

# HAVE FUN and lets improve :)
