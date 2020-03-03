package com.mitchmele.algorithmcloudprocessor.services

import com.mitchmele.algorithmcloudprocessor.common.flatMap
import com.mitchmele.algorithmcloudprocessor.common.mapSuccess
import com.mitchmele.algorithmcloudprocessor.common.succeeds
import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModel
import org.springframework.stereotype.Service

@Service
class MongoValidationService(
    private val mongoClient: MongoClient
) {
    //make generic interface for validator
    fun validate(algorithm: AlgorithmDomainModel): Boolean {
        val listOfNames =
            mongoClient.loadAllAlgorithms()
                .mapSuccess { algorithms ->
                    algorithms.map { it.name.toLowerCase() }
                }.succeeds()

        return when {
            !listOfNames.contains(algorithm.name.toLowerCase()) -> true
            else -> false
        }
    }
}