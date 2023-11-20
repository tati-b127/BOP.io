# IGABOP

Widget for Bill of Process management  

##  Пререквизиты
  * 3DEXPERIENCE platform V6R2021x
  * MxUpdate 0-90-0
  * Java 11
  * com.igatec:utilsspring:0.0.1-SNAPSHOT (см папку custom-dependencies)
  * widget-utils (см папку custom-dependencies)
  * gradle 5.2.1
  * npm 6.4.1
  * библиотеки DS (обозначены как $enoviaGroupId:*:$enoviaRelease)

##  Запуск
Для запуска приложения необходимо выполнить команду `npm run start` (клиентская часть) и запустить 
com.igatec.bop.BOPApplication.main (серверная часть)

##  Сборка
Дистрибутив приложения состоит из 2-х частей:   
  * исполняемого .jar файла, конфигурационных файлов application.yml и application-premise.yml 
  * набора конфигурационных файлов MxUpdate (папка dbschema)

Для сборки пакета igabop-1.0.0.jar необходимо последовательно выполнить|запустить:
 * `npm run deploy` (см. package.json)
 *  задачу gradle 'bootJar' (см. build.gradle) или 'jar' (для облегченной версии igabop-1.0.0.jar 
    без зависимостей от библиотек DS)

##  Установка
Установка состоит из 2 частей - установка самого приложения и установка кастомизации платформы 3DEXPERIENCE
1. Развернуть дистрибутив
2. Копировать с заменой файл igabop-1.0.0.jar в C:\Customizations\IGABOP
3. Изменить в соответствии с окружением платформы 3DEXPERIENCE файлы application.yml и application-premise.yml
4. Изменить файл C:\Apache24\conf\httpd.conf (или один из файлов, указанных в директивах include), добавив:
```conf
	<Location "/bop">
		RequestHeader set x-forwarded-proto "https"
		RequestHeader set x-forwarded-port "443"

		ProxyPass http://127.0.0.1:8002/bop
		ProxyPassReverse  http://127.0.0.1:8002/bop

		# Cross-origin resource sharing (CORS)
		SetEnvIf Origin "^http(s):\/\/(.+\.)?(igatec.com)(:\d{1,5})?$" origin_is=$0
		Header always set Access-Control-Allow-Origin %{origin_is}e env=origin_is
		Header set Access-Control-Allow-Credentials "true"
		Header set Access-Control-Allow-Methods "GET, POST, OPTIONS, HEAD, PUT, DELETE, PATCH"
		Header set Access-Control-Allow-Headers "accept,x-requested-method,origin,x-requested-with,x-request,cache-control,
		content-type,last-modified,x-utc-offset,SecurityContext,X-DS-CSRFTOKEN,keep-alive,DS-API-Version,DS-Client-Step-Name,
		DS-Request-ID,ENO_CSRF_TOKEN,SecurityToken,DS-Change-Authoring-Context,DS-Configuration-Authoring-Context"
		Header set Access-Control-Expose-Headers "DS-Request-ID,X-DS-CSRFTOKEN,X-3DComment-Has-Moderation-Rights"
		Header set Access-Control-Max-Age "600"
	</Location>
```
5. Запустить исполняемый файл igabop-1.0.0.jar (или перезапустить сервис IGABOP)
***
6. Запустить скрипт `C:\Customizations\IGABOP\dbschema\Install.mql`
7. Разместить в папках `C:\Customizations\customLibs` и `C:\PLM\3DSpace\win_b64\code\tomee\webapps\3dspace\WEB-INF\lib`:
   * igabop-0.0.1-SNAPSHOT.jar (облегченную версию файла (без зависимостей))
   * utilsspring-0.0.1-SNAPSHOT.jar
   * fop-2.2.jar
   * fop-pdf-images-2.3.jar
8. Перезапустить tomee сервиса 3dspace

##  Среда разработчика
**Адрес сервера разработки**: 192.168.3.36 (eno-3de-sw-d01.igatec.com)  
**Адрес тестового сервера**: 192.168.3.24 (3dspace-sw-m04.igatec.com - адрес сервиса 3dspace)  
Для корректной работы приложения локально необходимо в прокси-сервере 3DEXPERIENCE платформы настроить
перенаправление на машину разработчика (пример, 192.168.200.25) в файле /etc/httpd/conf/httpd.conf (или файлах, 
указанных в директивах include):
```conf
<Location "/bop">
	RequestHeader set x-forwarded-proto "https"
	RequestHeader set x-forwarded-port "443"
	#RequestHeader set x-forwarded-contextpath "/"

	ProxyPass http://192.168.200.25:8081/
	ProxyPassReverse  http://192.168.200.25:8081/

	# Cross-origin resource sharing (CORS)
	SetEnvIf Origin "^http(s):\/\/(.+\.)?(igatec.com)(:\d{1,5})?$" origin_is=$0
	Header always set Access-Control-Allow-Origin %{origin_is}e env=origin_is
	Header set Access-Control-Allow-Credentials "true"
	Header set Access-Control-Allow-Methods "GET, POST, OPTIONS, HEAD, PUT, DELETE, PATCH"
	Header set Access-Control-Allow-Headers "accept,x-requested-method,origin,x-requested-with,x-request,cache-control,content-type,last-modified,x-utc-offset,SecurityContext,X-DS-CSRFTOKEN,keep-alive,DS-API-Version,DS-Client-Step-Name,DS-Request-ID,ENO_CSRF_TOKEN,SecurityToken,DS-Change-Authoring-Context,DS-Configuration-Authoring-Context"
	Header set Access-Control-Expose-Headers "DS-Request-ID,X-DS-CSRFTOKEN,X-3DComment-Has-Moderation-Rights"
	Header set Access-Control-Max-Age "600"
</Location>

<Location "/bop/api">
	RequestHeader set x-forwarded-proto "https"
	RequestHeader set x-forwarded-port "443"
	#RequestHeader set x-forwarded-contextpath "/"

	ProxyPass http://192.168.200.25:8080/bop/api
	ProxyPassReverse  http://192.168.200.25:8080/bop/api

	# Cross-origin resource sharing (CORS)
	SetEnvIf Origin "^http(s):\/\/(.+\.)?(igatec.com)(:\d{1,5})?$" origin_is=$0
	Header always set Access-Control-Allow-Origin %{origin_is}e env=origin_is
	Header set Access-Control-Allow-Credentials "true"
	Header set Access-Control-Allow-Methods "GET, POST, OPTIONS, HEAD, PUT, DELETE, PATCH"
	Header set Access-Control-Allow-Headers "accept,x-requested-method,origin,x-requested-with,x-request,cache-control,content-type,last-modified,x-utc-offset,SecurityContext,X-DS-CSRFTOKEN,keep-alive,DS-API-Version,DS-Client-Step-Name,DS-Request-ID,ENO_CSRF_TOKEN,SecurityToken,DS-Change-Authoring-Context,DS-Configuration-Authoring-Context"
	Header set Access-Control-Expose-Headers "DS-Request-ID,X-DS-CSRFTOKEN,X-3DComment-Has-Moderation-Rights"
	Header set Access-Control-Max-Age "600"
</Location>
```



## Настройка и сборка UI  
Для скачивания зависимостей из внутренного репозитория компании необходимо залогиниться в нем:
```cmd
npm login --registry=https://nexus.igatec.com/repository/pdm-npm-private/
```
  
и настроить npm таким образом, чтоб определенные модули брались из внутренного репозитория:
```cmd
npm config set @raos:registry https://nexus.igatec.com/repository/pdm-npm-private/
```

Для сборки UI части проекта выполнить
```
npm i
npm run build
```

Для сборки в ощий пакет нужно выполнить команду
```
npm run deploy
```
в директории /src/main/resources появится путь /static/ куда запишутся файлы UI и переходим к настройке backend.
## Настройка и сборка BE

Для корректной сборки проекта необходимо указать версию java как в IDE 
так и в сборщике gradle.

В приложение осушествляется проверка лицензии. Чтобы отключить эту фичу в собранном приложении
нужно при сборке или при разворачивании а IDE указать параметр   
```-PexcludeLicense```.

Для сборки готового приложения нужно выполнить команду ```gradle bootJar -PexcludeLicense``` 
 или с лицензией ```gradle bootJar```. Или в меню gradle выбрать команду **bootJar**

После сборки в папке проекта создастся путь **/build/libs** где будет размещаться
 искомый jar архив.

Далее на сервере изменить файл C:\Apache24\conf\httpd.conf (или один из файлов, указанных в директивах include), добавив:
```
      <Location "/bop">
         RequestHeader set x-forwarded-proto "https"
         RequestHeader set x-forwarded-port "443"

         ProxyPass http://127.0.0.1:8002/bop
         ProxyPassReverse  http://127.0.0.1:8002/bop

         # Cross-origin resource sharing (CORS)
         SetEnvIf Origin "^http(s):\/\/(.+\.)?(igatec.com)(:\d{1,5})?$" origin_is=$0
         Header always set Access-Control-Allow-Origin %{origin_is}e env=origin_is
         Header set Access-Control-Allow-Credentials "true"
         Header set Access-Control-Allow-Methods "GET, POST, OPTIONS, HEAD, PUT, DELETE, PATCH"
         Header set Access-Control-Allow-Headers "accept,x-requested-method,origin,x-requested-with,x-request,cache-control,
         content-type,last-modified,x-utc-offset,SecurityContext,X-DS-CSRFTOKEN,keep-alive,DS-API-Version,DS-Client-Step-Name,
         DS-Request-ID,ENO_CSRF_TOKEN,SecurityToken,DS-Change-Authoring-Context,DS-Configuration-Authoring-Context"
         Header set Access-Control-Expose-Headers "DS-Request-ID,X-DS-CSRFTOKEN,X-3DComment-Has-Moderation-Rights"
         Header set Access-Control-Max-Age "600"
      </Location>
```
 И далее перезагрузить сервис httpd (linux **systemctl restart httpd**)

Для запуска приложения на сервере необходимо скопировать jar архив с файлами настроек 
 **application.yml и application-premise.yml** в папку для запуска приложения. 
 И выполнить команду с консоли
```
java -jar -Dspring.profiles.active=cas,premise,remote 
-Dspring.config.location=file:./application.yml,file:./application-premise.yml 
-Dagentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8003 igabop-1.0.0.jar
```
сделав соответствующие изменения в команде.
