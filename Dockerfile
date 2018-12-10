FROM openjdk:8 as ilpjar

WORKDIR /usr/src/app

COPY . .

RUN ./gradlew shadowJar

# adapted from https://github.com/justb4/docker-jmeter
FROM alpine:3.8

WORKDIR /usr/src/app
ARG JMETER_VERSION="4.0"
ENV JMETER_HOME /opt/apache-jmeter-${JMETER_VERSION}
ENV	JMETER_BIN	${JMETER_HOME}/bin
ENV	JMETER_DOWNLOAD_URL  https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-${JMETER_VERSION}.tgz

# Install extra packages
# See https://github.com/gliderlabs/docker-alpine/issues/136#issuecomment-272703023
# Change TimeZone TODO: TZ still is not set!
ARG TZ="Europe/Amsterdam"
RUN    apk update \
	&& apk upgrade \
	&& apk add ca-certificates \
	&& update-ca-certificates \
	&& apk add --update openjdk8-jre tzdata curl unzip bash \
	&& rm -rf /var/cache/apk/* \
	&& mkdir -p /tmp/dependencies  \
	&& curl -L --silent ${JMETER_DOWNLOAD_URL} >  /tmp/dependencies/apache-jmeter-${JMETER_VERSION}.tgz  \
	&& mkdir -p /opt  \
	&& tar -xzf /tmp/dependencies/apache-jmeter-${JMETER_VERSION}.tgz -C /opt  \
	&& rm -rf /tmp/dependencies

COPY --from=ilpjar /usr/src/app/build/libs/JMeterIlpSamplers-all.jar $JMETER_HOME/lib/ext/.

# Set global PATH such that "jmeter" command is found
ENV PATH $PATH:$JMETER_BIN

# Entrypoint has same signature as "jmeter" command
COPY entrypoint.sh /
COPY scripts/wait-for-it.sh /
RUN chmod +x /entrypoint.sh
RUN chmod +x /wait-for-it.sh

WORKDIR	${JMETER_HOME}

ENTRYPOINT ["/entrypoint.sh"]