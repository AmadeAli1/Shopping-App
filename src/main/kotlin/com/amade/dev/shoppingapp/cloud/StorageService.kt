package com.amade.dev.shoppingapp.cloud

import com.amade.dev.shoppingapp.exception.ApiException
import com.google.cloud.Identity
import com.google.cloud.Policy
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageRoles
import com.google.common.net.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import java.util.*


@Service
class StorageService(
    private val storage: Storage,
    environment: Environment,
) {
    private val bucketName: String? = environment["parent.bucket.name"]

    init {
        val originalPolicy: Policy = storage.getIamPolicy(bucketName)
        val build = originalPolicy.toBuilder()
            .addIdentity(StorageRoles.objectViewer(), Identity.allUsers()).build()
        storage.setIamPolicy(bucketName, build)
    }

    suspend fun save(file: FilePart, subDirectory: String): String {
        val filePath = "$subDirectory/${UUID.randomUUID()}"
        val blobId = BlobId.of(bucketName, filePath)
        val blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(MediaType.ANY_IMAGE_TYPE.type())
            .setBlobId(blobId)
            .build()
        try {
            val blob = storage.create(blobInfo, file.toByteArray())
            if (blob != null) return filePath else throw ApiException("Failure in upload this file ${file.name()}")
        } catch (e: Exception) {
            throw ApiException(e.message)
        }
    }


    //DataBufferUtils.join(file.content()).map { dataBuffer -> dataBuffer.asByteBuffer().array() }.subscribe {
    //                storage.create(blobInfo, it)
    //            }
    private suspend fun FilePart.toByteArray() = withContext(Dispatchers.IO) {
        DataBufferUtils.join(this@toByteArray.content()).map { it.asByteBuffer().array() }.block()!!
    }

    suspend fun delete(filePath: String): Boolean {
        val blobId = storage.get(BlobId.of(bucketName, filePath)).blobId
        if (blobId != null) {
            return storage.delete(blobId)
        }
        throw ApiException("Image not found!")
    }

    suspend fun update(filePath: String, file: FilePart) {
        val blobId = storage.get(BlobId.of(bucketName, filePath)).blobId
        if (blobId != null) {
            val blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(MediaType.ANY_IMAGE_TYPE.type())
                .setBlobId(blobId)
                .build()
            storage.create(blobInfo, file.toByteArray())
        }
    }


}