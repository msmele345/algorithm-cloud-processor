package com.mitchmele.algorithmcloudprocessor.mongodb

import com.mitchmele.algorithmcloudprocessor.store.AlgorithmDomainModel
import org.springframework.data.mongodb.repository.MongoRepository

interface AlgorithmMongoRepository : MongoRepository<AlgorithmDomainModel, String>