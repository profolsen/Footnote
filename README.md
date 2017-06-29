# Footnote
!(https://travis-ci.org/profolsen/Footnote.svg?branch=master)

Name
-----

This project began as a footnote to a different project I was working on at the time but took on a life of it's own, much like the footnotes of [David Foster Wallace](https://en.wikipedia.org/wiki/David_Foster_Wallace).

Setup
---------

<b>Requirements.</b>
This project requires java (version 1.5+) to run.  
<b>Installation Steps.</b>
Compile the java source files.  

Running
----------

To run the program on the command line, change into the directory where the compiled code is kept and execute the line:
<pre>java Footnote [options] infile [outfile]</pre>
For infile and outfile, no extension should be specified.

<b>Running with No Options and No outfile.</b> If no options or outfile is specified, then one of the following three things will occur:
1. A program will be assembled.  This happens if infile unambiguously refers to infile.ftnt.
In this case, the assembled program will be in infile.i.
2. A program will be run.  This happens if infile unambiguously refers to infile.i.
3. A program will be assembled and run.  This happens if infile is present with both .ftnt and .i extensions.
In this case, infile.i  will be replaced with a reassembled version (from infile.ftnt file) and the new infile.i file will be run.

<b>Outfile.</b>
The outfile is only valid if infile.ftnt is present.
If infile.ftnt is absent, an error will be printed.
In this case, infile.ftnt will be assembled and saved to outfile.i.

<b>Options.</b>
1. <b>-version</b>  displays the current version.  
If this flag is set, all other arguments are ignored and the version is printed.
2. <b>-sym</b> valid only for assembly.  
Saves the symbol table to symbols.txt
3. <b>-lines</b> valid only for assembly.
Saves the mapping from instruction counter in the assembled program to line of unassembled code.
The map is saved to a file called linemap.txt
4. <b>-memory amount</b> valid only for running.
Sets the memory of the virtual machine to <b>amount</b> 32-bit integers.
The default <b>amount</b> is 256.




Virtual Machine Architecture
-----------------

The Footnote Virtual Machine is simple (There are only sixteen instructions - see below) but powerful (This limited set of instructions is powerful enough to write any program).
Footnote VM operates on 32-bit integers and allows the user to set the memory with a command line argument.

<b>Virtual Machine Basics.</b>
1. stack.  
The virtual machine does not have usable registers; instead, a stack is provided.
The stack always starts at the highest memory location (i.e. the bottom of the stack is always at the highest memory address).
As elements are pushed onto the stack, the stack grows toward memory address 0. 
The location of the top of the stack is stored in a register called 'stack'.
2. maximum stack height.  
FootnoteVM's stack has a capacity that is determined when the program is loaded into main memory.
The maximum stack height is always equal to the maximum memory address minus the number of integers in the specified program.
In other words, the maximum stack height is equal to the maximum memory address minus the number of integers used to define the Executable and Data Sections of a program (see <b>Program Basics</b> below).
3. sequential execution.  
Machine instructions from a program are executed in a sequential order, except in the case of jmp and beq (see <b>Instructions</b> below).
These two instructions allow the user to set the next instruction address to be any arbitrary value (as long as the value is a valid address).
The program counter keeps of the next instruction's address using a special register, called pc.

<b>Program Basics.</b>
Each program written to be executed on the contains three sections:
1. Called the Executable Section, this first section contains all the executable code of the program.
This part of the program begins with the first integer of the program and continues until the first initialization of the data section.
It is best practice to explicitly terminate this section with a hlt instruction (so that it is clear where the next section starts), but it is not an error to terminate in some other way.
2. The Data Section contains the initializations of memory locations which will be used as variables during program execution.
The final integers of the program make up this section beginning after the last instruction.
3. The final section of each program is the Stack Section.
Unlike the Data section which must be explicitly initialized by the user, the stack section is always taken to be the rest of available memory not used by the previous two sections.
For example, if the first two sections together use 130 integers of memory, then the stack section will comprise the remaining 126 integers of memory, assuming there are 256 integers of memory in total.

<b>Instructions.</b>
The following table provides details about the instructions the Footnote VM can interpret.

Preliminary notes:
* Arguments. Some of the instructions in the table below require arguments.
There are no instructions with optional arguments.
If an instruction takes arguments, they must be given.
The argument must follow directly as the next 32 bit integer in the program after the instruction it is for.
* Stack Effects. The effect of the instruction on the stack is also specified in the table below.
The effect is described by providing a before and after picture of the stack relative to the instruction being discussed.
On the left, the before image of the stack is given by the first or first few items on the stack followed by an ellipsis (...) representing the rest of the items on the stack.
On the right, the after image of the stack is given by redrawing the first few items of the stack after the instruction is performed, again followed by an ellipsis.
For example, if the stack effect for an instruction is shown as (x y... -> y x...), then that instruction reverses the order of the top two values on the stack.

<table>
    <colgroup>
       <col span="1" style="width: 12.5%;">
       <col span="1" style="width: 12.5%;">
       <col span="1" style="width: 30%;">
       <col span="1" style="width: 45%;">
    </colgroup>
    <thead>
       <th>Name [argument]</th>
       <th>Opcode</th>
       <th>Stack Effect</th>
       <th>Description</th>
    </thead>
    <tbody>
       <tr>
         <td>jmp</td>
         <td>0x0</td>
         <td>x... -> ...</td>
         <td>This instruction pops the top value off the stack and goes to that position.</td>
       </tr>
       <tr>
    <td>beq</td>
    <td>0x1</td>
    <td>x y z... -> ...</td>
    <td>This instruction branches pops the top three values off the stack. If the second two popped values are equal, it branches to the first popped value.</td>
    </tr>
    <tr>
    <td>ld location</td>
    <td>0x2</td>
    <td><nobr>...&nbsp-> (val&nbspat&nbsplocation)...</nobr></td>
    <td>This instruction pushes a value from a specified location in memory (location) onto the top of the stack. This instruction has two modes, determined by the value of location. (Absolute Mode) If location is greater than or equal to 0, it is treated like memory address, the value stored at that location is put on top of the stack. (Relative Mode) If location is less than zero, it is treated like a relative address, and the value stored at the memory address equal to the maximum stack height plus location is pushed onto the stack.</td>
    </tr>
    <tr>
    <td>sys code</td>
    <td>0x3</td>
    <td>Dependent on Code</td>
    <td>This instruction handles system calls. The code determines which action is taken by the system. Additional arguments (if any) must be on the stack and will be popped off the stack after execution. For more information on system calls, see <b>System Calls</b> below.</td>
    </tr>
    <tr>
    <td>iarith</td>
    <td>0x4</td>
    <td><nobr>x&nbspy...&nbsp->&nbspf(x,&nbspy)...</nobr></td>
    <td>This instruction applies a mathematical function to the top two stack elements. The function this instruction executes depends on the argument code which must be supplied. See <b>Integer Arithmetic Extended Instructions</b> below for details.</td>
    </tr>
    <tr>
    <td>farith code</td>
    <td>0x5</td>
    <td><nobr>x&nbspy...&nbsp->&nbspf(x,&nbspy)...</nobr></td>
    <td>This instruction pops the top two values from the stack, performs some floating point operation (designated by the code argument) on them and pushes the result to the stack. Currently, no floating point operations are defined.</td>
    </tr>
    <tr>
    <td>undefined</td>
    <td>0x6</td>
    <td>No Stack Effects</td>
    <td>This instruction does nothing.</td>
    </tr>
    <tr>
    <td>undefined</td>
    <td>0x7</td>
    <td>No Stack Effects</td>
    <td>This instruction does nothing.</td>
    </tr>
    <tr>
    <td>zero</td>
    <td>0x8</td>
    <td>... -> 0...</td>
    <td>This instruction pushes a zero on the top of the stack.</td>
    </tr>
    <tr>
    <td>one</td>
    <td>0x9</td>
    <td>... -> 1...</td>
    <td>This instruction pushes a one on the top of the stack.</td>
    </tr>
    <tr>
    <td>dup</td>
    <td>0xA</td>
    <td>x... -> x x...</td>
    <td>This instruction pushes a duplicate of the value on top of the stack.</td>
    </tr>
    <tr>
    <td>down val</td>
    <td>0xB</td>
    <td>val = 1:<br><nobr>x&nbspy...&nbsp->&nbspy&nbspx...</nobr><br>val = 2:<br><nobr>x&nbspy&nbspz...&nbsp->&nbspy&nbspz&nbspx...</nobr></td>
    <td>This instruction moves the top element on the stack val levels deep into the stack. The argument val can take on any nonnegative integer value including 0, but for reasons of space, the effects on the stack are only shown for the cases when val = 1 and val = 2.</td>
    </tr>
    <tr>
    <td>undefined</td>
    <td>0xC</td>
    <td>No Stack Effects</td>
    <td>This instruction does nothing.</td>
    </tr>
    <tr>
    <td>ldi val</td>
    <td>0xD</td>
    <td>... -> val...</td>
    <td>This instruction pushes the next value onto the stack.</td>
    </tr>    
    <tr>
    <td>st location</td>
    <td>0xE</td>
    <td>x... -> ...</td>
    <td>This instruction pops a value of the top of the stack and stores in in a specified address in memory (location). Like ld, this instruction operates in two modes, see ld for details.</td>
    </tr>
    <tr>
    <td>hlt</td>
    <td>0xF</td>
    <td>No Stack Effects.</td>
    <td>This instruction terminates a program.|</td>
    </tr>
    </tbody>
</table>








<b>Integer Arithmetic Extended Instructions</b>
An integer arithmetic extended instruction always obtains its arguments by popping the top two values from the stack.
It then executes a mathematical operation on them (e.g., add) and pushes the result to the stack.
When describing various integer arithmetic calls, the following format is used:
* code (name) - description
* 0x1 (add) - adds the top two values on the stack together.
* 0x2 (sub) - subtracts the second to top value on the stack from the top value on the stack.
* 0x3 (mul) - multiplies the top two values on the stack together.
* 0x4 (div) - divides the top value on the stack by the second to top value on the stack.
* 0x5 (cmp) - this instruction compares the top two values on the stack.
If the two values are equal, a zero is pushed.
If the top value is greater, a one is pushed.
If the top value is less than the second to top value, a negative 1 is pushed.

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
* 0x4 () - user input through keyboard.
An integer corresponding to a typed key is pushed on the stack.
the integer-key mapping is system dependent.

<b>Example Program (Fibonacci Numbers).</b>
The following program is designed to print out the first few fibonacci numbers (up through 233).
The following program is formatted like this: <pre>instruction op code and argument //comments appear like this.</pre>
Comments are not allowed in programs written for this FootnoteVM, but are provided for explanation only.
<pre>
8 //pushes 0 on to the stack
9 //pushes 1 on to the stack
10 //beginning of the loop; duplicate the value on the top of the stack, initially a 1.
14 29 //pop and store the top stack element.
4 1//add the top two values on the stack to get the next fibonacci number; store the number on the stack.
10 //duplicate the new fibonacci number.
3 1 3 3 //print out the fibonacci number and a new line (two separate instructions).
14 30 //pop and store the new fibonacci number at address 30 (see below).
2 29 //push the value stored at address 29.  Note that we could also have used -1 as the address to get the same result
2 30 //push the value stored at address 30.
2 29 //push the value stored at 29.
13 144 //push the constant 144 (decimal) on to the stack.
13 28 //push the address 28 (target of a beq) on to the stack.
1 //beq, compare the smaller currently stored fib number with 144 on equality, branch to 27.
13 2 //load the address 2 (top of the loop) on to the stack.
0 //branch unconditionally to address 2 pop 2 off the stack.
15 //address 27.  A halt instruction.  The Executable Section ends here.
0 //variable, address 29. By putting a zero here, this address is reserved and will not be overriden by a push.
0 //variable, address 30.
//the remaining memory addresses will be potentially used by the stack</pre>.

Footnote Assembler
-------------------

An assembler is included to facilitate the writing of programs for the Footnote VM.
Programs written in the assembly language provided here are defined in three sections.
1. The .include section is used to include code from other files in the program.
2. The .declare section is used to define constants and variables.
3. The .begin section is used to store the code to be executed.

Each of these sections begins with ".declare", ".begin" or ".include" on a line by itself.

<b>The .include Section.</b>
The .include section contains a list of files to include.
Files may be in the same directory, only the file name (.ftnt extension is assumed) is needed or files may be in a different directory, in which case the path and file name (.ftnt extension is assumed) are needed.
Any label, variable, constant, string and array from the included file is accessible by redirection.
For example to access a variable called 'alex' from the file 'code', use the label ':code.alex'.
An example of an include is:
<pre>
; includeexample.ftnt
.include
; ...
path/to/modulus ;contains some code with the starting address labeled 'mod'.
; ...
.begin
; ...
jal :modulus.mod  ; using the included code.
                  ; notice that it is unnecessary to include the path.
; ...
</pre>
In this example, modulus.ftnt (contained in the same directory as includeexample.ftnt) contains a block of code with the label 'mod'.
When the program when our program executes the 'jal :modulus.mod' instruction, it jumps to the code included from modulus.ftnt with the 'mod' label and picks up execution there.
Included files may themselves contain included files.
files included multiple times or including themselves (directly or indirectly) have no effect on the assembly of a program.


<b>The .declare Section.</b>
There are four kinds of declarations that may occur in the .declare section.
The first, a variable declaration, signals to the assembler to reserve space to be used as the variable.
Variable declarations always begin with a colon (:) and are followed by a variable name.
Variable names can include any characters except whitespace and '.' in any order.
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

The third kind of declaration is an array declaration.
Array declarations reserve several contiguous memory locations for use as an array.
An example of an array declaration is:
<pre>
:array length 10
</pre>
In this example, :array is the address of the lowest reserved cell in the array.
The length of the array, specified after the word 'length' in this case is 10.

The fourth kind of declaration is a string declaration.
String declarations reserve contiguous memory locations and store a string in those locations.
An example of a string declaration is:
<pre>
:string is 'I am happy.'
</pre>
In this example, :string is the address of the first character in the string 'I am happy.'
A null character (ASCII code 0) is always appended on the end of strings created in this way.
Please note that there are no escape sequences for strings available.


<b>The .begin Section</b>
The main logic of the program is found in the .begin section.
Each line in the .begin section is either (1) a line containing any combination of only whitespace and and a comment ("blank lines"), (2) a line containing code to be assembled, or (3) a label.
Blank lines are ignored by the assember.
A line containing code always begins with the name of a valid instruction.
Valid instruction names are jmp, beq, ld, print, println, printch add, sub, mul, div, zero, one, dup, down, cmp, st, ldi, hlt, jal, read, ret, lda, and sda
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

A label is a line starting with a colon (:) and a name of the label, which can be any combination of characters as long as no whitespace or '.' character is present.
When using a label as an argument to an instruction, the colon must be included.

The following list describes each instruction, its arguments (if any) and what it does.
In the list, an argument of "number" or "address" means the instruction will accept either an integer, variable, or constant as an argument.
If the argument is "label" the argument must be a label. 
Instructions indicated like this<sup>x</sup> are macros, i.e., constructed from several machine instructions.
* jmp :label<sup>x</sup> - branches to the specified label.  
The next instruction to be executed is the instruction that is immediately after the specified label.
* beq :label<sup>x</sup> - branches to the specified label if the top two values on the stack are equal.
The next instruction to be executed is the instruction that is immediately after the specified label.
* ld address - push the value stored at the memory address specified onto the stack.
* print<sup>x</sup> - pops and prints the value on the top of the stack to the screen as a decimal number.
* printch<sup>x</sup> - pops and prints the value on the top of the stack to the screen as a ASCII character.
* println<sup>x</sup> - prints a newline to the screen.
* read<sup>x</sup> - reads a character of input from the user and pushes it on the stack.
* add<sup>x</sup> - adds the top two values on the stack; pops both values off the stack; pushes the result on the stack
* sub<sup>x</sup> - subtracts the top value from the next value on the stack; pops both values off the stack; pushes the result on the stack.
* mul<sup>x</sup> - multiplies the top two values on the stack; pops both values off the stack; pushes the result on the stack
* div<sup>x</sup> - divides the top value by the next value on the stack; pops both values off the stack; pushes the result on the stack.
* zero - pushes the value 0 onto the stack.
* one - pushes the value 1 onto the stack.
* dup - pushes a copy of the value on top of the stack onto the stack.
* down number - moves the element on top of the stack down the specified number of elements into the stack.
* cmp<sup>x</sup> - compares the top two values on the stack, pops them both off the stack and pushes the result of the comparison.
The comparison has three possible results.
The result is -1 if the top value is less than the value underneath it.
The result is 0 if both top values are equal.
The result is 1 if the top value is greater than the value underneath it.
Then pushes a copy of that number of top elements on the stack to the top of the stack.
* ldi number - pushes number to the top of the stack.
* st address - pops the top element off the stack and stores it at the specified address.
* hlt - terminates the program.
* jal label<sup>x</sup> - pushes the address of the next instruction on the stack and then jumps unconditionally to the address the specified label indicates.
* ret - pops the top value off the stack and branches unconditionally to that address.
* lda label<sup>x</sup> - pushes a value stored at a certain offset from label onto the stack.
The offset must be pre-loaded onto the stack and is popped during the execution of this command.
* sda label<sup>x</sup> - Pops and stores the value at the top of the stack at a specified memory location.
The memory location is specified as the memory location specified by label plus an offset which is pre-loaded onto the stack.




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

License
-------

This project is made available under the [MIT License](https://github.com/profolsen/Footnote/blob/master/License.txt). 
