package br.pucpr.authserver.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class HashUtil {

    companion object {
        private fun hex(digest: ByteArray): String {
            val sb = StringBuilder()
            for (b in digest) {
                sb.append(String.format("%02x", b))
            }
            return sb.toString()
        }

        fun sha256hex(email: String): String {
            try {
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(email.toByteArray())
                return hex(digest)
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }
        }
    }
}