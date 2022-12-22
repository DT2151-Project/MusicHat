package furhatos.app.musichatskill

import java.io.*
import java.net.URL
import java.nio.file.Files
import javax.sound.sampled.*


object Util {

    fun convertURLtoWAV(url: String, fileName: String) {
        val conn = URL(url).openConnection()

        val inputStream = conn.getInputStream()
        val outStream = Files.newOutputStream(File("data/$fileName.wav").toPath())

        val targetFormat = AudioFormat(AudioFormat.Encoding.PCM_SIGNED,16000.0F,16, 1,2,16000.0F,false)

        Converter.convertFrom(inputStream).withTargetFormat(targetFormat).to(outStream)
    }

    fun filePathToURL(fileName: String): URL? {
        val file = File("data/$fileName.wav")
        return file.toURI().toURL()
    }
}