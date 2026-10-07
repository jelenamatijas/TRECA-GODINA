read : read.o
	ld -o read read.o
read.o : read.asm
	nasm -f elf64 -g -F dwarf read.asm