; Long mode initialization - Rutina za ulazak u 64-bitni long režim rada
; Izvršava se nakon što je straničenje aktivirano i GDT učitan
; U rutini za ulazak u long režim rada inicijalizuju se segmentni registri, 
; učitava se i osposobljava IDT tabela, omogućavaju se interapti, 
; omogućava se rad sa SSE registrima i skače se na kernel_main C funkciju.

bits 64  ; Sada smo u 64-bitnom modu

global long_mode_entry

extern kernel_main
extern load_idt

section .text
align 4096

; Rutina za ulazak u long režim rada
long_mode_entry:
    cli  ; Onemogući interapte
    
    ; Inicijalizuj segmentne registre (osim code registra)
    xor ax, ax
    mov ds, ax
    mov es, ax
    mov fs, ax
    mov gs, ax
    mov ss, ax
    
    ; Učitaj i osposobi IDT tabelu
    call load_idt
    
    ; Omogući rad sa SSE registrima
    mov rax, cr0
    and ax, 0xFFFB		;clear coprocessor emulation CR0.EM
    or ax, 0x2			;set coprocessor monitoring  CR0.MP
    mov cr0, rax
    mov rax, cr4
    or ax, 3 << 9		;set CR4.OSFXSR and CR4.OSXMMEXCPT at the same time
    mov cr4, rax

    ; Omogući interapte
    sti 
    
    ; Skoči na kernel_main C funkciju - glavna kernel petlja
    call kernel_main
    
    ; print HALT
    mov rax, 0x2f542f4C2f412f48 
    mov qword [0xb80A0], rax

; clear interrupts and hang computer indefinitely
    cli
.hang:
    hlt
    jmp .hang
