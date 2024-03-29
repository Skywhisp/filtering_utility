# Утилита фильтрации содержимого файлов

## Требование к окружению:  
Версия Java: 21.0.2  
Система сборки: Maven, версия - 4.0.0-alpha-12  
Сторонние библиотеки: Apache Commons CLI, версия - 1.4, ссылка на Maven зависимость:
```
<dependency>
    <groupId>commons-cli</groupId>
    <artifactId>commons-cli</artifactId>
    <version>1.4</version>
</dependency>
```

## Инструкция по запуску программы

**Параметры утилиты:**    

-s: Необязательный параметр. Если указан, программа выводит краткую статистику после обработки файлов.  

-f: Необязательный параметр. Если указан, программа выводит полную статистику после обработки файлов.  

-o: Необязательный параметр с аргументом. Позволяет указать путь к директории для сохранения выходных файлов. По умолчанию используется текущая директория.  

-p: Необязательный параметр с аргументом. Позволяет указать префикс для имен выходных файлов.  

-a: Необязательный параметр. Если указан, данные будут добавлены в существующие файлы, а не перезаписаны.  

[file1 file2 ...]: Обязательные позиционные аргументы. Пути к входным файлам, которые будут обработаны. Файлы могут быть указаны только их названием, например: in1.txt, если они находятся в корневой папке, а также могут указываться с их абсолютным путём, например: "D:\Programming\Filter_utility\src\main\resources\in1.txt"

**Пример входного файла in1.txt**  
Lorem ipsum dolor sit amet  
45  
Пример  
3.1415  
consectetur adipiscing  
-0.001  
тестовое задание  
100500  

**Пример входного файла in2.txt**  
Нормальная форма числа с плавающей запятой  
1.528535047E-25  
Long  
1234567890123456789  

**Пример запуска утилиты**  
```
java -jar util.jar -s -a -p sample- in1.txt in2.txt
```

**Результаты выполнения программы:**  
**sample-integers.txt**  
45  
1234567890123456789  
100500  

**sample-floats.txt**  
1.528535047E-25  
3.1415  
-0.001  

**sample-strings.txt**  
Lorem ipsum dolor sit amet  
Нормальная форма числа с плавающей запятой  
Пример  
Long  
consectetur adipiscing  
тестовое задание
