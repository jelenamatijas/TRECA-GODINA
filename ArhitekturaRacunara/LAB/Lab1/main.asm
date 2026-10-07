section .text
global _start

_start:
    mov rax, 60
    add rax, 40
    mov rbx, rax
    sub rbx, rax
    mov rax, 60
    mov rdi, 0
    syscall
