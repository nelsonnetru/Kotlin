package p01

import java.io.File

/*
add Tom email tommy@gmail.com
add Mike email mike@gmail.com
AddEmail Mike mike2@mail.ru
AddEmail Tom tom@mail.ru
AddPhone Tom 789456
AddPhone Tom 5552233
AddPhone Mike 123456789
show Mike
show Tom
find 789456
*/

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
const val COUNT_COMMANDS_FOR_EXPORT: Int = 2
const val ADD_PERSON_ERROR_MESSAGE: String = "Ошибка: не удалось добавить персону"
const val ADD_PERSON_ERROR_MESSAGE_DUPLICATE: String = "Запись с таким именем уже добавлена"
const val ERROR_BOOK_IS_EMPTY = "Коллекция контактов пуста"
const val EXPORT_FILE_PATH = "src/main/resources/"
const val EXPORT_FILE_EXTENSION = ".json"
const val EXPORT_SUCCESS_MESSAGE = "Данные успешно экспортированы в файл "


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

    fun add(phoneBook: MutableMap<String, Person>): Person? {
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
• find <email> поиск по email
• find <телефон> поиск по телефону
• export <Путь и название файла>
После выполнения команды, кроме команды exit, программа ждёт следующую команду.
""".trimIndent()
}

object ExitCommand : Command {
    override fun isValid(): Boolean = true
}

class ShowCommand(private val comArr: List<String>) : Command {
    var showErrorMsg: String = ""

    fun showRecord(phoneBook: MutableMap<String, Person>): Person? {
        if (this.isValid()) {
            for (record in phoneBook) {
                if (record.value.name == comArr[1]) return record.value
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

    fun add(phoneBook: MutableMap<String, Person>): Person? {
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

class ExportCommand(private val comArr: List<String>): Command  {
    var showErrorMsg: String = ""
    var showResultMessageExport: String = ""

    fun exportToJSON (phoneBook: MutableMap<String, Person>) : Boolean {
        if (this.isValid()) {
            if (phoneBook.isNotEmpty()) {
                val jsonObjects = phoneBook.values.map { person ->
                    json {
                        addProperty("name", person.name)
                        addProperty("phone", person.phone)
                        addProperty("email", person.email)
                    }
                }
                val jsonFormat = "[${jsonObjects.joinToString(", ")}]"
                File(EXPORT_FILE_PATH + comArr[1] + EXPORT_FILE_EXTENSION).writeText(jsonFormat)
                showResultMessageExport = EXPORT_SUCCESS_MESSAGE + EXPORT_FILE_PATH + comArr[1] + EXPORT_FILE_EXTENSION
                return true
            } else showErrorMsg = ERROR_BOOK_IS_EMPTY
        } else showErrorMsg = ERROR_COUNT_COMMANDS

        return false
    }

    override fun isValid(): Boolean {
        if (comArr.size == COUNT_COMMANDS_FOR_EXPORT) return true
        return false
    }
}

class FindByParam(private val comArr: List<String>) : Command {
    var showErrorMsg: String = ""

    fun find(phoneBook: MutableMap<String, Person>): Map<String, Person>? {
        if (this.isValid()) {
            var findResult:Map<String, Person> = mapOf()

            if (parseMail(comArr[1])) findResult = phoneBook.filter { (_, v) -> v.email.contains(comArr[1]) }
            else if (parsePhone(comArr[1])) findResult = phoneBook.filter { (_, v) -> v.phone.contains(comArr[1]) }

            if (findResult.isNotEmpty()) return findResult
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

fun findByNamePerson(phoneBook: MutableMap<String, Person>, searchValue: String): Person? {
    for (record in phoneBook) {
        if (record.value.name == searchValue) return record.value
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
        "export" -> ExportCommand(comArr)
        else -> null
    }

}

class JsonObject {
    private val map = mutableMapOf<String, Any>()

    fun addProperty(key: String, value: Any) {
        map[key] = value
    }

    override fun toString(): String {
        val properties = map.entries.joinToString(",\n    ") { (key, value) ->
            "\"$key\": ${if (value is String) "\"$value\"" else value}"
        }
        return "{\n\t$properties\n}"
    }
}

fun json(init: JsonObject.() -> Unit): JsonObject {
    return JsonObject().apply(init)
}

fun main() {
    val phoneBook: MutableMap<String, Person> = mutableMapOf()
    var com: String?
    var person: Person?
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
                    phoneBook[person.name] = person
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
                val foundPersons: Map<String, Person>? = resultCommand.find(phoneBook)
                println(foundPersons ?: resultCommand.showErrorMsg)
            }
            is ExportCommand -> {
                if (resultCommand.exportToJSON(phoneBook)) println(resultCommand.showResultMessageExport)
                else println(resultCommand.showErrorMsg)
            }
            null -> println(ERROR_MESSAGE + HelpCommand)
        }
    }
}