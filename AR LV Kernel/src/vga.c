#include "vga.h"
#include "port_io.h"
#include "string.h"

static uint16_t* video_mem = (uint16_t*) 0xB8000;
static size_t cursor_x = 0;
static size_t cursor_y = 0;
static uint8_t color = (VGA_BLACK << 4) | VGA_LIGHT_GREY;

void vga_init() {
	vga_clear();
	vga_set_color(VGA_LIGHT_GREY, VGA_BLACK);
}

void vga_clear() {
	for (size_t i = 0; i < VGA_WIDTH * VGA_HEIGHT; i++) {
		video_mem[i] = ((uint16_t) ' ') | ((uint16_t) color << 8);
	}
	cursor_x = 0;
	cursor_y = 0;
	vga_update_cursor();
}

void vga_set_color(uint8_t fg, uint8_t bg) {
	color = (bg << 4) | fg;
}

static void vga_scroll() {
	// Move all lines up by one
	memmove(video_mem, video_mem + VGA_WIDTH, (VGA_HEIGHT - 1) * VGA_WIDTH * 2);
	
	// Clear the last line
	for (size_t x = 0; x < VGA_WIDTH; x++) {
		video_mem[(VGA_HEIGHT - 1) * VGA_WIDTH + x] = ((uint16_t) ' ') | ((uint16_t) color << 8);
	}
}

void vga_putchar(char c) {
	if (c == '\n') {
		cursor_x = 0;
		cursor_y++;
	} else if (c == '\b') {
		if (cursor_x > 0) {
			cursor_x--;
			video_mem[cursor_y * VGA_WIDTH + cursor_x] = ((uint16_t) ' ') | ((uint16_t) color << 8);
		}
	} else if (c == '\t') {
		cursor_x = (cursor_x + 4) & ~3;
	} else {
		video_mem[cursor_y * VGA_WIDTH + cursor_x] = ((uint16_t) c) | ((uint16_t) color << 8);
		cursor_x++;
	}
	
	// Wrap to next line
	if (cursor_x >= VGA_WIDTH) {
		cursor_x = 0;
		cursor_y++;
	}
	
	// Scroll if necessary
	if (cursor_y >= VGA_HEIGHT) {
		vga_scroll();
		cursor_y = VGA_HEIGHT - 1;
	}
	
	vga_update_cursor();
}

void vga_puts(const char* str) {
	for (size_t i = 0; str[i] != '\0'; i++) {
		vga_putchar(str[i]);
	}
}

void vga_update_cursor() {
	uint16_t pos = cursor_y * VGA_WIDTH + cursor_x;
	
	// Set cursor position via VGA control registers
	outb(0x3D4, 0x0F);
	outb(0x3D5, (uint8_t) (pos & 0xFF));
	outb(0x3D4, 0x0E);
	outb(0x3D5, (uint8_t) ((pos >> 8) & 0xFF));
}
