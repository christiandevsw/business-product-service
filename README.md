# MBBK Session API

MBBK Session API.

## Iniciando

Siga las siguientes instrucciones para iniciar el desarrollo de este proyecto.

### Pre-Requisitos

Plugins que deben estar instalados en su IDE:
* [Lombok](http://projectlombok.org/) - *Libreria de Bytecode que genera automaticamente los Getters y Setters*.
* [CheckStyle](http://www.checkstyle.com/) - *Plugin para poder comprobar el estilo del codigo usando las reglas de Google*
* FindBugs - *Plugin que realiza un an??lisis est??tico para buscar errores en el c??digo en base a patrones de errores.* 

---
Instalar JCE (Java Cryptography Extension)

* [JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)

---

Definir las siguiente variable de entorno:

| Variable | Valor |
| -------- | ----- |
| LOGSTASH_SOCKET | pmbklnxd11:9800 |

Ejemplo para levantar el api en Windows: mvn spring-boot:run -Drun.jvmArguments="-DLOGSTASH_SOCKET=pmbrklnxd11:9800"

## Flujo de desarrollo.

* Todo desarrollo debe iniciarse en una rama con la nomenclatura `feature/nombre-de-cambio` el cual debe crearse desde la rama `develop`.

* Cuando se completa el desarrollo, se deber?? generar un `New Merge Request` desde la rama creada `feature/nombre-de-cambio` hacia la rama `develop`.

* Cuando los cambios son revisados y probados, se aceptar?? el Merge Request, con lo que los cambios quedar??n listos en la rama `develop` para realizar el despliegue en el ambiente de desarrollo.


## Ejecuci??n de pruebas

Para la ejecuci??n de pruebas `unitarias` se debe ejecutar lo siguiente:

```
mvn test
```

Para la ejecuci??n de pruebas de `integraci??n` se debe ejecutar lo siguiente:

```
mvn verify -Dskip.integration.tests=false -Dskip.unit.tests=true
```

## Integraci??n Continua

Los pipeline de build y CI se encuentran definidos en los siguientes archivos:

* `jenkins/Jenkinsfile-ic-build-pipeline.groovy` compila, ejecuta las pruebas unitarias, ejecuta los plugin CheckStyle y FindBugs.
* `jenkins/Jenkinsfile-ic-pipeline.groovy` compila, ejecuta las pruebas unitarias, ejecuta los plugin CheckStyle y FindBugs, hace an??lisis con SonarQube y por ??ltimo sube la versi??n SNAPSHOT del proyecto a artifactory.

Para la ejecuci??n del job de *build*, se debe configurar un webhook para que se ejecute cada vez que se haga un push al proyecto.

La ejecuci??n del job de *CI*, puede ser a demanda, desde la rama `develop`.

## Despliegue



Los pipeline para realizar los despliegues se encuentran en los siguientes archivos:

* `jenkins/Jenkinsfile-delivery-dev-pipeline.groovy` el cual compila, ejecuta las pruebas unitarias, hace an??lisis con SonarQube, sube la versi??n SNAPSHOT del proyecto a artifactory y despliega en el ambiente de desarrollo.
* `jenkins/Jenkinsfile-delivery-cert-pipeline.groovy` el cual crea una nueva rama `release/{version}` desde la ranma `develop`, crea un tag `RC`, sube el proyecto dentro de CERT en artifactory y despliega en el ambiente de certificaci??n.
* `jenkins/Jenkinsfile-delivery-prod-pipeline.groovy` el cual despliega al ambiente de producci??n en base al tag `RC` y hace merge de la rama `release/{version}` a `master`.

Los servidores donde se desplegar?? el proyecto se definen en `devops/ansible/hosts`.

Las variables de entorno para cada ambiente son definidos en los siguientes archivos:

* `devops/deploy/dev-vars.yml` para desarrollo.
* `devops/deploy/cert-vars.yml` para certificaci??n.
* `devops/deploy/prod-vars.yml` para producci??n.

## Jobs en Jenkins

