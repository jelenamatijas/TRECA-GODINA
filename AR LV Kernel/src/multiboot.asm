; Fajl multiboot.asm sadrži kod za podešavanje Multiboot2 zaglavlja, kojim se omogućava da GRUB bootloader prepozna kernel i započne njegovo izvršavanje.
; Reference: https://www.gnu.org/software/grub/manual/multiboot2/multiboot.html

bits 32

section .data

; The field 'magic' is the magic number identifying the header, which must be the hexadecimal value 0xE85250D6. 
MAGIC equ 0xE85250D6 ; multiboot2 magic constant

; The field 'header_length' specifies the Length of Multiboot2 header in bytes including magic fields. 
HEADER_LENGTH equ header_end - header_start

section .multiboot_header
header_start:
    dd MAGIC
    dd 0 ; protected mode i386
    dd HEADER_LENGTH
    dd 0x100000000 - (MAGIC + HEADER_LENGTH)

    ; end tag
    ;
    ; ‘type’ is divided into 2 parts. Lower contains an identifier of contents of the rest of the tag. ‘size’ contains the size of tag including header fields. If bit ‘0’ of ‘flags’ (also known as ‘optional’) is set, the bootloader may ignore this tag if it lacks relevant support. Tags are terminated by a tag of type ‘0’ and size ‘8’. 
    dw 0    ; type
    dw 0    ; flags
    dd 8    ; size
header_end:
