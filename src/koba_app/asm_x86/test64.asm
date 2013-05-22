.bits 0x40
.syntax intel

.import _filelen,_printf
.global _main
.section ".text"

_main:	;int main(int argc,char** argv);
	cmp ecx,0x1;	argc<=1
	jmp end
	mov rcx,qword ptr[rdx+8];argv[1]
	add rsp,-0x28;	48 83 c4 d8
	call _filelen;
	mov r8,rax
	mov rdx,rcx
	mov rcx,Label077;
	call _printf;
	add rsp,0x28;	48 83 c4 28
end:
	xor eax,eax;
	ret

.section ".data"

Label077:
	.asciz "The length of file %s is 0x%016x"
	
	
	
