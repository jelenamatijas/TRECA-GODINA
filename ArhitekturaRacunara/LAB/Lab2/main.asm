section .data
    a dq 10
    b dq 3
    format db "Vrijednost: %d",10,0
    op1 db "------ Aritmeticke operacije ------", 10, 0
    op2 db "------ Logicke operacije ------", 10, 0
    op3 db "------ Rad sa stekom ------", 10, 0
    op4 db "------ Rad sa flagovima ------", 10, 0
    op5 db "------ Uslovni skokovi ------", 10, 0
    op6 db "------ Rad sa procedurama -------", 10, 0
    c dq 0b0001
    d dq 0b0011

section .text
    global main
    extern printf

main:
    mov rdi, op1
    mov rax, 0
    call printf

    ; ADD
    mov rax, [a]
    add rax, [b]
    mov rdi, format
    mov rsi, rax
    mov rax, 0
    call printf

    ; SUB
    mov rax, [a]
    sub rax, 2
    mov rdi, format
    mov rsi, rax
    mov rax, 0
    call printf

    ; MUL
    mov rbx, [b]
    mul rbx
    mov rdi, format
    mov rsi, rax
    mov rax, 0
    call printf

    ; DIV
    cqo
    mov rcx, [b]
    div rcx
    mov rdi, format
    mov rsi, rax
    mov rax, 0
    call printf

    mov rdi, op2
    mov rax, 0
    call printf

    ; AND
    mov rax, [c]
    and rax, [b]
    mov rdi, format
    mov rsi, rax
    mov rax, 0
    call printf

    ; OR
    mov rax, [c]
    or rax, [b]
    mov rdi, format
    mov rsi, rax
    mov rax, 0
    call printf

    ; XOR
    mov rax, [c]
    xor rax, [b]
    mov rdi, format
    mov rsi, rax
    mov rax, 0
    call printf

    ; NOT
    mov rax, 5
    not rax
    mov rdi, format
    mov rsi, rax
    mov rax, 0
    call printf

    mov rdi, op3
    mov rax, 0
    call printf

    ; STEK
    mov rax, 10       
    mov rbx, 20       
    mov rcx, 30       

    ; Push
    push rax          
    push rbx          
    push rcx         

    ; Pop
    pop rdx           
    mov rdi, format
    mov rsi, rdx
    mov rax, 0
    call printf       

    pop rdx           
    mov rdi, format
    mov rsi, rdx
    mov rax, 0
    call printf       

    pop rdx           
    mov rdi, format
    mov rsi, rdx
    mov rax, 0
    call printf   

    ; FLAGS
        ; -> ZF (Zero Flag) - postavlja se ako je rezultat 0
        ; -> SF (Sign Flag) - postavlja se ako je rezultat negativan
        ; -> OF (Overflow Flag) - postavlja se ako je došlo do overflow-a kod signed operacija
        ; -> CF (Carry Flag) - postavlja se ako je došlo do carry/borrow kod unsigned operacija
    ; --- ADD koji mijenja ZF i CF ---
    mov rdi, op4
    mov rax, 0
    call printf

    mov rax, 5
    add rax, 6      ; rax = 10, ZF=0, SF=0, OF=0
    mov rdi, format
    mov rsi, rax
    mov rax, 0
    call printf      ; Ispis: 10

    ; --- SUB koji mijenja ZF ---
    sub rax, 10      ; rax = 0, ZF=1 (rezultat je 0)
    mov rdi, format
    mov rsi, rax
    mov rax, 0
    call printf      ; Ispis: 0    

    mov rdi, op5
    mov rax, 0
    call printf

    ; Skokovi
    mov rax, 5
    mov rbx, 6

    cmp rax, rbx
    je equal_label

    mov rdi, format
    mov rsi, 0
    mov rax, 0
    call printf
    jmp end_label

    equal_label:
        mov rdi, format
        mov rsi, 1
        mov rax, 0
        call printf
    
    end_label:
        mov rcx, 10

        ; Petlja -> izvrsava se dok god je rcx vece od 0
        loop_start:
            push rcx
            mov rdi, format
            mov rsi, rcx
            mov rax, 0
            call printf
            pop rcx

            dec rcx
            jnz loop_start

    
    ; Pozivi procedura

    mov rdi, op6
    mov rax, 0
    call printf

    mov rdi, 5          
    mov rsi, 7          
    call add_numbers    

    mov rdi, format
    mov rsi, rax        
    mov rax, 0
    call printf

    mov rax, 60 
    xor rdi, rdi 
    syscall


; --------------------------------------------------------------------------------

add_numbers:
    push rdi            
    push rsi            

    pop rax             
    pop rbx             

    add rax, rbx        

    ret    