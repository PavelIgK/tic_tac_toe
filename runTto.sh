export JAVA_HOME=`/usr/libexec/java_home -v 17.0.8`
mvn clean package
docker-compose down
#docker volume rm $(docker volume ls -q --filter dangling=true)
docker rmi $( docker image ls --format '{{.Repository}}:{{.Tag}}' | grep '^tic_tac_toe')
docker-compose -f docker-compose.yml up