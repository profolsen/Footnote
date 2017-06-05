# stackmachine      


Chip Architecture 
-----------------

The chip the project simulates is simple (There are only sixteen instructions - see below) but powerful (This limited set of instructions is powerful enough to write any program).
The chip operates on 32-bit integers and assumes a memory of 256 integers (although this is expanded on some systems).

<b>Chip Basics.</b>
1. stack.  The chip does not have usable registers; instead, a stack is provided.
The stack always starts at the highest memory location (i.e. the bottom of the stack is always at the highest memory address).
As elements are pushed onto the stack, the stack grows downward. 
The location of the top of the stack is stored in a register called 'stack'.
2. maximum stack height.  The chip's stack has a capacity that is determined when the program is loaded into main memory.
The maximum stack height is always equal to the maximum memory address minus the number of integers in the specified program.
In other words, the maximum stack height is equal to the maximum memory address minus the number of integers used to define the Executable and Data Sections of a program (see <b>Program Basics</b> below).
3. sequential execution.  Instructions from a program are executed in a sequential order, except in the case of jmp and beq.
These two instructions allow the user to set the next instruction address to be any arbitrary value (as long as the value is a valid address).
The program counter keeps of the next instruction's address using a special register, called pc.

<b>Program Basics.</b>
Each program written to be executed on the chip contains three sections:
1. Called the Executable Section, this first section contains all the executable code of the program.
This part of the program begins with the first integer of the program and continues until the first initialization of the data section.
It is best practice to explicitly terminate this section with a hlt instruction (so that it is clear where the next section starts), but it is not an error to terminate in some other way.
2. The Data Section contains the initializations of memory locations which will be used as variables during program execution.
The final integers of the program make up this section beginning after the last instruction.
3. The final section of each program is the Stack Section.
Unlike the Data section which must be explicitly initialized by the user, the stack section is always taken to be the rest of available memory not used by the previous two sections.
For example, if the first two sections together use 130 integers of memory, then the stack section will comprise the remaining 126 integers of memory.

<b>Instructions.</b>
Each instruction will be defined and discussed using the format described below:
- [name (opcode)] [args] | [(before -> after) description]

Each list item begins by giving the name and the opcode (in hexadecimal) for the instruction.
Then, the arguments taken by the instruction are given.
There are no instructions with optional arguments.
If a instruction takes arguments, they must be given.
After the pipe, the effect of the instruction on the stack (if there is one) is specified.  
The effect is described by providing a before and after picture of the stack relative to the instruction being discussed.
On the left, the before image of the stack is given by the first or first few items on the stack followed by an ellipsis (...) representing the rest of the items on the stack.
On the right, the after image of the stack is given by redrawing the first few items of the stack after the instruction is performed, again followed by an ellipsis.
For example, if the stack effect is pictured as (x y... -> y x...), then this means that the top two values on the stack are reversed in order.
Finally, there is a short description of what the instruction does.
* dup (0xA) | (x... -> x x...) this instruction pushes a duplicate of the value on top of the stack.
* dup2 (0xB) | (x y... -> x y x y...) this instruction duplicates the top two values on the stack.
* dupn (0xC) | (no before & after picture drawn) this instruction pops the top value of the stack and duplicates that number of values.
* add (0x4) | (x y... -> x+y...) this instruction replaces the top value on the stack with the sum of the top two values on the stack.
* sub (0x5) | (x y... -> x-y...) this instruction replaces the top value on the stack with the top value minus the next value.
* mul (0x6) | (x y... -> x*y...) this instruction replaces the top value on the stack with the top two values multiplied together.
* div (0x7) | (x y... -> x/y...)) this instruction replaces the top value on the stack with the top value divided by the next value.
* jmp (0x0) | (x... -> ...) this instruction pops the top value off the stack and goes to that position.
* beq (0x1) | (x y z... -> ...) this instruction branches pops the top three values off the stack.
If the second two popped values are equal, it branches to the first popped value.
* zero (0x8) | (... -> 0...) this instruction pushes a zero on the top of the stack.
* one (0x9) | (... -> 1...) this instruction pushes a one on the top of the stack.
* ld location (0x2) | (... -> val&#64;location...) this instruction pushes a value from a specified location in memory (location) onto the top of the stack.
This instruction has two modes, determined by the value of location.
(Absolute Mode) If location is greater than or equal to 0, it is treated like memory address, the value stored at that location is put on top of the stack.
(Relative Mode) If location is less than zero, it is treated like a relative address, and the value stored at the memory address equal to the maximum stack height plus location is pushed onto the stack.
* sys code (0x3) | (no before and after shown) this instruction handles system calls.
The code determines which action is taken by the system.
Additional arguments (if any) must be on the stack and will be popped off the stack after execution.
For more information on system calls, see <b>System Calls</b> below.
* st location (0xE) | (x... -> ...) this instruction pops a value of the top of the stack and stores in in a specified address in memory (location).
Like ld, this instruction operates in two modes, see ld for details.
* hlt (0x15) |  this instruction signals the end of a program.
* ldi val (0xD) | (... -> val...) this instruction pushes the next value onto the stack.

<b>Wait... No Pop?</b>
It is true that there is no pop instruction.  With good memory management, being able to pop a value of the stack is unnecessary.  However, if popping is something you just can't do without, the following sequence of instructions will pop a value off the stack: 
0. -> x y...
1. dup -> x x y...
2. zero -> 0 x x y...
3. sub -> -x x y...
4. add -> 0 y...
5. add -> y...

The disclaimer here is of course that this only works if there's more than one value on the stack.
This problem can be easily avoided by just pushing a zero on the stack at the beginning of program execution.


<b>System Calls.</b>
When describing various system calls, the following format is used:
* code (arg1, arg2, ..., argn) - description

The code determines which system call will be executed.
The list in parenthesis gives the names of all arguments the system call requires.
If the system call requires no arguments, empty parentheses will be shown.
After the code and arguments list, the action of the call is described.
* 0x1 () - prints the value on top of the stack to the screen in decimal.
* 0x2 () - Interprets the value on top of the stack as an ASCII character and then prints it to the screen.
* 0x3 () - prints a newline character to the screen.

<b>Example Program (Fibonacci Numbers).</b>
The following program is designed to print out the first few fibonacci numbers (up through 233).
The following program is formatted like this: <pre>instruction(s) //comments appear like this.</pre>
Comments are not allowed in programs written for this chip, but are provided for explanation only.
<pre>
8 //pushes 0 on to the stack
9 //pushes 1 on to the stack
10 //beginning of the loop; duplicate the value on the top of the stack, initially a 1.
14 28 //pop and store the top stack element.
4 //add the top two values on the stack to get the next fibonacci number; store the number on the stack.
10 //duplicate the new fibonacci number.
3 1 3 3 //print out the fibonacci number and a new line (two separate instructions).
14 29 //pop and store the new fibonacci number at address 29 (see below).
2 28 //push the value stored at address 28.  Note that we could also have used -1 as the address to get the same result
2 29 //push the value stored at address 29.
2 28 //push the value stored at 28.
13 144 //push the constant 144 (decimal) on to the stack.
13 27 //push the address 27 (target of a beq) on to the stack.
1 //beq, compare the smaller currently stored fib number with 144 on equality, branch to 27.
13 2 //load the address 2 (top of the loop) on to the stack.
0 //branch unconditionally to address 2 pop 2 off the stack.
15 //address 27.  A halt instruction.  The Executable Section ends here.
0 //variable, address 28. By putting a zero here, this address is reserved and will not be overriden by a push.
0 //variable, address 29.
//the remaining memory addresses will be potentially used by the stack</pre>.

Assembler
---------

An assembler is included with this project to facilitate the writing of programs for the chip.
Programs written in the assembly language provided here are defined in two sections.
1. The .declare section is used to define constants and variables.
2. The .begin section is used to store the code to be executed.

Each of these sections begins with ".declare" or ".begin" on a line by itself.

<b>The .declare Section.</b>
There are two kinds of declarations that may occur in the .declare section.
The first, a variable declaration, signals to the assembler to reserve space to be used as the variable.
variable declarations always begin with a colon (:) and are followed by a variable name.
variable names can include any characters except whitespace in any order.
An example of a variable declaration is:
<pre>:var</pre>
In this example, a variable named :var is created.  
Note that the colon is part of the variable name.

The second kind of declaration is a constant declaration.
These declarations are similar, but the constant needs a value associated with the name.
An example would be:
<pre>
:two 2
</pre>
In this example, a constant named two is created and is equal to 2.
This constant can be used anywhere an address is expected, but might be particularly useful to make ldi instructions easier to read.

<b>The .begin Section</b>
The main logic of the program is found in the .begin section.
Each line in the .begin section is either (1) a line containing any combination of only whitespace and and a comment ("blank lines"), (2) a line containing code to be assembled, or (3) a label.
Blank lines are ignored by the assember.
A line containing code always begins with the name of a valid instruction.
Valid instruction names are jmp, beq, ld, print, println, printch add, sub, mul, div, zero, one, dup, dup2, dupn, st, ldi, and hlt.
If the instruction requires arguments, arguments follow.
A line may also be a label.
A label is used to indicate the destination of a branching (beq) or jumping (jmp) instruction.
Labels do not need to be defined before they are used so it is possible to branch forward in a program.
Here is an example of a label being defined and used:
<pre>
:infiniteloop
jmp :infiniteloop
</pre>
In this example, the label :infiniteloop is the location of the first executable line of code after the label appears.
In this case, that means the label :infiniteloop refers to the jmp instruction.
Therefore, this program is an infinite loop: the jmp instruction branches to itself.

A label is a line starting with a colon (:) and a name of the label, which can be any combination of non-whitespace characters.
When using a label as an argument to an instruction, the colon must be included.

The following list describes each instruction, its arguments (if any) and what it does.
In the list, an argument of "number" or "address" means the instruction will accept either an integer, variable, or constant as an argument.
If the argument is "label" the argument must be a label.  
* jmp :label - branches to the specified label.  
The next instruction to be executed is the instruction that is immediately after the specified label.
* beq :label - branches to the specified label if the top two values on the stack are equal.
The next instruction to be executed is the instruction that is immediately after the specified label.
* ld address - push the value stored at the memory address specified onto the stack.
* print - pops and prints the value on the top of the stack to the screen as a decimal number.
* printch - pops and prints the value on the top of the stack to the screen as a ASCII character.
* println - prints a newline to the screen.
* add - adds the top two values on the stack; pops both values off the stack; pushes the result on the stack
* sub - subtracts the top value from the next value on the stack; pops both values off the stack; pushes the result on the stack.
* mul - multiplies the top two values on the stack; pops both values off the stack; pushes the result on the stack
* div - divides the top value by the next value on the stack; pops both values off the stack; pushes the result on the stack.
* zero - pushes the value 0 onto the stack.
* one - pushes the value 1 onto the stack.
* dup - pushes a copy of the value on top of the stack onto the stack.
* dup2 - pushes a copy of the top 2 values on the stack to the top of the stack.
* dupn - pops a number off the stack.
Then pushes a copy of that number of top elements on the stack to the top of the stack.
* ldi number - pushes number to the top of the stack.
* st address - pops the top element off the stack and stores it at the specified address.
* hlt - terminates the program.

<b>Comments.</b> a comment can be made on any line by using the semicolon (;) character.  
All text on a line after this character is ignored.

<b>Example Program.</b>
<pre>
; Fibonacci number listing program.
; Created by Paul Olsen, (c) 2017.


.declare
:x          ; f_{n-1}
:y          ; f_{n}
:limit 144


.begin
zero
one        ; start by pushing 0 and 1 onto the stack.

:loop
dup        ; duplicate the value on the top of the stack.
st :x      ; store the top value (f_{n-1}) on the stack to memory location x.
           ; we'll need this number to calculate f_{n+1}
add        ; this generates the next fibonacci number (f_{n}).
dup        ; duplicate the top value on the stack so it can be printed.
print      ; print f_{n}.
println    ; print a new line.
st :y      ; store f_{n} to memory address y.
ld :x
ld :y      ; getting ready for next iteration of the loop.
ld :x      ; getting ready for the branch equal instruction (the while condition).
ldi :limit ; pushing 144 onto the stack
beq :end   ; if x==144, goto end.
jmp :loop  ; ...else goto loop (stay in the loop).

:end
hlt        ; end of the program.

</pre>



