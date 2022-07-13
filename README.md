# Lilach
This is our final project for Software Engineering in the University of Haifa.

The system is splitted into two parts:

* **lilach-client:** Built with JavaFX
* **lilach-server** Built with MySQL

## Requirements
1. [JDK 17](https://openjdk.java.net/projects/jdk/17/)
2. [Maven](https://maven.apache.org/)
3. [MySQL](https://www.mysql.com/)

## Running the project
We are using Maven as our build system. So, in order to run the whole project you have three options:

1. **Running using IntelliJ IDEA**

   This can be achieved by using the `Server+Client` configuration.

2. **Running using two separate commands**

   Running Server: `mvn clean compile -pl lilach-server exec:exec -am`
   
   Running Client: `mvn clean compile -pl lilach-client exec:exec -am`

3. **Running using one command**
   
   Just run: `mvn -T2 clean compile exec:exec`
