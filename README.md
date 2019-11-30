# spring-demo
Spring boot demo for web layer and integration testing

To build Gradle project from the source directory use:
`./gradlew clean build --info`

To run unit tests:
`./gradlew clean test --info`

To run integration tests:
`./gradlew clean integration --info`

To run the application:
`./gradlew bootRun` it will start on the default 8080 port

<b>Are you up??</b> - <a>http://localhost:8080/actuator/health</a> <br>

<b>Wanna look at some tables??</b> - <a>http://localhost:8080/h2-console/</a> <br>
User=<i>admin</i>   Password=<i>admin</i>
<br>

<b>Got Swag??</b> - <a>http://localhost:8080/swagger-ui.html</a>