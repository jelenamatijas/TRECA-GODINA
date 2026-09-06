#ifndef VGA_H
#define VGA_H

#include <stddef.h>
#include <stdint.h>
#include <stdbool.h>

#define VGA_WIDTH 80
#define VGA_HEIGHT 25

// VGA color constants
#define VGA_BLACK 0
#define VGA_BLUE 1
#define VGA_GREEN 2
#define VGA_CYAN 3
#define VGA_RED 4
#define VGA_MAGENTA 5
#define VGA_BROWN 6
#define VGA_LIGHT_GREY 7
#define VGA_DARK_GREY 8
#define VGA_LIGHT_BLUE 9
#define VGA_LIGHT_GREEN 10
#define VGA_LIGHT_CYAN 11
#define VGA_LIGHT_RED 12
#define VGA_LIGHT_MAGENTA 13
#define VGA_LIGHT_BROWN 14
#define VGA_WHITE 15

// Initialize VGA driver
void vga_init();

// Clear the screen
void vga_clear();

// Write a single character at current cursor position
void vga_putchar(char c);

// Write a string at current cursor position
void vga_puts(const char* str);

// Set text color (background << 4 | foreground)
void vga_set_color(uint8_t fg, uint8_t bg);

// Update hardware cursor position
void vga_update_cursor();

#endif
