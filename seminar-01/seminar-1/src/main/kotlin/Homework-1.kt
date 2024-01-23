/*
Написать программу, которая обрабатывает
введённые пользователем в консоль команды:
• exit
• help
• add <Имя> phone <Номер телефона>
• add <Имя> email <Адрес электронной почты>
После выполнения команды, кроме команды exit, программа ждёт следующую команду.
 */ 
fun main() {
    var com: String? = ""
    while (com != "exit") {
        print("Введите команду: ")
        com = readlnOrNull()
        val comArr = com?.split(" ")
        if (comArr?.size!! > 0) {
            if (comArr[0] == "exit") return
            if (comArr[0] == "help") help()
            if (comArr[0] == "add" && comArr.size == 4) {
                if (addRecord(comArr[2], comArr[3])) {
                    printResult("${comArr[1]} ${comArr[2]} ${comArr[3]}")
                } else {
                    printResult("Ошибка! Не удалось выполнить команду!")
                }
            } else {
                printResult("Ошибка! Не верный набор параметров!")
            }
        }
    }
}

fun addRecord(com: String, value: String): Boolean {
    if (com == "phone") {
        var phoneToParse: String = value
        if (value.substring(0, 1) == "+") {
            phoneToParse = value.substring(1, value.length)
        }
        return parsePhone(phoneToParse)
    }
    if (com == "email") {
        return parseMail(value)
    }

    return false
}

fun parsePhone(phone: String): Boolean = phone.matches(Regex("""\d+"""))

fun parseMail(mail: String): Boolean = mail.matches(Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"))

fun printResult(msg: String) {
    println(msg)
}

fun help() {
    println(
        """
Введите в консоль команды:
• exit
• help
• add <Имя> phone <Номер телефона>
• add <Имя> email <Адрес электронной почты>
После выполнения команды, кроме команды exit, программа ждёт следующую команду.  
            """.trimIndent()
    )
}