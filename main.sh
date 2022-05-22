# Just trying to fix the sqlite database. Was not an issue locally with intelliJ
mvn clean compile package

mvn dependency:build-classpath

cd target/

java -jar maven-shopify-application-jar-with-dependencies.jar
