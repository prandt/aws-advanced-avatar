package br.pucpr.authserver.users

import br.pucpr.authserver.storage.IStorage
import br.pucpr.authserver.users.responses.GravatarProfileResponse
import br.pucpr.authserver.utils.HashUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.io.InputStream
import java.net.URI

@Service
class AvatarService(private val gravatarClient: WebClient, private val storage: IStorage) {

    fun getAvatarUrl(email: String, name: String): String {
        val avatarUrl = generateAvatarUrl(email, name)
        val file = downloadImage(avatarUrl)
        val finalUrl = uploadAvatar(file, email)
        log.info("Avatar for $email uploaded to $finalUrl")
        return finalUrl
    }

    private fun generateAvatarUrl(email: String, name: String): String {
        val gravatarResponse = gravatarClient
            .get()
            .uri("/profiles/${HashUtil.sha256hex(email)}")
            .retrieve()
            .bodyToMono(GravatarProfileResponse::class.java)
            .map {
                if (it.displayName.contains("impossiblychief")) return@map ""
                it.avatarUrl
            }
            .onErrorReturn("")
            .block()

        if (!gravatarResponse.isNullOrBlank()) return gravatarResponse

        val names = name.split(" ")
        return "https://ui-avatars.com/api/?background=random&name=${names.first()}${if (names.size > 1) "+${names.last()}" else ""}"
    }

    fun uploadAvatar(inputStream: InputStream, email: String) = storage.uploadFile(
            inputStream,
            "auth-richardprandt-avatar",
            "avatars/$email-avatar.jpg",
            "image/jpeg"
    )

    private fun downloadImage(image: String): InputStream {
        val url = URI(image).toURL()
        val connection = url.openConnection()
        return connection.getInputStream()
    }

    companion object {
        val log = LoggerFactory.getLogger(AvatarService::class.java)
    }
}