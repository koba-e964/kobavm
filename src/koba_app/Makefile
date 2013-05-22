default:
	make.exe debugger
debugger:
	cd .. ; javac.exe koba_app/compiler/Debugger.java;cd koba_app
debug_run: debugger
	cd ..; java koba_app.compiler.Debugger;cd koba_app
clean:
	-rm compiler/*.class
