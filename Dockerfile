# Install LTS version of Java
FROM adoptopenjdk:11-jdk-hotspot

# Create directory for app
RUN mkdir /app

# Set as current directory for RUN, ADD, COPY commands
WORKDIR /app

# Add wrapper files
COPY gradle ./gradle
COPY gradlew* ./

# Add Gradle configuration from upstream
COPY build.gradle ./
COPY settings.gradle ./

# Install dependencies
RUN { \
    echo; \
    echo 'task fetchDependencies { description "Pre-downloads *most* dependencies"'; \
    echo 'doLast { configurations.getAsMap().each { name, config ->'; \
    echo 'print "Fetching dependencies for $name..."'; \
    echo 'try { config.files; println "done" }'; \
    echo 'catch (e) { println ""; project.logger.info e.message; }'; \
    echo '} } }'; \
    echo; \
} >>build.gradle

RUN ./gradlew --no-daemon clean fetchDependencies

# Add entire student fork (overwrites previously added files)
ARG SUBMISSION_SUBFOLDER
COPY $SUBMISSION_SUBFOLDER ./

# Overwrite files in student fork with upstream files
COPY assessment ./assessment
COPY test.sh ./
COPY build.gradle ./
