#ifndef KEYBOARD_H
#define KEYBOARD_H

#include <stdint.h>

// Keyboard IRQ is IRQ1, which maps to interrupt 33 (0x21)
#define KEYBOARD_IRQ 1
#define KEYBOARD_INTERRUPT (32 + KEYBOARD_IRQ)

// Function pointer for keyboard event handler
extern void (*keyboard_handler)(unsigned char scancode);

// Convert scancode to ASCII character (0 if non-printable)
unsigned char scancode_to_ascii(unsigned char scan_code);

// Set the keyboard handler function
void set_keyboard_handler(void (*handler)(unsigned char scancode));

// Called by ISR to dispatch to keyboard handler
void call_keyboard_handler(unsigned char scancode);

// Initialize keyboard (enable IRQ1)
void keyboard_init();

// Scancode to ASCII mapping for scan code set 1
static char sc2ascii_printable[] = {
	0, // ?
	0, // escape
	'1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
	'-', '=',
	'\b', // backspace
	'\t', // tab,
	'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 
	'[', ']', 
	'\n',
	0, // lctrl
	'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l',
	';', '\'', '`',
	0, // lshift,
	'\\',
	'z', 'x', 'c', 'v', 'b', 'n', 'm',
	',', '.', '/',
	0, // rshift,
	'*', // numpad *
	0, // lalt,
	' ', // space,
	0, // capslock
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // f1--f10
	0, // numlock,
	0, // scrolllock,
	'7', '8', '9', '-', '4', '5', '6', '+', '1', '2', '3', '0', '.', // numpad
	0, // ?
	0, // ?
	0, // ?
	0, 0 // f11, f12
};

#endif
