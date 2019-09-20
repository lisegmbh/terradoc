FROM gradle:5.6.2-jdk8 AS build
ENV TER_VER 0.12.9

RUN apt-get update && apt-get install -y wget
RUN wget https://releases.hashicorp.com/terraform/${TER_VER}/terraform_${TER_VER}_linux_amd64.zip
RUN unzip terraform_${TER_VER}_linux_amd64.zip
RUN mv terraform /usr/local/bin/

WORKDIR /build
COPY . .
RUN gradle build installDist

FROM openjdk:8u212-jre-alpine AS runtime
COPY --from=build /usr/local/bin/terraform /usr/local/bin/
COPY --from=build /build/terradoc/build/install/ /opt/
COPY --from=build /build/terradoc/build/distributions/  /opt/terradoc/
RUN ln -s /opt/terradoc/bin/terradoc /usr/local/bin/terradoc