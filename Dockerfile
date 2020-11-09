FROM tomcat:10.0-jdk11
ADD target/*.war /usr/local/tomcat/webapps/
EXPOSE 80
CMD ["catalina.sh", "run"]
