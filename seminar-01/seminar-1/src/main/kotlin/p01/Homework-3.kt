package p01

const val KEY_FOR_SUCCESS_ADD_PARAMETER: String = "ok"
const val ERROR_PHONE_FORMAT: String = "Некорректный телефон"
const val ERROR_EMAIL_FORMAT: String = "Некорректный email"
const val ERROR_UNKNOWN_PARAMETER = "Неизвестный параметр"
const val ERROR_MESSAGE: String = "Ошибка: неизвестная комманда!"
const val FIND_ERROR_MESSAGE: String = "Ничего не найдено"
const val ERROR_COUNT_COMMANDS = "Не верный набор параметров для команды"
const val COUNT_COMMANDS_FOR_ADD: Int = 4
const val COUNT_COMMANDS_FOR_ADD_PARAMETER: Int = 3
const val COUNT_COMMANDS_FOR_SHOW: Int = 2
const val COUNT_COMMANDS_FOR_FIND: Int = 2
const val ADD_PERSON_ERROR_MESSAGE: String = "Ошибка: не удалось добавить персону"
const val ADD_PERSON_ERROR_MESSAGE_DUPLICATE: String = "Запись с таким именем уже добавлена"

fun parsePhone(phone: String): Boolean = phone.matches(Regex("""^\+?\d+${'$'}"""))
fun parseMail(mail: String): Boolean = mail.matches(Regex("""^[A-Za-z\d](.*)(@)(.+)(\.)([A-Za-z]{2,})"""))

data class Person(
    val name: String,
    var email: MutableList<String> = mutableListOf(),
    var phone: MutableList<String> = mutableListOf(),
)

sealed interface Command {
    fun isValid(): Boolean
}

class AddCommand(private val comArr: List<String>) : Command {
    var addErrorMsg: String = ""

    fun add(phoneBook: MutableList<Person>): Person? {
        if (this.isValid()) {
            if (findByNamePerson(phoneBook, comArr[1]) == null) {
                val newPerson = Person(comArr[1])
                val result: String = addParamToPerson(newPerson, comArr[2], comArr[3])
                if (result == KEY_FOR_SUCCESS_ADD_PARAMETER) return newPerson
                else addErrorMsg = result
            } else addErrorMsg = ADD_PERSON_ERROR_MESSAGE_DUPLICATE
        } else addErrorMsg = ERROR_COUNT_COMMANDS

        return null
    }

    override fun isValid(): Boolean {
        if (comArr.size != COUNT_COMMANDS_FOR_ADD) return false
        return true
    }

    override fun toString(): String {
        return "AddCommand(comArr=$comArr)"
    }
}

object HelpCommand : Command {
    override fun isValid(): Boolean = true

    override fun toString(): String = """

=== ПОМОЩЬ ===
Введите в консоль команды:
• exit
• help
• show <Имя>
• AddPhone <Имя> <Номер телефона>
• AddEmail <Имя> <Адрес электронной почты>
• add <Имя> phone <Номер телефона>
• add <Имя> email <Адрес электронной почты>
После выполнения команды, кроме команды exit, программа ждёт следующую команду.
""".trimIndent()
}

object ExitCommand : Command {
    override fun isValid(): Boolean = true
}

class ShowCommand(private val comArr: List<String>) : Command {
    var showErrorMsg: String = ""

    fun showRecord(phoneBook: MutableList<Person>): Person? {
        if (this.isValid()) {
            for (record in phoneBook) {
                if (record.name == comArr[1]) return record
            }
            showErrorMsg = FIND_ERROR_MESSAGE
        } else showErrorMsg = ERROR_COUNT_COMMANDS

        return null
    }

    override fun isValid(): Boolean {
        if (comArr.size == COUNT_COMMANDS_FOR_SHOW) return true
        return false
    }
}

open class AddEmail(private val comArr: List<String>) : Command {
    open var parameterName = "email"
    var showErrorMsg: String = ""

    fun add(phoneBook: MutableList<Person>): Person? {
        if (this.isValid()) {
            val person = findByNamePerson(phoneBook, comArr[1])
            if (person != null) {
                val result: String = addParamToPerson(person, parameterName, comArr[2])
                if (result == KEY_FOR_SUCCESS_ADD_PARAMETER) return person
                else showErrorMsg = result
            } else showErrorMsg = FIND_ERROR_MESSAGE
        } else showErrorMsg = ERROR_COUNT_COMMANDS

        return null
    }

    override fun isValid(): Boolean {
        if (comArr.size == COUNT_COMMANDS_FOR_ADD_PARAMETER) return true
        return false
    }
}

class AddPhone(comArr: List<String>) : AddEmail(comArr) {
    override var parameterName = "phone"
}

class FindByParam(private val comArr: List<String>) : Command {
    var showErrorMsg: String = ""

    fun find(phoneBook: MutableList<Person>): List<Person>? {
        if (this.isValid()) {
            val findResult: MutableList<Person> = mutableListOf()
            var findParam: String? = null

            if (parseMail(comArr[1])) findParam = "email"
            else if (parsePhone(comArr[1])) findParam = "phone"

            for (person in phoneBook) {
                val result = when (findParam) {
                    "email" -> person.email.find { it == comArr[1] }
                    "phone" -> person.phone.find { it == comArr[1] }
                    else -> null
                }
                if (result != null) findResult.add(person)
            }
            if (findResult.size > 0) return findResult
            else showErrorMsg = FIND_ERROR_MESSAGE
        } else showErrorMsg = ERROR_COUNT_COMMANDS

        return null
    }


    override fun isValid(): Boolean {
        if (comArr.size == COUNT_COMMANDS_FOR_FIND) return true
        return false
    }
}

fun addParamToPerson(person: Person, parameterName: String, parameterValue: String): String {
    var result = KEY_FOR_SUCCESS_ADD_PARAMETER

    when (parameterName) {
        "phone" -> {
            if (!parsePhone(parameterValue)) result = ERROR_PHONE_FORMAT
            else person.phone.add(parameterValue.replace("+", ""))
        }
        "email" -> {
            if (!parseMail(parameterValue)) result = ERROR_EMAIL_FORMAT
            else person.email.add(parameterValue)
        }
        else -> result = "$ERROR_UNKNOWN_PARAMETER $parameterName"
    }
    return result
}

fun findByNamePerson(phoneBook: MutableList<Person>, searchValue: String): Person? {
    for (record in phoneBook) {
        if (record.name == searchValue) return record
    }
    return null
}

fun readCommand(strCommand: String?): Command? {
    val comArr = strCommand?.split(" ") ?: return null

    return when (comArr[0]) {
        "add" -> AddCommand(comArr)
        "show" -> ShowCommand(comArr)
        "AddPhone" -> AddPhone(comArr)
        "AddEmail" -> AddEmail(comArr)
        "find" -> FindByParam(comArr)
        "help" -> HelpCommand
        "exit" -> ExitCommand
        else -> null
    }

}

fun main() {
    val phoneBook: MutableList<Person> = mutableListOf()
    var com: String?
    var person: Person? = null
    while (true) {
        print("Введите команду: ")
        com = readlnOrNull()
        when (val resultCommand = readCommand(com)) {
            is ExitCommand -> return
            is HelpCommand -> println(resultCommand)
            is ShowCommand -> {
                person = resultCommand.showRecord(phoneBook)
                if (person != null) {
                    println("Email:" + person.email)
                    println("Phone:" + person.phone)
                } else println(resultCommand.showErrorMsg)
            }
            is AddCommand -> {
                person = resultCommand.add(phoneBook)
                if (person != null) {
                    phoneBook.add(person)
                    println(phoneBook)
                } else {
                    println(ADD_PERSON_ERROR_MESSAGE + " (" + resultCommand.addErrorMsg + ")" + HelpCommand)
                }
            }
            is AddPhone -> {
                person = resultCommand.add(phoneBook)
                println(person ?: resultCommand.showErrorMsg)
            }
            is AddEmail -> {
                person = resultCommand.add(phoneBook)
                println(person ?: resultCommand.showErrorMsg)
            }
            is FindByParam -> {
                val foundPersons: List<Person>? = resultCommand.find(phoneBook)
                println(foundPersons ?: resultCommand.showErrorMsg)
            }
            null -> println(ERROR_MESSAGE + HelpCommand)
        }
    }
}