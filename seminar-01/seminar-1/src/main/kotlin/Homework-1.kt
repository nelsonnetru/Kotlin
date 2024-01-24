/*
Написать программу, которая обрабатывает
введённые пользователем в консоль команды:
• exit
• help
• add <Имя> phone <Номер телефона>
• add <Имя> email <Адрес электронной почты>
После выполнения команды, кроме команды exit, программа ждёт следующую команду.
 */
fun main(vararg args: String) {
    var com: String? = ""
    while (true) {
        print("Введите команду: ")
        com = readlnOrNull()
        val comArr = com?.split(" ")
        if (comArr != null) {
            when (comArr.firstOrNull()) {
                "exit" -> return
                "help" -> help()
                "add" -> {
                    if (comArr.size == 4 && addRecord(comArr[2], comArr[3]))
                        printResult("${comArr[1]} ${comArr[2]} ${comArr[3]}")
                    else
                        printResult("Ошибка! Не удалось выполнить команду!")
                }
                else -> printResult("Ошибка! Не верный набор параметров!")
            }
        }
    }
}

fun addRecord(com: String, value: String): Boolean {
    return when(com) {
        "phone" -> parsePhone(value)
        "email" -> parseMail(value)
        else -> false
    }
}

fun parsePhone(phone: String): Boolean = phone.matches(Regex("""^\+?\d+${'$'}"""))

fun parseMail(mail: String): Boolean = mail.matches(Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"))

fun printResult(msg: String) = println(msg)

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