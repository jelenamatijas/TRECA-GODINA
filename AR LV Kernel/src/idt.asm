bits 64

global load_idt

extern isr_handler
extern call_keyboard_handler

; MACRO
%macro ISR_NOERRCODE 1  ; define a macro, taking one parameter
  global isr%1        ; %1 accesses the first parameter.
  isr%1:
%if %1 == 9
    ; Keyboard interrupt handler (IRQ1)
    push rax
    push rdi
    
    ; Read scancode from keyboard port 0x60
    in al, 0x60
    movzx rdi, al
    
    ; Call C keyboard handler
    call call_keyboard_handler
    
    ; Send EOI to PIC
    mov al, 0x20
    out 0x20, al
    
    pop rdi
    pop rax
    iretq
%else
    jmp isr_common_stub
%endif
%endmacro

%macro ISR_ERRCODE 1
  global isr%1
  isr%1:
    jmp isr_common_stub_errcode
%endmacro

%macro ISR_PUT_ENTRY 1
    mov qword rax, isr%1
    mov qword r8, idt64 + 16*%1 ; r8 <- address of current entry
    mov qword rbx, rax
    and qword rbx, 0xFFFF
    mov word [r8 + 0], bx ; bits 0-15 of base address
    mov word [r8 + 2], 0x08 ; code segment in gdt
    mov byte [r8 + 4], 0 ; always zero
    mov byte [r8 + 5], 0x8E ; interrupt entry, ring 0, present
    mov qword rbx, rax    
    shr qword rbx, 16
    mov word [r8 + 6], bx ; bits 15-31 of base address
    shr qword rbx, 16
    mov dword [r8 + 8], ebx ; bits 32-63 of base address
    mov dword [r8 + 12], 0 ; should be set to 0 per spec
%endmacro

section .text
align 4096

load_idt:
    ISR_PUT_ENTRY 0
    ISR_PUT_ENTRY 1
    ISR_PUT_ENTRY 2
    ISR_PUT_ENTRY 3
    ISR_PUT_ENTRY 4
    ISR_PUT_ENTRY 5
    ISR_PUT_ENTRY 6
    ISR_PUT_ENTRY 7
    ISR_PUT_ENTRY 8
    ISR_PUT_ENTRY 9
    ISR_PUT_ENTRY 10
    ISR_PUT_ENTRY 11
    ISR_PUT_ENTRY 12
    ISR_PUT_ENTRY 13
    ISR_PUT_ENTRY 14
    ISR_PUT_ENTRY 15
    ISR_PUT_ENTRY 16
    ISR_PUT_ENTRY 17
    ISR_PUT_ENTRY 18
    ISR_PUT_ENTRY 19
    ISR_PUT_ENTRY 20
    ISR_PUT_ENTRY 21
    ISR_PUT_ENTRY 22
    ISR_PUT_ENTRY 23
    ISR_PUT_ENTRY 24
    ISR_PUT_ENTRY 25
    ISR_PUT_ENTRY 26
    ISR_PUT_ENTRY 27
    ISR_PUT_ENTRY 28
    ISR_PUT_ENTRY 29
    ISR_PUT_ENTRY 30
    ISR_PUT_ENTRY 31
    lidt [idt64.pointer]
    ret
 
isr_common_stub:
    iretq

isr_common_stub_errcode:
    add rsp, 8  ; pop error code from stack
    iretq

ISR_NOERRCODE 0
ISR_NOERRCODE 1
ISR_NOERRCODE 2
ISR_NOERRCODE 3
ISR_NOERRCODE 4
ISR_NOERRCODE 5
ISR_NOERRCODE 6
ISR_NOERRCODE 7
ISR_ERRCODE   8
ISR_NOERRCODE 9 ; Keyboard interrupt (IRQ1 at default mapping)
ISR_ERRCODE   10
ISR_ERRCODE   11
ISR_ERRCODE   12
ISR_ERRCODE   13
ISR_ERRCODE   14
ISR_NOERRCODE 15
ISR_NOERRCODE 16
ISR_NOERRCODE 17
ISR_NOERRCODE 18
ISR_NOERRCODE 19
ISR_NOERRCODE 20
ISR_NOERRCODE 21
ISR_NOERRCODE 22
ISR_NOERRCODE 23
ISR_NOERRCODE 24
ISR_NOERRCODE 25
ISR_NOERRCODE 26
ISR_NOERRCODE 27
ISR_NOERRCODE 28
ISR_NOERRCODE 29
ISR_NOERRCODE 30
ISR_NOERRCODE 31

section .data
align 4096
; idt
idt64:
    TIMES 512 dq 0 ; zero-init 256 * 16 bytes for 256 IDT entries
.pointer:
    dw $ - idt64 - 1 ; idt limit
    dq idt64 ; base address of IDT
