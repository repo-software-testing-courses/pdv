### Docker usage
Docker required for running this application. [Download here](https://docs.docker.com/install/#supported-platforms) 

The docker-compose.yml file uses a mysql-server image, sets up the environment with a root password and a table named trackorjargh. Then uses a script to wait for the database and executes the jar generated with create_image.ps1 and links both containers.

#### For Linux / MacOS

##### Starting the application
Executing the start_docker.sh script will do a dockler-compose down, execute create_image.sh and docker-compose up. No more config required.

The script create_image.sh builds the maven project into a jar file with a docker maven image.

Publish_image.sh publishes the image into the dockerhub repo.

#### For Windows

##### Starting the application
Executing the start_docker.ps1 script will do a dockler-compose down, execute create_image.sh and docker-compose up. No more config required.

The script create_image.ps1 builds the maven project into a jar file with a docker maven image.

Publish_image.ps1 publishes the image into the dockerhub repo.


