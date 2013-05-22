MAKE=make.exe

default:
	$(MAKE) build

build:
	cd ../../ & javac.exe koba_app/asm_x86/*.java & cd koba_app/asm_x86
run: build
	cd ../../ & java.exe koba_app.asm_x86.Tokenizer0 & cd koba_app/asm_x86
clean:
	del *.class
