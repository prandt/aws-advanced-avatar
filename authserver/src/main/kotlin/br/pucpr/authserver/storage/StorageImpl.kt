package br.pucpr.authserver.storage

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import org.springframework.stereotype.Service

import java.io.InputStream

@Service
class StorageImpl(private val amazonS3: AmazonS3): IStorage {

    override fun uploadFile(file: InputStream, bucketName: String, fileName: String, contentType: String): String {
        val metadata = ObjectMetadata().apply {
            this.contentType = contentType
        }
        amazonS3.putObject(bucketName, fileName, file, metadata)
        return amazonS3.getUrl(bucketName, fileName).toString()
    }

}