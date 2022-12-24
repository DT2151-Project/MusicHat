package furhatos.app.musichatskill

import java.io.*
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem


class Converter constructor(private val input: InputStream) {
    private var audioFormat: AudioFormat? = null
    fun withTargetFormat(targetAudioFormat: AudioFormat?): Converter {
        audioFormat = targetAudioFormat
        return this
    }

    fun to(output: OutputStream?) {
        try {
            ByteArrayOutputStream().use { rawOutputStream ->
                convert(input, rawOutputStream, targetFormat)
                val rawResult = rawOutputStream.toByteArray()
                val audioInputStream = AudioInputStream(ByteArrayInputStream(rawResult), targetFormat, rawResult.size.toLong())
                AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, output)
            }
        } catch (e: Exception) {
            throw e
        } finally {
            input.close()
        }
    }

    private fun convert(input: InputStream, output: OutputStream, targetFormat: AudioFormat) {
        AudioSystem.getAudioInputStream(input).use { rawSourceStream ->
            val sourceFormat = rawSourceStream.format
            val convertFormat = getAudioFormat(sourceFormat)
            AudioSystem.getAudioInputStream(convertFormat, rawSourceStream).use { sourceStream ->
                        AudioSystem.getAudioInputStream(targetFormat, sourceStream).use { convertStream ->
                            var read: Int
                            val buffer = ByteArray(8192)
                            while (convertStream.read(buffer, 0, buffer.size).also { read = it } >= 0) {
                                output.write(buffer, 0, read)
                            }
                        }
                    }
        }
    }

    private val targetFormat: AudioFormat
        private get() = if (audioFormat == null) AudioFormat(44100f, 8, 1, true, false) else audioFormat!!

    private fun getAudioFormat(sourceFormat: AudioFormat): AudioFormat {
        return AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sourceFormat.sampleRate,16, sourceFormat.channels,sourceFormat.channels * 2, sourceFormat.sampleRate,false)
    }

    companion object {
        fun convertFrom(input: InputStream): Converter {
            return Converter(input)
        }
    }
}