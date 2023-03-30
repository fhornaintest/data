cd code-with-quarkus
quarkus create --extension='camel-quarkus-microprofile-health, camel-quarkus-log, camel-quarkus-timer, camel-quarkus-jdbc, quarkus-jdbc-mariadb, quarkus-agroal, camel-quarkus-xml-io-dsl'


quarkus build --native -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=podman 

ip addr show

podman build --layers=false -f ./src/main/docker/Containerfile -t quay.io/fhornain0/myquarkus/myoibapp .

podman run -i --rm -p 8080:8080 quay.io/fhornain0/myquarkus/myoibapp


