package br.pucpr.authserver.storage

import java.io.InputStream

interface IStorage {
    fun uploadFile(file: InputStream, bucketName: String, fileName: String, contentType: String): String
}