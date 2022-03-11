package cryptography
import java.awt.image.BufferedImage
import java.io.File
import java.util.Scanner
import javax.imageio.IIOException
import javax.imageio.ImageIO

const val END_MSG = "000000000000000000000011"
val scanner = Scanner(System.`in`)

fun main() {
    println("Task (hide, show, exit):")
    while (scanner.hasNext()){
        when (scanner.next()) {
            "hide" -> hide()
            "show" -> show()
            "exit" -> { println("Bye!"); break }
            else -> println("Wrong task: $scanner")
        }
    }
}

fun hide() {
    println("Input image file: ")
    val inputName = readLine()!!
    println("Output image file: ")
    val outputName = readLine()!!
    println("Message to hide: ")
    val message = readLine()!!
    println("Password:")
    val password = readLine()!!
    val encryptedMessage = convertToArray(message, password)
    try {
        val imageFile = File(inputName)
        val image: BufferedImage = ImageIO.read(imageFile)

        if (encryptedMessage.length < image.width * image.height) {
            var i = 0
            loop@ for (y in 0 until image.height) {
                for (x in 0 until image.width) {
                    if (i == encryptedMessage.length) break@loop

                    if (encryptedMessage[i] == '0') {
                        image.setRGB(x, y, image.getRGB(x, y).and(0xFFFFFE))
                    } else if (encryptedMessage[i] == '1'){
                        image.setRGB(x, y, image.getRGB(x, y).or(0x1))
                    }

                    i++
                }
            }

            val outputFile = File(outputName)
            ImageIO.write(image, "png", outputFile)

            println("Message saved in ${outputFile.name} image.")
            println("Task (hide, show, exit):")
        } else {
            println("The input image is not large enough to hold this message.")
            println("Task (hide, show, exit):")
        }
    } catch (e: IIOException) {
        println("Can't read input file!")
    }
}

fun convertToArray(msg: String, pswrd: String): String {
    var message = ""
    for (ch in msg) {
        val temp = ch.code.toString(2)
        message += temp.padStart(8, '0')
    }
    var password = ""
    for (ch in pswrd) {
        val temp = ch.code.toString(2)
        password += temp.padStart(8, '0')
    }
    var passwordLikeMessage = ""
    while (passwordLikeMessage.lastIndex < message.lastIndex) {
        for (i in password) {
            passwordLikeMessage += i
        }
    }
    var encryptedMessage = ""
    for (i in message.indices) {
        encryptedMessage += (message[i].toString().toInt() xor passwordLikeMessage[i].toString().toInt()).toString()
    }
    return encryptedMessage + END_MSG
}


fun convertToMassage(message: String, pswrd: String): String {
    var password = ""
    for (ch in pswrd) {
        val temp = ch.code.toString(2)
        password += temp.padStart(8, '0')
    }
    var passwordLikeMessage = ""
    while (passwordLikeMessage.lastIndex < message.lastIndex) {
        for (i in password) {
            passwordLikeMessage += i
        }
    }
    var encryptedMessage = ""
    for (i in message.indices) {
        encryptedMessage += (message[i].toString().toInt() xor passwordLikeMessage[i].toString().toInt()).toString()
    }
    return encryptedMessage
}


fun show() {
    println("Input image file: ")
    val inputName = readLine()!!
    try {
        val imageFile = File(inputName)
        val image: BufferedImage = ImageIO.read(imageFile)
        println("Password:")
        val password = readLine()!!
        var message = ""
        var letter = ""
        loop@ for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                letter += image.getRGB(x, y).and(1).toString()

                if (letter.length == 8) {
                    message += letter

                    letter = ""

                    if (message.contains(END_MSG)) {
                        message.replace(END_MSG, "")
                        break@loop
                    }
                }
            }
        }
        val decryptedMessage = convertToMassage(message, password)
        println("Message:")
        println(decryptedMessage.chunked(8).map { it.toInt(2).toChar() }.joinToString(""))
        println("Task (hide, show, exit):")
    } catch (e: IIOException) {
        println("Can't read input file!")
    }
}
