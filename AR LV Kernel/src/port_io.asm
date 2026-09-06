bits 64

global outb
global outw
global outd
global inb
global inw
global ind

section .text
align 4096

outb:
    ; rdi - port
    ; rsi - value
    mov dx, di
    mov al, sil
    out dx, al
    ret
    
outw:
    ; rdi - port
    ; rsi - value
    mov dx, di
    mov ax, si
    out dx, ax
    ret
    
outd:
    ; rdi - port
    ; rsi - value
    mov dx, di
    mov eax, esi
    out dx, eax
    ret

inb:
    ; rdi - port
    mov dx, di
    in al, dx
    ret

inw:
    ; rdi - port
    mov dx, di
    in ax, dx
    ret
    
ind:
    ; rdi - port
    mov dx, di
    in eax, dx
    ret
