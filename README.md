# script-parser
Консольное приложение для выполнения скриптов их текстового файла.
Адрес файла со скриптом передается через параметр командной строки
запуске приложения Последовательно исполняются все команды. 
По достижению последней строки скрипта программа заканчивает выполнение.

Строки могут быть представлены в следующем виде:

```
    <label>: <command>
    <label>:
    <command>
```

**label:** - уникальная метка в пределах скрипта 
(двоеточие после метки обязательно,пробелы между меткой и двоеточием недопустимы)
**command** - отдельная команда, которая может быть следующей формы:

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

- **variable** - имя переменной (допустимо использование букв и чисел, нельзя использовать только числа)
- **varnum** - либо переменная, либо число
- **number** - целое число (integer 32bit)

## Команды

- **read** - считывает числовое значение из командной строки в переменную
- **print** - выводит значение varnum в командную строку
- **variable = varnum** - присвоение переменной значения varnum
- **variable = varnum +-*/% varnum** -  присвоение переменой значения выражения между двумя операндами с одним из приведенных операторов оператором
- **goto** - перейти в скрипте к команде после именованной метки
- **if..goto** -  перейти в скрипте к команде после именованной метки в случае выполнения условия. В случае невыполнения условия перейти к следующей команде


Пробелы и пустые строки в скрипте не учитываются.
Текст не чувствителен к регистру.

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
Аналогично отработает следующий скрипт:

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
