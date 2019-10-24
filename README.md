# script-parser-console-util
Консольное приложение для выполнения скриптов их текстового файла.
Адрес файла со скриптом передается через параметр командной строки при
запуске приложения после чего последовательно исполняются все команды скрипта. 
По достижению последней строки скрипта программа заканчивает выполнение.

Строки могут быть представлены в одном из следующих видов:

```
    <label>: <command>
    
    <label>:
    <command>
```

**label:** - уникальная метка в пределах скрипта (двоеточие после метки обязательно, пробелы между меткой и двоеточием недопустимы)

**command** - отдельная команда в одном из следующих вариантов:

```
    read <variable>
    print <varnum>
    <variable> = <varnum>
    <variable> = <varnum> + <varnum>
    <variable> = <varnum> - <varnum>
    <variable> = <varnum> / <varnum>
    <variable> = <varnum> * <varnum>
    <variable> = <varnum> % <varnum>
    goto <label>
    if <varnum> == <varnum> goto <label>
    if <varnum> <= <varnum> goto <label>
    if <varnum> >= <varnum> goto <label>
    if <varnum> < <varnum> goto <label>
    if <varnum> > <varnum> goto <label>
```
## Операнды:

- **variable** - имя переменной (состоит из букв английского алфавита)
- **number** - целое число (integer 32bit)
- **varnum** - либо переменная, либо число


## Команды

- **read** - считывает числовое значение из командной строки в переменную указанную в качестве аргумента
- **print** - выводит значение varnum в командную строку
- **variable = varnum** - присвоение переменной значения varnum
- **variable = varnum +-*/% varnum** -  присвоение переменой значения выражения между двумя операндами с одним из приведенных операторов
- **goto** - перейти в скрипте к команде после именованной метки указанной в качестве аргумента
- **if...goto** -  перейти в скрипте к команде после именованной метки в случае выполнения условия. В случае невыполнения условия перейти к следующей команде


Каждый аргумент команды должен быть оделен от следующего пробелом.
Дополнительные пробелы, табуляция и пустые строки в скрипте не учитываются.
Текст скрипта не чувствителен к регистру.

## Примет скрипта:

```
    read MAX
    INT1 = 0
    INT2 = 1    
    COUNTER = 0
start:
    if COUNTER < MAX goto cycle
    goto end
cycle:
    RESULT = INT1 + INT2
    print RESULT
    INT1 = INT2
    INT2 = RESULT
    COUNTER = COUNTER + 1
    goto start
end:
```
Аналогичный скрипт:

```
    read MaX
        INT1 = 0


    INT2 = 1    
    COUNTER = 0
start:    if COUNTER             < MAX goto cycle
         goto             end
cycle:


    RESULt        = INT1   +  INT2
    print                   RESULT
    INT1                            = INT2



    int2 = RESULT
    COUNTER     = COuNTeR + 1
    goto         start
end:
```

Вывод командной строки:
```
    max=10
    1
    2
    3
    5
    8
    13
    21
    34
    55
    89
```
