FROM ubuntu:20.04

ENV TZ=Asia/Kolkata \ DEBIAN_FRONTEND=noninteractive

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

# Librerias para lanzar Java FX
RUN apt-get update && apt-get install -y \
    libxss1 \
    libx11-6 \
    libxext6 \
    libxi6 \
    libxrender1 \
    libxrandr2 \
    libxxf86vm1 \
    libglu1-mesa \
    libfontconfig1 \
    libgdk-pixbuf2.0-0 \
    libgtk-3-0 \
    libgl1-mesa-glx \
    libatk-bridge2.0-0 \
    libatspi2.0-0 \
    libcairo2 \
    libdbus-1-3 \
    libdrm2 \
    libegl1-mesa \
    libgbm1 \
    libice6 \
    libsm6 \
    libwayland-egl1-mesa \
    libxkbcommon0 \
    libxkbcommon-x11-0

# Clonar el repositorio
RUN git clone https://github.com/juliogtrenard/ProyectoBiblioteca.git

# Establecer el directorio
WORKDIR /ProyectoBiblioteca

# Construir el proyecto
RUN mvn clean package

# Ejecutar el JAR
CMD java -jar target/ProyectoBiblioteca-1.0-SNAPSHOT-jar-with-dependencies.jar
