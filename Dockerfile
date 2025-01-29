FROM ubuntu:20.04

# Actualizar
RUN apt-get update -y && apt-get upgrade -y

# Instalar dependencias
RUN apt-get install -y wget software-properties-common

# Repositorio del JDK 22
RUN add-apt-repository ppa:openjdk-r/ppa && apt-get update -y

# Instalar OpenJDK 22
RUN apt-get install -y openjdk-22-jdk

# Instalar Maven y Git
RUN apt-get install -y maven git

# Clonar el repositorio
RUN git clone https://github.com/juliogtrenard/ProyectoBiblioteca.git

# Establecer el directorio
WORKDIR /ProyectoBiblioteca

# Construir el proyecto
RUN mvn package

# Ejecutar el JAR
CMD java -jar target/ProyectoBiblioteca-1.0-SNAPSHOT-jar-with-dependencies.jar
