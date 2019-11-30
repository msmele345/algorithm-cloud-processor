# Algorithm-Cloud-Processor

##What is it?
This application uses Spring-Integration and Spring-Cloud-Streams to showcase EIP message processing. 
Messages are produced and put on a Kafka queue in another producer app (See Algorithm-Producer in github).

##What does it do?
This application consumes messages from another Spring-boot app in my repo (Algorithms-Producer) via a kafka broker and performs several processing actions through Spring-Integration endpoints (components)
##What is the end result?
The messages are consumed and then transformed before finally being written to a Mongo Db Instance. Another Spring-Boot app in my repo (Algos-Api) can be used as an endpoint to retrieve the data

##Spring-Integration Components
- Transformer - Transforms the BaseAlgorithm from the static file into the Domain version of the Object
- MessageHandler - Processes incoming messages and writes them to MongoDb container via an outbound adapter. 
- ErrorAdvice  - handles exceptions and errorHandling from all of the components.
- If the errorAdvice processes a bad message, then errors are routed to a rabbitMq error exchange that is bound to a rabbitMQ Docker instance.
 
##Requirements
1. Java 8
2. Docker
- The project uses Gradle for it's build system, https://gradle.org/
- Go to the root project folder
- Run ./gradlew clean build to build the app
- Jars are produced in the algorithm-cloud-processor/build/libs/directory

##Local Setup
Go to the algorithm-cloud-processor director
cd Docker
Run docker-compose up -d to start Rabbit, Kafka, Mongo Db and Zookeeper services 
Run ./gradlew bootRun to start the app
The app will be running and ready to accept messages from Kafka 

##Setup Docker
Run docker-compose up to spin up Zookeeper, Kafka, MongoDb, and RabbitMQ
If there are issues with docker-compose, there is a start_docker.sh script in the same directory 
This will prune any stale containers before running compose and should solve the issue

##Useful Docker commands:
1. docker ps -a to list containers
2. docker stop containerid to stop running container
3. docker rm containerid to remove a non-running container
4. Cleanup docker container space execute docker volume rm $(docker volume ls -qf dangling=true)

##MongoDb CLI Access and Commands: 
1. docker exec -it mongodb-1 sh
2. mongo 
3. use algorithmDomainModels
4. db.algorithmDomainModels.find() - shows all current records
5. Delete Duplicates:
    -db.algorithmDomainModels.find({}, {myCustomKey:1}).sort({_id:1}).forEach(function(doc){
         db.myCollection.remove({_id:{$gt:doc._id}, myCustomKey:doc.myCustomKey});
     })