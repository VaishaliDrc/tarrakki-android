package com.tarrakki.api

import android.util.Base64
import com.tarrakki.App
import com.tarrakki.BuildConfig
import com.tarrakki.R
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AES {
    /**
     * AES encryption of string
     * @param data
     *@return Encrypted data in string form
     */
    @Throws(Exception::class)
    fun encrypt(data: String): String {

        val keyStart = App.INSTANCE.getString(R.string.key).toByteArray(StandardCharsets.UTF_8)
        val key = SecretKeySpec(keyStart, "AES")

        val IV = App.INSTANCE.getString(R.string.iv).toByteArray()
        val IVSpec = IvParameterSpec(IV)

        val cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding")
        val blockSize = cipher.blockSize

        val dataBytes = data.plus("*#$*").toByteArray(StandardCharsets.UTF_8)
        var plaintextLength = dataBytes.size
        if (plaintextLength % blockSize != 0) {
            plaintextLength += (blockSize - plaintextLength % blockSize)
        }

        val plaintext = ByteArray(plaintextLength)
        Arrays.fill(plaintext, 6)
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.size)

        cipher.init(Cipher.ENCRYPT_MODE, key, IVSpec)
        val encrypted = cipher.doFinal(plaintext)

        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    /**
     * AES dencryption of string
     * @param data
     *@return Dencrypted data in string form
     */
    fun decrypt(cipherText: String): String {
        val keyStart = App.INSTANCE.getString(R.string.key).toByteArray(StandardCharsets.UTF_8)
        val key = SecretKeySpec(keyStart, "AES")

        val IV = App.INSTANCE.getString(R.string.iv).toByteArray()
        val IVSpec = IvParameterSpec(IV)
        var result = ""
        try {
            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key, IVSpec)
            val decrypted = removeTrailingNulls(cipher.doFinal(Base64.decode(cipherText, Base64.NO_WRAP)))
            result = String(decrypted)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        }

        return result
    }

    private fun removeTrailingNulls(source: ByteArray): ByteArray {
        var i = source.size
        while (source[i - 1].toInt() == 0x00) {
            i--
        }

        val result = ByteArray(i)
        System.arraycopy(source, 0, result, 0, i)

        return result
    }
}