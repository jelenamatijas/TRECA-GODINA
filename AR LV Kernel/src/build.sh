#!/bin/bash
# Script to build the x64 kernel
# Dependencies: nasm qemu-system-x86 grub-common grub-pc-bin xorriso mtools

set -e # exit on error

# Auto-detect cross-compiler location
if [ -d "/opt/cross/bin" ]; then
    export PATH="/opt/cross/bin:$PATH"
    echo "Using cross-compiler from: /opt/cross"
elif [ -d "../cross_compiler/bin" ]; then
    export PATH="$(realpath ../cross_compiler)/bin:$PATH"
    echo "Using cross-compiler from: $(realpath ../cross_compiler)"
else
    echo "Warning: Cross-compiler not found in /opt/cross or ../cross_compiler"
    echo "Assuming x86_64-elf-gcc is in PATH"
fi

# Create build directory if it doesn't exist
BUILD_DIR="../build"
mkdir -p "$BUILD_DIR"

nasm -felf64 -o "$BUILD_DIR/multiboot.o" multiboot.asm
nasm -felf64 -o "$BUILD_DIR/boot.o" boot.asm
nasm -felf64 -o "$BUILD_DIR/paging.o" paging.asm
nasm -felf64 -o "$BUILD_DIR/checks.o" checks.asm
nasm -felf64 -o "$BUILD_DIR/gdt.o" gdt.asm
nasm -felf64 -o "$BUILD_DIR/idt.o" idt.asm
nasm -felf64 -o "$BUILD_DIR/long_mode_init.o" long_mode_init.asm
nasm -felf64 -o "$BUILD_DIR/port_io.o" port_io.asm

# -- Freestanding okruženje --

# Pri kompajliranju, -ffreestanding argument naznačava da će se kompajlirani programi izvršavati u tzv. freestanding okruženju, to jeste:

#     Naznačava da se ne koristi standardna biblioteka, te da korištene funkcije sa nazivima standardnih funkcija ne moraju zapravo odgovarati funkcijama iz C standardne biblioteke
#     Naznačava da ulazna tačka programa ne mora biti main. U linkerskoj skripti je tada moguće odrediti da neka druga procedura bude ulazna tačka.
#     Ovim argumentom se sprječavaju neke kompajlerske optimizacije.
#     Navođenje ovog argumenta čini dostupnim samo neka standardna zaglavlja koja sadrže tipove i konstante koje su odgovarajuće za ciljanu arhitekturu (konstante za minimalne i maksimalne vrijednosti i tipove određenih dužina). Primjer takvih zaglavlja su: <float.h>, <limits.h>, <stdarg.h>, <stddef.h>, <stdbool.h> i <stdint.h>.

# Međutim, potrebno je da freestanding okruženje učini dostupnim neke implementacije za standardne rutine memcpy, memmove, memset i memcmp. Ovo je potrebno jer je, čak i pored navođenja opcije -freestanding, moguće da će GCC generisati kod koji emituje pozive ka ovim rutinama.

# Za izvor ovih informacija i dodatne informacije, vidjeti stranicu o standardima jezika koji su podržani od strane GCC kompajlera: https://gcc.gnu.org/onlinedocs/gcc-12.2.0/gcc/Standards.html#Standards.

x86_64-elf-gcc -ffreestanding -mcmodel=large -mno-red-zone -mno-mmx -c kernel.c -o "$BUILD_DIR/kernel.o"
x86_64-elf-gcc -ffreestanding -mcmodel=large -mno-red-zone -mno-mmx -c string.c -o "$BUILD_DIR/string.o"
x86_64-elf-gcc -ffreestanding -mcmodel=large -mno-red-zone -mno-mmx -c vga.c -o "$BUILD_DIR/vga.o"
x86_64-elf-gcc -ffreestanding -mcmodel=large -mno-red-zone -mno-mmx -c keyboard.c -o "$BUILD_DIR/keyboard.o"

# -- Argumenti za linker --

# -nostdlib daje informaciju linkeru da se neće koristiti standardne biblioteke u fazi linkovanja. Ova opcija isključuje, između ostaloga, linkovanje sa određenom bibliotekom koja sadrži interne podrutine koje GCC koristi, kako bi se izbjegli problemi pri radu sa određenim platformama. Moguće je da će GCC i dalje, u kompajliranom kodu, emitovati pozive ka nekim rutinama iz te biblioteke, pa je potrebno posebno naznačiti da je potrebno ipak potrebno da se ta biblioteka uključi, sa -lgcc argumentom.

#     Sa argumentom -n određuje se da neće biti moguće upisivati sadržaj u text segment koji je definisan u linkerskoj skripti, tj. da je tekst segment read-only.
#     Opcija -T specificira koja linkerska skripta treba da se koristi, što je u slučaju priloženog primjera fajl linker.ld.

x86_64-elf-gcc -ffreestanding -O2 -nostdlib -lgcc -n -T linker.ld -o "$BUILD_DIR/kernel.bin" "$BUILD_DIR/multiboot.o" "$BUILD_DIR/checks.o" "$BUILD_DIR/boot.o" "$BUILD_DIR/paging.o" "$BUILD_DIR/gdt.o" "$BUILD_DIR/long_mode_init.o" "$BUILD_DIR/kernel.o" "$BUILD_DIR/idt.o" "$BUILD_DIR/port_io.o" "$BUILD_DIR/string.o" "$BUILD_DIR/vga.o" "$BUILD_DIR/keyboard.o"

# Create ISO structure in build directory
mkdir -p "$BUILD_DIR/isofiles/boot/grub"
cp "$BUILD_DIR/kernel.bin" "$BUILD_DIR/isofiles/boot/kernel.bin"
cp isofiles/boot/grub/grub.cfg "$BUILD_DIR/isofiles/boot/grub/grub.cfg"

grub-mkrescue -o "$BUILD_DIR/myos.iso" "$BUILD_DIR/isofiles"

qemu-system-x86_64 -cdrom "$BUILD_DIR/myos.iso"

echo "done"
