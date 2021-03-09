[![Build Status](https://travis-ci.com/Fhurup/CA3_startcode.svg?branch=master)](https://travis-ci.com/Fhurup/CA3_startcode)



First of all, you need to go to, the pom.xml file, and edit the remote ser URl to your own URl you will use to the project

After you need to ssh into your droplet and nano to docker-compose.yml file and rather change or make a new connection string (CONNECTION_STR: "jdbc:mysql://db:3306/db_name")

After you have done that, you need to type following commands:

(docker-compose down
docker-compose build
docker-compose up -d)

When you have done that, you go to travis and find the repo you are working in and, change the environment variables to your tomcat user where you use the following commands ( "REMOTE_USER : XXXXXX", "REMOTE_PW : XXXXXXX" )

When you make a commit through github, travis deploy it to your tomcat

You can see the orginal guide here: https://docs.google.com/document/d/1K6s6Tt65bzB8bCSE_NUE8alJrLRNTKCwax3GEm4OjOE/edit
 
