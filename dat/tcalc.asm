00000000:mov rs5,0x7c00	10 40 07 00 7c 00 00
00000007:mov [rs5-0],0x2	10 42 07 *8bytes*
00000012:mov [rs5-4],0x3	10 42 07 *8bytes*
0000001d:mov ra5,[rs5-0]	10 20 70 00 00 00 00
00000024:mov rd5,[rs5-4]	10 20 73 fc ff ff ff
0000002b:mul ra5,rd5	18 00 30
0000002e:mov [rs5-8],ra5	10 02 07 f8 ff ff ff
00000035:mov [rs5-12],0x6	10 42 07 *8bytes*
00000040:mov [rs5-16],0xb	10 42 07 *8bytes*
0000004b:mov ra5,[rs5-12]	10 20 70 f4 ff ff ff
00000052:mov rd5,[rs5-16]	10 20 73 f0 ff ff ff
00000059:mul ra5,rd5	18 00 30
0000005c:mov [rs5-20],ra5	10 02 07 ec ff ff ff
00000063:mov ra5,[rs5-8]	10 20 70 f8 ff ff ff
0000006a:mov rd5,[rs5-20]	10 20 73 ec ff ff ff
00000071:add ra5,rd5	11 00 30
00000074:mov [rs5-24],ra5	10 02 07 e8 ff ff ff
0000007b:mov [rs5-28],0x8	10 42 07 *8bytes*
00000086:mov [rs5-32],0x17	10 42 07 *8bytes*
00000091:mov ra5,[rs5-28]	10 20 70 e4 ff ff ff
00000098:mov rd5,[rs5-32]	10 20 73 e0 ff ff ff
0000009f:mul ra5,rd5	18 00 30
000000a2:mov [rs5-36],ra5	10 02 07 dc ff ff ff
000000a9:mov ra5,[rs5-24]	10 20 70 e8 ff ff ff
000000b0:mov rd5,[rs5-36]	10 20 73 dc ff ff ff
000000b7:add ra5,rd5	11 00 30
000000ba:mov [rs5-40],ra5	10 02 07 d8 ff ff ff
exit	ff
