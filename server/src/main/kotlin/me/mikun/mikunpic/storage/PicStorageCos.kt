package me.mikun.mikunpic.storage

import com.qcloud.cos.COSClient
import com.qcloud.cos.ClientConfig
import com.qcloud.cos.auth.BasicCOSCredentials
import com.qcloud.cos.auth.COSCredentials
import com.qcloud.cos.http.HttpProtocol
import com.qcloud.cos.model.Bucket
import com.qcloud.cos.model.CannedAccessControlList
import com.qcloud.cos.model.CreateBucketRequest
import com.qcloud.cos.model.GetObjectRequest
import com.qcloud.cos.model.ListObjectsRequest
import com.qcloud.cos.model.ObjectMetadata
import com.qcloud.cos.model.PutObjectRequest
import com.qcloud.cos.region.Region
import io.ktor.server.application.Application
import me.mikun.mikunpic.dto.data.api.OhMyRouting
import java.io.InputStream

class PicStorageCos : PicStorage() {
    private lateinit var cosClient: COSClient
    private lateinit var bucket: Bucket

    override fun init(application: Application) {
        with(application) {
            with(environment) {
                fun initClient() {
                    val cred: COSCredentials = BasicCOSCredentials(
                        config.property("storage.cos.secretId").getString(),
                        config.property("storage.cos.secretKey").getString(),
                    )
                    val region = Region(config.property("storage.cos.region").getString())
                    val clientConfig = ClientConfig(region)
                    clientConfig.httpProtocol = HttpProtocol.https

                    cosClient = COSClient(
                        cred,
                        clientConfig,
                    )
                }
                initClient()

                fun initBucket() {
                    val bucketName = config.property("storage.cos.bucket_name").getString()
                    bucket =
                        if (cosClient.doesBucketExist(bucketName)) {
                            cosClient.listBuckets().first { it.name == bucketName }
                        } else {
                            CreateBucketRequest(bucketName)
                                .apply {
                                    cannedAcl = CannedAccessControlList.PublicRead
                                }.let { request ->
                                    cosClient.createBucket(request)
                                }
                        }

                    var lastMarker = ""
                    while (true) {
                        ListObjectsRequest()
                            .apply {
                                this.bucketName = bucket.name
                                prefix = ""
                                maxKeys = 1000
                                marker = lastMarker
                            }.let { request ->
                                cosClient.listObjects(request)
                            }.let { objectListing ->
                                picKeys.addAll(
                                    objectListing.objectSummaries.map { it.key },
                                )
                                if (objectListing.nextMarker == null) break
                                lastMarker = objectListing.nextMarker
                            }
                    }
                }
                initBucket()
            }
        }
    }

    override suspend fun random(): InputStream? = GetObjectRequest(
        bucket.name,
        picKeys.random(),
    ).let { request ->
        cosClient.getObject(request)
    }.objectContent

    override suspend fun byName(
        name: String,
        thumbnail: OhMyRouting.Pic.Filename.Thumbnail,
    ): InputStream? {
        val reqKey = "$name${thumbnail.asParam()}"
        println(reqKey)
        return cosClient.getObject(
            GetObjectRequest(
                bucket.name,
                reqKey
            )
        ).objectContent
    }

    override suspend fun upload(
        byteArray: ByteArray,
        filename: String,
    ) {
        val metadata = ObjectMetadata().apply {
            contentLength = byteArray.size.toLong()
        }

        val request = PutObjectRequest(
            bucket.name,
            filename,
            byteArray.inputStream(),
            metadata,
        )

        cosClient.putObject(request)
    }

    private fun OhMyRouting.Pic.Filename.Thumbnail.asParam(): String {
        return when(this) {
            OhMyRouting.Pic.Filename.Thumbnail.Thumb -> "/thumb"
            OhMyRouting.Pic.Filename.Thumbnail.Small -> "/small"
            OhMyRouting.Pic.Filename.Thumbnail.Medium -> "/medium"
            OhMyRouting.Pic.Filename.Thumbnail.Large -> "/large"
            OhMyRouting.Pic.Filename.Thumbnail.Orig -> ""
        }
    }
}
