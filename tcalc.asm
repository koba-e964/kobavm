00000000:mov rs5,0x7c00	10 40 07 00 7c 00 00
00000007:mov [rs5-0],0x2	10 42 07 *8bytes*
00000012:mov [rs5-4],0x3	10 42 07 *8bytes*
0000001d:mov [rs5-8],0x4	10 42 07 *8bytes*
00000028:mov ra5,[rs5-4]	10 20 70 fc ff ff ff
0000002f:mov rd5,[rs5-8]	10 20 73 f8 ff ff ff
00000036:mul ra5,rd5	18 00 30
00000039:mov [rs5-12],ra5	10 02 07 f4 ff ff ff
00000040:mov [rs5-16],0x5	10 42 07 *8bytes*
0000004b:mov ra5,[rs5-12]	10 20 70 f4 ff ff ff
00000052:mov rd5,[rs5-16]	10 20 73 f0 ff ff ff
00000059:div ra5,rd5	20
0000005a:mov [rs5-20],ra5	10 02 07 ec ff ff ff
00000061:mov ra5,[rs5-0]	10 20 70 00 00 00 00
00000068:mov rd5,[rs5-20]	10 20 73 ec ff ff ff
0000006f:add ra5,rd5	11 00 30
00000072:mov [rs5-24],ra5	10 02 07 e8 ff ff ff
00000079:mov [rs5-28],0x64	10 42 07 *8bytes*
00000084:mov ra5,[rs5-24]	10 20 70 e8 ff ff ff
0000008b:mov rd5,[rs5-28]	10 20 73 e4 ff ff ff
00000092:add ra5,rd5	11 00 30
00000095:mov [rs5-32],ra5	10 02 07 e0 ff ff ff
exit	ff
