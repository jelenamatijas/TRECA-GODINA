section .data
    output db "Ovo je ispis na konzolu.", 10, 0
    len equ $ - output ; equ definisanje konstante
                       ; $ vrijednost trenutne adrese
    poruka db "Ovo je poruka za upis u fajl.", 10, 0
    duzina_poruke equ $ - poruka

    greska_arg db 'Upotreba: ./program <putanja_do_fajla>', 0xA
    duzina_greska_arg equ $ - greska_arg
    
    greska_otvaranje db 'Greska pri otvaranju fajla!', 0xA
    duzina_greska_otvaranje equ $ - greska_otvaranje
    
    greska_upis db 'Greska pri upisu u fajl!', 0xA
    duzina_greska_upis equ $ - greska_upis
    
    uspjeh db 'Podaci uspjesno upisani u fajl.', 0xA
    duzina_uspjeh equ $ - uspjeh

section .bss
    fd resq 1 ; File descriptor - 64bit

section .text
    global _start

_start:
    ; Ispis output poruke na konzolu
    mov rax, 1 ; Sistemski poziv -> sys_write
    mov rdi, 1 ; File descriptor -> stdout
    mov rsi, output ; Pokazivac na string koji se ispisuje
    mov rdx, len ; Duzina stringa
    syscall ; Poziv kernela

    ;-------------------------------------------------------------------

    ; Upis u fajl
    pop rcx ; provjeravanje koliko argumenata je uneseno
    cmp rcx, 2
    jl .greska_argumenti 

    pop rdi ; dobijanje prvog argumenta
    pop rdi ; dobijanje drugg argumenta -> putanja do fajla

    mov rax, 2 ; sys_open, otvaranje fajla za pisanje
    mov rsi, 0x241 ; O_CREAT -> 0x40, kreira fajl ako ne postoji, ako postoji otvorice se 
                   ;| O_WRONLY -> 0x1, otvara fajl samo za pisanje 
                   ;| O_TRUNC -> 0x200, brise fajl ako vec postoji
    mov rdx, 0644o
    syscall

    cmp rax, 0 ; Provjera da li je fajl uspjesno otvoren
    jl .greska_otvaranje
    mov [fd], rax ; Cuva fajl descriptor

    mov rax, 1 ; Upis podataka u fajl, sys_write
    mov rdi, [fd]
    mov rsi, poruka
    mov rdx, duzina_poruke
    syscall

    cmp rax, 0 ; Provjera da li je upis bio uspjesan
    jl .greska_upis

    mov rax, 3 ; Zatvaranje fajla
    mov rdi, [fd]
    syscall
    
    mov rax, 1 ; Ispis o uspjehu upisivanja u fajl
    mov rdi, 1 ; stdout
    mov rsi, uspjeh
    mov rdx, duzina_uspjeh
    syscall

    jmp .kraj ; Izlaz iz programa
    
.kraj:
    mov rax, 60
    mov rdi, 0
    syscall

.greska_argumenti:
    mov rax, 1
    mov rdi, 1
    mov rsi, greska_arg
    mov rdx, duzina_greska_arg
    syscall
    jmp .kraj

.greska_otvaranje:
    mov rax, 1
    mov rdi, 1
    mov rsi, greska_otvaranje
    mov rdx, duzina_greska_otvaranje
    syscall
    jmp .kraj

.greska_upis:
    mov rax, 1
    mov rdi, 1
    mov rsi, greska_upis
    mov rdx, duzina_greska_upis
    syscall
    jmp .kraj