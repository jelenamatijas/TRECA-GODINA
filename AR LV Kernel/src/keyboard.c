#include "keyboard.h"
#include "port_io.h"

void (*keyboard_handler)(unsigned char scancode) = 0;

unsigned char scancode_to_ascii(unsigned char scan_code) {
	int keyboard_key_released = scan_code & 0x80;

	if (keyboard_key_released)
		return 0;
	
	if (scan_code < sizeof(sc2ascii_printable))
		return sc2ascii_printable[scan_code];

	return 0;
}

void call_keyboard_handler(unsigned char scancode) {
	if (keyboard_handler != 0)
		keyboard_handler(scancode);
}

void set_keyboard_handler(void (*handler)(unsigned char scancode)) {
	keyboard_handler = handler;
}

void keyboard_init() {
	// Enable IRQ1 (keyboard) in PIC
	// Mask all IRQs except IRQ1 (bit 1 = 0)
	outb(0x21, 0xFD);  // 11111101 - only IRQ1 enabled
}
