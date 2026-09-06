ENTRY(start) /* Entry point. */

/* -- Linkerska skripta --

U linkerskoj skripti se sa ENTRY komandom definiše koja će biti ulazna tačka programa. Definisane su i odgovarajuće sekcije izlaznog fajla, sa početnim adresama poravnatim na 4096 (4K). U linkerskoj skripti, u SECTIONS bloku, specificirano je kako se sekcije u izlaznom fajlu formiraju na osnovu ulaznih sekcija, tj. sekcija iz ulaznih fajlova. Na primjer, sekcija .text izlaznog fajla dobija se spajanjem sekcija .multiboot_header, .text i .rodata iz svih ulaznih fajlova.

Sekcije se smještaju u adresni prostor onim redoslijedom kako su navedeni u SECTIONS bloku, osim ako se eksplicitno ne naznači adresa na koju treba da se smjesti neka sekcija. Sa naredbom . = 1M, koja je smještena na početku SECTIONS bloka linkerske skripte u priloženom primjeru, određuje se da smještanje prve sekcije treba da počne od prvog megabajta u adresnom prostoru. Ovo je konvencija, jer su adrese ispod prvog megabajta rezervisane (tekstualna videomemorija se nalazi u opsegu memorijskih lokacija ispod prvog megabajta).

*/

SECTIONS 
{
	/* Start at 1MB to avoid low memory */
	. = 1M;

	/* Executable code section (read + execute) */
	.text BLOCK(4K) : ALIGN(4K)
	{
		*(.multiboot_header)
		*(.text)
	} :text

	/* Read-only data section (read only) */
	.rodata BLOCK(4K) : ALIGN(4K)
	{
		*(.rodata)
	} :rodata

	/* Initialized read-write data (read + write) */
	.data BLOCK(4K) : ALIGN(4K)
	{
		*(.data)
	} :data

	/* Exception handling frames (read + write) */
	.eh_frame BLOCK(4K) : ALIGN(4K)
	{
		*(.eh_frame)
	} :data

	/* Uninitialized read-write data and stack (read + write) */
	.bss BLOCK(4K) : ALIGN(4K)
	{
		*(COMMON)
		*(.bss)
	} :data
}

/* Program headers define segment permissions to avoid RWX warnings */
PHDRS
{
	text PT_LOAD FLAGS(5);    /* Read + Execute (0b101) */
	rodata PT_LOAD FLAGS(4);  /* Read only (0b100) */
	data PT_LOAD FLAGS(6);    /* Read + Write (0b110) */
}
