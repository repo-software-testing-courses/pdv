### Run mysql using docker
```
docker run --rm -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=TrackOrJargh -d mysql:5.7.21 mysqld --lower_case_table_names=1
```
