; U boot.asm fajlu naznačeno je da asemblirani kod treba biti 32-bitni mašinski kod (bits 32), što je potrebno jer kernel, 
; pri početku izvršavanja, neće biti u 64-bitnom long režimu rada. 
; U proceduri start, koja treba biti ulazna tačka kernela, podešava se vrh steka na neku rezervisanu memorijsku lokaciju. 
; Zatim se izvršavaju odgovarajuće provjere mogućnosti prelaska u long režim rada, te se aktivira mehanizam straničenja 
; (što je preduslov za prelazak u long režim rada). 
; Nakon toga se učitava GDT tabela i vrši se daleki skok na odgovarajući rutinu za ulazak u long režim rada.

bits 32

global start
extern run_checks
extern long_mode_entry
extern enable_paging
extern gdt64.code
extern gdt64.pointer

section .text 
align 4096
start:
    cli
    mov esp, stack_top
    call run_checks
    call enable_paging
    lgdt [gdt64.pointer]
    jmp gdt64.code:long_mode_entry

section .bss
align 4096
stack_bottom:
    resb 16*1024
stack_top:

