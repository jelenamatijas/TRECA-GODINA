/* Check if the compiler thinks we are targeting the wrong operating system. */
#if defined(__linux__)
#error "You are not using a cross-compiler, you will most certainly run into trouble"
#endif

#include <stdint.h>
#include "vga.h"
#include "keyboard.h"

// Keyboard event handler - displays typed characters
void on_key_press(unsigned char scancode) {
	unsigned char ascii = scancode_to_ascii(scancode);
	if (ascii != 0) {
		vga_putchar(ascii);
	}
}

// Example Hello world procedure
//
// void kernel_main(void) {
// 	// VGA text mode buffer at physical address 0xB8000
// 	// Each character is 2 bytes: [ASCII char][color attribute]
// 	uint16_t *vga_mem = (uint16_t*) 0xB8000;
	
// 	// Write "Hello world!" with different colors
// 	vga_mem[0]  = ((uint16_t) 'H') | ((uint16_t) VGA_WHITE       << 8);
// 	vga_mem[1]  = ((uint16_t) 'e') | ((uint16_t) VGA_BLUE        << 8);
// 	vga_mem[2]  = ((uint16_t) 'l') | ((uint16_t) VGA_GREEN       << 8);
// 	vga_mem[3]  = ((uint16_t) 'l') | ((uint16_t) VGA_CYAN        << 8);
// 	vga_mem[4]  = ((uint16_t) 'o') | ((uint16_t) VGA_RED         << 8);
// 	vga_mem[5]  = ((uint16_t) ' ') | ((uint16_t) VGA_MAGENTA     << 8);
// 	vga_mem[6]  = ((uint16_t) 'w') | ((uint16_t) VGA_BROWN       << 8);
// 	vga_mem[7]  = ((uint16_t) 'o') | ((uint16_t) VGA_LIGHT_GREY  << 8);
// 	vga_mem[8]  = ((uint16_t) 'r') | ((uint16_t) VGA_DARK_GREY   << 8);
// 	vga_mem[9]  = ((uint16_t) 'l') | ((uint16_t) VGA_LIGHT_RED   << 8);
// 	vga_mem[10] = ((uint16_t) 'd') | ((uint16_t) VGA_LIGHT_BLUE  << 8);
// 	vga_mem[11] = ((uint16_t) '!') | ((uint16_t) VGA_LIGHT_GREEN << 8);
// }


// Kernel main entry point
void kernel_main(void) {
	// Initialize VGA display
	vga_init();
	
	// Display welcome message
	vga_set_color(VGA_LIGHT_CYAN, VGA_BLACK);
	vga_puts("Simple x86-64 Kernel\n");
	
	// Set up keyboard handler
	set_keyboard_handler(on_key_press);
	keyboard_init();
	
	vga_set_color(VGA_LIGHT_GREY, VGA_BLACK);
	vga_puts("Keyboard driver initialized. Start typing!\n\n");
	
	// Infinite loop - interrupts will handle keyboard
	while (1) {
		asm volatile ("hlt");
	}
}

