# IdASM

IdASM is an assembly language designed for the **Id16bit** virtual machine.  
It provides a low-level programming model with a clear syntax, simple instructions, and a register set tailored for this architecture.

---

## 📌 Table of Contents
1. [Introduction](#-introduction)
2. [Syntax](#-syntax)
    - [General Structure](#general-structure)
    - [Values](#values)
    - [Instructions](#instructions)
    - [Controls](#controls)
    - [Comments](#comments)
    - [CPU static addresses](#cpu-static-addresses)
3. [Compiler Options](#-compiler-options)
4. [Target Platform](#-target-platform)
5. [Usage: Hello world !](#usage-hello-world)

resources:
- [XMem compiler](https://github.com/IdCom4/Xmem-compiler) - a binary mem formater allowing easy binary file creation
- [IdASM syntax highlight](https://github.com/IdCom4/IdASM-syntax-highlight-plugin-intellij) - intellij syntax highlight
- [XMem syntax highlight](https://github.com/IdCom4/XMem-syntax-highlight-intellij-plugin) - intellij syntax highlight
- [Id16Bit virtual processor](https://github.com/IdCom4/Id16Bit-virtual-processor) - the target virtual CPU 
---

## 📝 Introduction
IdASM is a **custom assembler** designed to write programs that run on the **Id16bit** virtual CPU.  
It is intended to:
- provide a simple introduction to assembly programming,
- be used for experimentation (emulation, compiler design, reverse engineering).
- have fun

> ⚠️ I didn't have any prior experience in creating programing languages when I created IdASM. \
> I learned a lot while doing it and would do things differently now, specially in regard of the lexing / parsing. \
> So use it as you like, have fun, but be critical towards the implementation 👌

---

## 🔤 Syntax

### General Structure
An IdASM file consists of **functions** containing **instructions** and **controls**.

⚠️ The program entry point depends on the way it is fed to the Id16bit,
but it's usually at the first instruction of the first function, no matter how it's named. 

A function must be declared with the keyword **func** then with the function name:
```idasm
func myAwesomeFunction {}
```

Each instruction follows this format:

- `INSTRUCTION`: the instruction keyword (e.g., `MOVE`, `ADD`, `MUL`)
- `PARAMETER`: can be a register, a numeric literal, a label, etc.

Instructions expect between 0 and 2 parameters, depending on which one.

---

### Values

There is 2 main value types:
- `immediate value`
- `memory address`

each with their subtypes:

`immediate value` types:
- `int` - represents a direct value, can be written in 3 bases:
    - `0` - decimal
    - `0x0` - hexadecimal
    - `0b0` - binary
- `char` - represents a char, but is really an int under the hood.
    - Written as follows: `'a'`
- `string` - represents a string, only accepted by the `PRINT` instruction as of now.
    - Written as follows: `"Hello World !\n"`

`memory address` types:
- `static address` - represents one of the known addresses of the `Id16Bit` CPU.
  - if used as a source, will give it's content as value
  - if used as a destination, will store the value
  - ex: `R0`
  - (see [CPU static addresses](#cpu-static-addresses))
- `pointer` - a pointer will the use the value stored inside the provided memory address
    - Written as follows: `*<memory_address>` ex: `*R0`
- `mem location` - represents a place in RAM.
    - Written as follows: `[<memory_address | immediate_value>]`
  
---

### Instructions
Here is the list of **reserved instructions**:

Data:
- `MOVE SRC<any> DEST<memory_address>` – copy a value into a different memory place
- `PUSH SRC<any>` – push a value to the stack
- `POP DEST<memory_address>` – pop a value to the stack and store it where specified
- `RET VALUE<any>?` - return from a function and push it's return value to the stack, if any

Math: (result go to register `ACC0`)
- `ADD PARAM0<any> PARAM1<any>` – add two values
- `SUB PARAM0<any> PARAM1<any>` – subtract `PARAM1` to `PARAM0`
- `DIV PARAM0<any> PARAM1<any>` – divide `PARAM0` by `PARAM1`
- `MOD PARAM0<any> PARAM1<any>` – modulo `PARAM0` by `PARAM1`
- `MUL PARAM0<any> PARAM1<any>` – multiply `PARAM0` by `PARAM1`
- `INCR PARAM0<any>` – increment `PARAM0` by 1
- `DECR PARAM0<any>` – decrement `PARAM0` by 1

Bitwise:
- `AND PARAM0<any> PARAM1<any>` – and `PARAM1` & `PARAM0`
- `OR PARAM0<any> PARAM1<any>` – or `PARAM1` | `PARAM0`
- `XOR PARAM0<any> PARAM1<any>` – xor `PARAM1` ^ `PARAM0`
- `LSHFT PARAM0<any> PARAM1<any>` – left shift `PARAM0` bits of `PARAM1` place
- `RSHFT PARAM0<any> PARAM1<any>` – right shift `PARAM0` bits of `PARAM1` place

Control: (all `JMPX` instructions will jump to the address stored in `R3` if their condition is met)
- `JMPL PARAM0<any> PARAM1<any>` – jump if `PARAM0` `<` `PARAM1`
- `JMPLE PARAM0<any> PARAM1<any>` – jump if `PARAM0` `<=` `PARAM1`
- `JMPE PARAM0<any> PARAM1<any>` – jump if `PARAM0` `==` `PARAM1`
- `JMPG PARAM0<any> PARAM1<any>` – jump if `PARAM0` `>=` `PARAM1`
- `JMPGE PARAM0<any> PARAM1<any>` – jump if `PARAM0` `>` `PARAM1`
- `GOTO PARAM0<any>` – unconditional jump to the `PARAM0` address
- `INTR PARAM0<any>` – trigger an interrupt with the code in `PARAM0`

IO:
- `PRINT PARAM0<any>` - print the value of `PARAM0` to `STDOUT`
- `GETC PARAM0<memory_address>` - read a char from `STDIN` and store it inside `PARAM0`.
  - ⚠️ note it's non-blocking and that if there is no input to be read, `PARAM0` will be equal to `0`
- `BGETC PARAM0<memory_address>` - wait until there is an input to read from `STDIN` and store it inside `PARAM0`.
  - ⚠️ note it's blocking and will wait until there is an input to read
---

### Controls

You can do some simplified logic controls with `loop` and `if`.
Both have the same structure:
```
<loop | if> <value> <cmp_operator> <value> {
    # instructions
}
```

Example:
```idasm
# list of all cmp operators:
# < | <= | == | >= | >

MOVE 0 R0

loop R0 < 3 {

    MOD R0 2
    
    if ACC0 == 0 {
        ADD R0 48
    
        PRINT "even: "
        PRINT ACC0
        PRINT '\n'
    }
    
    INCR R0
}
```

---

### Comments
Comments begin with `#` and extend to the end of the line:

```idasm
# This is a comment
MOVE 10 R1 # Move 10 into register R1
```
---

### CPU static addresses
The Id16bit CPU provides the following **registers**:

- `R0 / R1 / R2` – general purpose registers
- `R3` – it's value is read by `JMPX` instructions if their condition is met
- `ACC0` – the result of math computation
- `ACC1` – the secondary result of math computation (ex: remainder in division)
- `SPTR` – stack pointer
- `EXPTR` – instruction pointer
- `STACK` - the stack.
  - if used as a source, will decrement `SPTR`
  - if used as a destination, will increment `SPTR`
- `FLAGS` – flags are set based on last math computation result
- `MEMEXT` - is used to increase memory addressing from 16 to 32 bits, are the 16 high bits
- `INTRC` - store the interrupt code,
- `IN` - the computer input
- `OUT` - the computer output

---

### ⚙️ Compiler Options
The **IdASM** compiler supports several options:

- `-o=<output_file_name>` – output file name

It outputs a xmem file, that can itself be compiled using the [XMem compiler](https://github.com/IdCom4/Xmem-compiler) to the final binary file

---

### 🖥 Target Platform

[Id16Bit virtual processor](https://github.com/IdCom4/Id16Bit-virtual-processor)

- **Name**: `Id16bit`
- **Architecture**: 16-bit virtual CPU
- **Endianness**: big-endian
- **Memory**: can be configured when starting the computer
    - will always start execution at `0x19` memory address
- **Instruction set**: *defined above*

---

### Usage: Hello world
```idasm
# hello_world.idasm

func _start {
    print "Hello world !\n"
    ret 0
}
```

````shell
$> ./build.sh
$> ./run.sh hello_world.idasm -o=hello_world
````

```xmem
# hello_world.xmem

$byte_encoding=02

8020 0048 000d
8020 0065 000d
8020 006c 000d
8020 006c 000d
8020 006f 000d
8020 0077 000d
8020 0020 000d
8020 0077 000d
8020 006f 000d
8020 0072 000d
8020 006c 000d
8020 0064 000d
8020 0020 000d
8020 0021 000d
8020 000a 000d
8020 ffff 000d
8020 0000 0006
0020 000b 0007
0020 000b 0005
0020 000b 0004
0020 000b 0002
0020 000b 0001
0020 000b 0000
0020 000b 0003
0020 0006 000b
0020 0003 0008
```
