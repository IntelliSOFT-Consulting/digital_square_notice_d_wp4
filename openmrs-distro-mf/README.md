<img src="readme/openmrs-logo.jpg" alt="OpenMRS Logo" width="500"/>

# OpenMRS Distribution for Micro Frontends


This repository maintains the 'distro POM' for the _OpenMRS Distribution for Micro Frontends_.

It downloads and brings in one place all artifacts needed by the distribution.


Folder structure:
- `docker/` Where to run the Docker Compose project. See below for detailed instructions.
- `micro-frontends/` Npm project to fetch all Micro Frontend modules and compute the corresponding Import Map -using [Packmap](https://github.com/openmrs/packmap). The list of the packaged Micro Frontends is documented in the [package.json](micro-frontends/package.json) file:
```
"dependencies": {
    "@openmrs/esm-api": "^3.0.0",
    "@openmrs/esm-devtools-app": "^2.0.0",
    "@openmrs/esm-error-handling": "^1.1.1",
    ...
```
- `openmrs-config-mf/` A default OpenMRS Config  (processed by [Initializer](https://github.com/mekomsolutions/openmrs-module-initializer)) to configure the OpenMRS backend with a minimal set of metadata (concepts, global properties etc...)
- the root [pom.xml](pom.xml) is where all backend dependencies are set:
```
<spaVersion>1.0.7-SNAPSHOT</spaVersion>
<fhir2Version>1.0.0-SNAPSHOT</fhir2Version>
<legacyuiVersion>1.3.3</legacyuiVersion>
<addresshierarchyVersion>2.12.0</addresshierarchyVersion>
...
```


## Build and run locally:
Build the project:
```bash
mvn clean package
```

And run it locally (OpenMRS backend + MF):
```bash
cd docker/
docker-compose -p mf -f docker-compose-with-spa.yml up
```


-> `http://localhost:8080/openmrs/spa`

<img src="readme/openmrs-login.png" alt="OpenMRS MF Login" width="300"/>

---


This distribution can be used as a parent distribution when implementing any MF-based OpenMRS distribution.

To use it, simply refer to it as a `<parent>` in a child distribution's **pom.xml** file:
```xml
<parent>
  <groupId>net.mekomsolutions</groupId>
  <artifactId>openmrs-distro-mf</artifactId>
  <version>1.0.0</version>
</parent>
```

### Output directory:

* `openmrs_modules/`
<br/>The required set of OpenMRS modules.
* `openmrs_config/`
<br/>The OpenMRS bespoke configuration (more [here](https://github.com/mekomsolutions/openmrs-config-haiti)) to be processed by the [Initializer module](https://github.com/mekomsolutions/openmrs-module-initializer).
* `openmrs_core/`
<br>The target version of OpenMRS Core.</br>
* `frontends/`
<br>The required set of Micro-Frontends.</br>
