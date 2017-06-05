# jbcnconf-2017-health-care
Health Care Demo for JBCN Conf 2017


Demo Script
* We show a Domain model: Patient, Observation, Procedure, Emergency 
	* project: model (it will be great if we can use FHIR as our model, it is not very DDD like)
* We show a business model (quickly describe it and show its value)
	* Simple decision: that defines how to categorise Patients (high risk/low risk)
	* Simple process: that defines how to treat incoming emergencies
	* Both business models emit events
* We show a couple of services:
	* Emergency Service
		* project: emergency-service
		* dependencies: activiti-engine
	* Patient Medical Records Service
		* project: patient-records-service
		* dependencies: pure crud
* We show a Front End just to have a way to interact, this will require
	* API Gateway
	* Spring Message Bus
	* Spring Data Flow 
		* We need to create sources/processors/sinks
*  Approach 1: Emergency Service will use the models by having embedded engines	 
* (Approach 2: Incoming Emergency Service will use generated code)
* (Optional) We show some kind of Business Value driven monitoring (should we use KIBANA??? ) 



# Requirements

We are using Spring Data Flow to connect Sources, Processors and Sinks together via Streams
We are also using Docker compose in order to start some providers such as RabbitMQ and Redis

### Start Docker Compose
cd docker/
docker-compose up -d


### Download Spring Data Flow
curl -O http://repo.spring.io/snapshot/org/springframework/cloud/spring-cloud-dataflow-server-local/1.2.2.BUILD-SNAPSHOT/spring-cloud-dataflow-server-local-1.2.2.BUILD-20170601.204606-1.jar
java -jar spring-cloud-dataflow-server-local-1.2.2.BUILD-20170601.204606-1.jar
### Start the Server
java -jar spring-cloud-dataflow-server-local-1.2.1.RELEASE.jar

### Download and start the Data flow shell
curl -O http://repo.spring.io/snapshot/org/springframework/cloud/spring-cloud-dataflow-shell/1.2.2.BUILD-SNAPSHOT/spring-cloud-dataflow-shell-1.2.2.BUILD-20170601.204606-1.jar
java -jar spring-cloud-dataflow-shell-1.2.2.BUILD-20170601.204606-1.jar

### Flo -> UI
If the Data Flow server is started you can access to the UI by pointing the Browser to: http://localhost:9393/dashboad

### Registering Apps and configuring & deploying the stream definition

app register --name time-source --type source --uri maven://com.example:emergency-source:jar:0.0.1-SNAPSHOT

app register --name time-processor --type processor --uri maven://com.example:emergency-processor:jar:0.0.1-SNAPSHOT

app register --name logging-sink --type sink --uri maven://com.example:emergency-sink:jar:0.0.1-SNAPSHOT

stream create --name time-to-log --definition 'time-source | time-processor | logging-sink'

stream deploy --name time-to-log


### Creating a new emergency
The Emergency Source project expose a REST endpoint where you can send new Emergencies. 
These emergencies will be propageted to the Emergency Processor which will start a Process for each of these emergencies. 
All the events emited by the Processor will be sent to the Emergency Sink.

In order to create a new emergency you need to look at the Emergency Source deployed app in the Dashboard:  http://localhost:9393/dashboad
and send a POST request to http://localhost:<look for the assigned port>/api/emergency/
With the following body: 

 {
	"date": "1496648974639",
	"location": {
		"longitude": 2.0,
		"latitude": 1.0
	}
 }

Alternatively, you can use the Chrome (extension) POSTman collection that you can found in root directory. Notice that you will need to modify the ports.

