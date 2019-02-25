------------------
Manuela Weilharter 
0826002
Master Thesis - Automatic Tourist Guide
------------------

BACKEND 
Java Web Application


Deployed at the free cloud application plattform Heroku:
http://atg-gatherer.herokuapp.com/

Deploy local:
Prerequisites
- Maven
- Postgresql (User: atg_user, PW: atg_pw, Database: pois)


1. CREATE USER atg_user WITH PASSWORD 'atg_pw';
2. CREATE DATABASE pois OWNER atg_user;
// GRANT ALL PRIVILEGES ON DATABASE pois to atg_user;
3. SET SCHEMA 'pois';

4. Download https://www.dropbox.com/s/z1l5ao3nfzxsshk/s_g_l_a.dump?dl=1 and insert the complete database dump of all necessary tables and the pois for the four countries: Liechtenstein, Germany, Austria, Swiss from using the following command:
psql.exe pois < s_g_l_a.dump atg_user

5. mvn exex:java

6. Browse to http://localhost:8080/ and login with username: admin and password: master!thesis