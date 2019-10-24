package com.mitchmele.algorithmcloudprocessor.services

import com.mitchmele.algorithmcloudprocessor.mongodb.AlgorithmMongoRepository
import com.mitchmele.algorithmcloudprocessor.result.*
import com.mitchmele.algorithmcloudprocessor.store.AlgorithmClient
import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModel
import org.springframework.stereotype.Service
import java.lang.Exception


@Service
class MongoClient(
    private val mongoRepository: AlgorithmMongoRepository
) : AlgorithmClient {
    override fun saveAlgorithm(algorithm: AlgorithmDomainModel): Result<Unit, ServiceErrors> {
        return try {
            mongoRepository.save(algorithm)
            Success(Unit)
        } catch (ex: Exception) {
            Failure(serviceErrorOf(ServiceError(
                service = ServiceName.MONGO,
                errorMessage = ex.localizedMessage,
                errorType = ErrorType.INPUT_VALIDATION
            )))
        }
    }

    override fun findAlgorithmByName(algorithmName: String): Result<AlgorithmDomainModel, ServiceErrors> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAlgorithmByName(algorithmName: String): Result<Unit, ServiceErrors> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}