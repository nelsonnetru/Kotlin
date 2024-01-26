package p01

const val ERROR_MESSAGE: String = "Ошибка: неизвестная комманда!"
const val ERROR_COUNT_COMMANDS_FOR_ADD_MESSAGE = "Не верный набор параметров для команды"
const val COUNT_COMMANDS_FOR_ADD: Int = 4
const val ADD_PERSON_ERROR_MESSAGE: String = "Ошибка: не удалось добавить персону"
const val NOT_INIT_MESSAGE: String = "Not initialized"

fun parsePhone(phone: String): Boolean = phone.matches(Regex("""^\+?\d+${'$'}"""))
fun parseMail(mail: String): Boolean = mail.matches(Regex("""^[A-Za-z\d](.*)(@)(.+)(\.)([A-Za-z]{2,})"""))

data class Person(val name: String, var email: String? = null, var phone: String? = null)

interface Command {
    fun isValid(): Boolean
}

sealed class AppCommand : Command

class AddCommand(private val comArr: List<String>) : AppCommand() {
    var ADD_ERROR_MSG: String = ""

    fun add(): Person? {
        if (this.isValid()) {
            val newPerson = Person(comArr[1])
            when (comArr[2]) {
                "email" -> newPerson.email = comArr[3]
                "phone" -> newPerson.phone = comArr[3].replace("+", "")
            }
            return newPerson
        }

        return null
    }

    override fun isValid(): Boolean {
        if (comArr.size == COUNT_COMMANDS_FOR_ADD) {
            return when (comArr[2]) {
                "email" ->
                    if (!parseMail(comArr[3])) {
                        ADD_ERROR_MSG = "Некорректный email"
                        false
                    } else true

                "phone" ->
                    if (!parsePhone(comArr[3])) {
                        ADD_ERROR_MSG = "Некорректный телефон"
                        false
                    } else true
                else -> {
                    ADD_ERROR_MSG = "Неизвестный параметр " + comArr[2]
                    false
                }
            }
        }
        ADD_ERROR_MSG = ERROR_COUNT_COMMANDS_FOR_ADD_MESSAGE
        return false
    }

    override fun toString(): String {
        return "AddCommand(comArr=$comArr)"
    }
}

object HelpCommand : AppCommand() {
    override fun isValid(): Boolean = true

    override fun toString(): String = """

=== ПОМОЩЬ ===
Введите в консоль команды:
• exit
• help
• show выводит последнее значение, введённой с помощью команды add
• add <Имя> phone <Номер телефона>
• add <Имя> email <Адрес электронной почты>
После выполнения команды, кроме команды exit, программа ждёт следующую команду.
""".trimIndent()
}

object ExitCommand : AppCommand() {
    override fun isValid(): Boolean = true
}

object ShowCommand : AppCommand() {
    override fun isValid(): Boolean = true
}

fun readCommand(strCommand: String?): Command? {
    val comArr = strCommand?.split(" ") ?: return null

    return when (comArr[0]) {
        "add" -> AddCommand(comArr)
        "show" -> ShowCommand
        "help" -> HelpCommand
        "exit" -> ExitCommand
        else -> null
    }

}

fun main() {
    var com: String?
    var person: Person? = null
    while (true) {
        print("Введите команду: ")
        com = readlnOrNull()
        when (val resultCommand = readCommand(com)) {
            is ExitCommand -> return
            is HelpCommand -> println(resultCommand)
            is ShowCommand -> println(person ?: NOT_INIT_MESSAGE)
            is AddCommand -> {
                person = resultCommand.add()
                println(person ?: (ADD_PERSON_ERROR_MESSAGE + " (" + resultCommand.ADD_ERROR_MSG + ")" + HelpCommand))
            }
            null -> println(ERROR_MESSAGE + HelpCommand)
        }
    }
}