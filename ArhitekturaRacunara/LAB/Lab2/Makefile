main : main.o
	gcc main.o -no-pie -o main
main.o : main.asm
	nasm -f elf64 -g -F dwarf main.asm