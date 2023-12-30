# Due to Oracle being Oracle, the Java-Docker situation is tricky.
# The formerly official "openjdk" image has been deprecated, and users
# are urged to consider alternatives.  Among others, these are
#
# - https://hub.docker.com/_/amazoncorretto
# - https://hub.docker.com/_/eclipse-temurin
# - https://hub.docker.com/_/ibmjava
#
# The following file decides to go with the first, but it might be
# worthwhile to investigate the alternatives as well.
FROM docker.io/library/amazoncorretto:19-alpine-jdk

# This will be our working directory within the container.
WORKDIR /usr/src/app

# Copy everything from this actual directory into the working
# directory within the container.
COPY . .

# A quick and dirty command to compile all .java files in the container.
RUN find . -name "*.java" -print0 | xargs -0 javac -cp .:jkgp.jar

# Start the JVM and use Agent as the main class.  No further arguments
# are necessary, we assume the client connects via TCP to
# localhost:2761.
CMD ["java", "-Xms128m", "-Xmx512m", "-cp", ".:jkgp.jar", "Agent"]
