bits 32

global enable_paging

section .text
enable_paging:
    call set_up_page_tables
    
    ; load PML4 to CR3
    mov eax, pml4
    mov cr3, eax

    ; enable PAE-flag in cr4 (Physical Address Extension)
    mov eax, cr4
    or eax, 1 << 5
    mov cr4, eax

    ; set the long mode bit in the EFER MSR (model specific register)
    mov ecx, 0xC0000080
    rdmsr
    or eax, 1 << 8
    wrmsr

    ; enable paging in the cr0 register
    mov eax, cr0
    or eax, 1 << 31
    mov cr0, eax

    ret

set_up_page_tables:
    PT_FLAG_PRESENT equ 1 << 0
    PT_FLAG_WRITEABLE equ 1 << 1
    PT_FLAG_USER equ 1 << 2
    PT_FLAG_HUGE equ 1 << 7
    
    ; VA:
    ; 64--49 48-40 39--31 30-21 21-12 11-0
    ; unused pml4e pdptre pdire ptabe offs
    ; pml4 entry leads to 2^39 of data (512GB)
    ; pdptr entry leads to 2^30 of data (1GB)
    ; pd entry leads to 2^21 of data (2MB)
    ; pt entry leads to 2^12 of data (4KB)
    ; We'll be using 2M huge pages at PD level.

    ; map pml4 1st entry to initial page directory pointer table (pdp0)
    mov eax, pdp0
    or eax, PT_FLAG_PRESENT | PT_FLAG_WRITEABLE
    mov [pml4], eax

    ; map pdp0 1st entry to initial page directory (pd0)
    mov eax, pd0
    or eax, PT_FLAG_PRESENT | PT_FLAG_WRITEABLE
    mov [pdp0], eax
    
    ; map each pd0 entry to a 2MiB page
    mov ecx, 0         ; counter
    .map_next_page_table:
    ; NOTE: Identity mapping
    ; map ecx-th PD0 entry to a huge page that starts at address 2MiB*ecx
    mov eax, 0x200000  ; 2MiB
    mul ecx            ; start address of ecx-th page
    or eax, PT_FLAG_PRESENT | PT_FLAG_WRITEABLE | PT_FLAG_HUGE ; present + writable + huge
    mov [pd0 + ecx * 8], eax ; map ecx-th entry

    inc ecx            ; increase counter
    cmp ecx, 512       ; if counter == 512, the whole PD0 table is mapped
    jne .map_next_page_table  ; else map the next entry

    ; map higher half kernel (PML4 entry index 10000000b and higher)
    ;   user space: 0x0000_0000_0000_0000 - 0x0000_7FFF_FFFF_FFFF
    ; kernel space: 0xFFFF_8000_0000_0000 - 0xFFFF_FFFF_FFFF_FFFF

    ret
	
section .bss 
align 4096
    ; initial paging
    pml4 resb 512*8
    pdp0 resb 512*8
    pd0 resb 512*8