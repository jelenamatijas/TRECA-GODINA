section .data
    poruka db "Unesi tekst: ", 0
    poruka_len equ $ - poruka
    novi_red db 0xA
    args_greska db "Nisu unesena dva argumenta: ./read <putanja_do_fajla>", 10
    args_greska_len equ $ - args_greska
    read_buffer db 1024 dup(0) ; == unsigned char read_buffer[1024] = {0};
    open_greska db "Greska pri otvaranju fajla.", 10
    open_greska_len equ $ - open_greska

section .bss
    buffer resb 256 ; Rezervisano 256 bajtova za unos
    duzina_unosa resq 1 ; Duzina procitanog fajla
    bytes_read resq 1 ; broj procitanih bajtova

section .text
    global _start

_start:
    ;---- Citanje iz fajla ----

    mov rax, [rsp] ; skida argumente konzole sa steka
    cmp rax, 2  ; poredi da li postoje 2 argumenta
    je open_file ; ako postoje 2 argumenta prelazi na otvaranje fajla

    mov rax, 1 ; sistemski poziv za write
    mov rdi, 1 ; podrazumijevani output je konzola (1)
    mov rsi, args_greska
    mov rdx, args_greska_len
    syscall

read:
    ;---- Citanje sa konzole ----
    mov rax, 1
    mov rdi, 1
    mov rsi, poruka
    mov rdx, poruka_len
    syscall

    mov rax, 0
    mov rdi, 0
    mov rsi, buffer
    mov rdx, 256
    syscall

    mov [duzina_unosa], rax ; Cuvanje duzine unosa

    mov rax, 1
    mov rdi, 1
    mov rsi, buffer
    mov rdx, [duzina_unosa]
    syscall

    mov rax, 1
    mov rdi, 1
    mov rsi, novi_red
    mov rdx, 1
    syscall

    jmp exit

open_file:
    mov rbx, [rsp+16]    ; argv[1] - adresa stringa putanje
    mov rax, 2           ; syscall: open (stari broj 2)
    mov rdi, rbx         ; filename
    mov rsi, 0           ; O_RDONLY
    mov rdx, 0
    syscall

    cmp rax, 0
    js  open_failed      ; ako je negativan -> greška (jump if sign)

    ; uspješno otvoren fajl -> rax = fd
    mov rdi, rax         ; fajl-deskriptor za read

    mov rax, 0           ; syscall: read
    mov rsi, read_buffer
    mov rdx, 1024
    syscall

    mov [bytes_read], rax
    mov rcx, rax

    mov rax, 1           ; write stdout
    mov rdi, 1
    mov rsi, read_buffer
    mov rdx, rcx
    syscall

    jmp read

open_failed:
    ; ispiši poruku o grešci
    mov rax, 1
    mov rdi, 1
    mov rsi, open_greska    
    mov rdx, open_greska_len
    syscall


exit:
    mov rax, 60
    mov rdi, 0
    syscall
