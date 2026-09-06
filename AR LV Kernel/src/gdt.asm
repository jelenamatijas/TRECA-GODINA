bits 32

global gdt64.code
global gdt64.pointer

; GDT flags
GDT_FLAG_EXECUTABLE equ 1<<43 ; is an executable segment
GDT_FLAG_DESC_TYPE equ 1<<44 ; is 1 for data and code seg.
GDT_FLAG_PRESENT equ 1<<47 ; is present
GDT_FLAG_X86_64 equ 1<<53 ; is an x86-64 code segment

section .text
align 4096

section .rodata
align 4096
gdt64:
    dq 0 ; null entry
.code: equ $ - gdt64
    dq GDT_FLAG_EXECUTABLE | GDT_FLAG_DESC_TYPE | GDT_FLAG_PRESENT | GDT_FLAG_X86_64
.pointer:
    dw $ - gdt64 - 1
    dq gdt64
