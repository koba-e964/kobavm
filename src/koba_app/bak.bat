robocopy ./compiler d:\backup\favorites\koba\koba_app\compiler  *.java *.class
cd compiler
jar cf  ../Compiler.zip *.java *.class
cd ..
