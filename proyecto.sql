-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: proyecto
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `centro_deportivo`
--

DROP TABLE IF EXISTS `centro_deportivo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `centro_deportivo` (
  `rut` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(250) NOT NULL,
  `direccion` varchar(150) NOT NULL,
  `telefono` varchar(10) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `apertura` time DEFAULT NULL,
  `cierre` time DEFAULT NULL,
  `capacidad` int NOT NULL,
  `estado` enum('activo','inactivo','mantenimiento') NOT NULL DEFAULT 'activo',
  PRIMARY KEY (`rut`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `centro_deportivo`
--

LOCK TABLES `centro_deportivo` WRITE;
/*!40000 ALTER TABLE `centro_deportivo` DISABLE KEYS */;
INSERT INTO `centro_deportivo` VALUES (1,'Deportivo Central','Avenida Principal 123','3156789102','contacto@deportivocentral.com','06:00:00','22:00:00',150,'activo'),(2,'Fitness Zone','Calle 45 #67-89','3123456789','info@fitnesszone.com','05:30:00','21:30:00',200,'activo'),(3,'Piscinas Bogotá','Carrera 20 #15-35','3109876543','reservas@piscinasbogota.com','08:00:00','20:00:00',120,'mantenimiento'),(4,'Club Deportivo Andes','Transversal 10 #50-70','3198765432','contacto@clubandes.com','06:30:00','22:30:00',250,'activo'),(5,'Arena Fit','Diagonal 85 #18-90','3134567890','info@arenafit.com','07:00:00','21:00:00',300,'activo'),(6,'Centro Acuático Sur','Calle 30 #15-20','3145678901','contacto@acuaticosur.com','09:00:00','19:00:00',100,'inactivo'),(7,'Gimnasio Elite','Carrera 7 #50-99','3112345678','info@gimnasioelite.com','06:00:00','23:00:00',180,'activo'),(8,'Polideportivo Norte','Avenida 68 #80-120','3129876543','info@polinorte.com','05:00:00','22:00:00',400,'mantenimiento'),(9,'Deportes Familia','Carrera 100 #25-40','3101234567','contacto@deportesfamilia.com','07:30:00','20:30:00',150,'activo'),(10,'Zona Activa','Calle 10 #20-30','3163456789','info@zonaactiva.com','06:30:00','22:30:00',220,'activo');
/*!40000 ALTER TABLE `centro_deportivo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inscripcion`
--

DROP TABLE IF EXISTS `inscripcion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inscripcion` (
  `numero` int NOT NULL AUTO_INCREMENT,
  `fhinscripcion` date NOT NULL,
  `telefono` varchar(10) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `estado` enum('proceso','realizado','postulante') DEFAULT 'postulante',
  `rut` int NOT NULL,
  `id_user` int NOT NULL,
  PRIMARY KEY (`numero`),
  KEY `rut` (`rut`),
  KEY `id_user` (`id_user`),
  CONSTRAINT `inscripcion_ibfk_1` FOREIGN KEY (`rut`) REFERENCES `centro_deportivo` (`rut`),
  CONSTRAINT `inscripcion_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `usuario` (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inscripcion`
--

LOCK TABLES `inscripcion` WRITE;
/*!40000 ALTER TABLE `inscripcion` DISABLE KEYS */;
INSERT INTO `inscripcion` VALUES (1,'2025-03-01','3156789102','juan.perez@gmail.com','realizado',1,1),(2,'2025-03-05','3123456789','maria.gomez@hotmail.com','proceso',2,2),(3,'2025-03-10','3109876543','uis.rodriguez@gmail.com','postulante',3,3),(4,'2025-03-12','3198765432','ana.martinez@gmail.com','realizado',4,4),(5,'2025-03-15','3134567890','carlos.ramirez@gmail.com','proceso',5,5),(6,'2025-03-18','3145678901','laura.torres@hotmail.com','postulante',6,6),(7,'2025-03-20','3112345678','fernando.silva@hotmail.com','realizado',7,7),(8,'2025-03-22','3129876543','diana.lopez@gmail.com','proceso',8,8),(9,'2025-03-25','3101234567','jorge.castillo@gmail.com.com','postulante',10,10),(10,'2025-03-28','3163456789','paula.moreno@hotmail.com','realizado',10,6);
/*!40000 ALTER TABLE `inscripcion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permiso`
--

DROP TABLE IF EXISTS `permiso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permiso` (
  `id` int NOT NULL AUTO_INCREMENT,
  `accion` enum('SELECT','CREATE','INSERT','UPDATE','DELETE') DEFAULT NULL,
  `tabla` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permiso`
--

LOCK TABLES `permiso` WRITE;
/*!40000 ALTER TABLE `permiso` DISABLE KEYS */;
/*!40000 ALTER TABLE `permiso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(25) DEFAULT NULL,
  `contrasena` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol_permiso`
--

DROP TABLE IF EXISTS `rol_permiso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol_permiso` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_rol` int DEFAULT NULL,
  `id_permiso` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_rol_permiso` (`id_rol`),
  KEY `fk_permiso_rol` (`id_permiso`),
  CONSTRAINT `fk_permiso_rol` FOREIGN KEY (`id_permiso`) REFERENCES `permiso` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_rol_permiso` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol_permiso`
--

LOCK TABLES `rol_permiso` WRITE;
/*!40000 ALTER TABLE `rol_permiso` DISABLE KEYS */;
/*!40000 ALTER TABLE `rol_permiso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_user` int NOT NULL AUTO_INCREMENT,
  `documento` int NOT NULL,
  `nombre` varchar(250) NOT NULL,
  `direccion` varchar(150) NOT NULL,
  `telefono` varchar(10) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `estado` enum('activo','inactivo','libre') NOT NULL DEFAULT 'libre',
  PRIMARY KEY (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,12345678,'Juan Pérez','Carrera 10 #15-20','3156789102','juan.perez@gmail.com','activo'),(2,87654321,'María Gómez','Calle 45 #23-67','3123456789','maria.gomez@hotmail.com','activo'),(3,23456789,'Luis Rodríguez','Avenida 68 #80-45','3109876543','uis.rodriguez@gmail.com','libre'),(4,98765432,'Ana Martínez','Transversal 10 #50-70','3198765432','ana.martinez@gmail.com','activo'),(5,34567890,'Carlos Ramírez','Diagonal 85 #18-90','3134567890','carlos.ramirez@gmail.com','activo'),(6,65432109,'Laura Torres','Calle 30 #15-20','3145678901','laura.torres@hotmail.com','activo'),(7,45678901,'Fernando Silva','Carrera 7 #50-99','3112345678','fernando.silva@hotmail.com','activo'),(8,54321098,'Diana López','Avenida Principal 123','3129876543','diana.lopez@gmail.com','libre'),(9,56789012,'Jorge Castillo','Carrera 100 #25-40','3101234567','jorge.castillo@gmail.com.com','activo'),(10,9876543,'Paula Moreno','Calle 10 #20-30','3163456789','paula.moreno@hotmail.com','inactivo'),(11,243,'pelpio','calle wofjsodf','3044695661','carloshernandezsena10@gmail.com','activo');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario_rol`
--

DROP TABLE IF EXISTS `usuario_rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_rol` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int DEFAULT NULL,
  `id_rol` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_usuario_rol` (`id_usuario`),
  KEY `fk_rol_usuario` (`id_rol`),
  CONSTRAINT `fk_rol_usuario` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_usuario_rol` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_user`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario_rol`
--

LOCK TABLES `usuario_rol` WRITE;
/*!40000 ALTER TABLE `usuario_rol` DISABLE KEYS */;
/*!40000 ALTER TABLE `usuario_rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `valoraciones`
--

DROP TABLE IF EXISTS `valoraciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `valoraciones` (
  `codigo` int NOT NULL AUTO_INCREMENT,
  `valoracion` decimal(5,1) DEFAULT NULL,
  `fhvaloracion` date NOT NULL,
  `cometario` text,
  `rut` int NOT NULL,
  `id_user` int DEFAULT NULL,
  PRIMARY KEY (`codigo`),
  KEY `rut` (`rut`),
  CONSTRAINT `valoraciones_ibfk_1` FOREIGN KEY (`rut`) REFERENCES `centro_deportivo` (`rut`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `valoraciones`
--

LOCK TABLES `valoraciones` WRITE;
/*!40000 ALTER TABLE `valoraciones` DISABLE KEYS */;
INSERT INTO `valoraciones` VALUES (1,5.0,'2025-03-01','Excelente servicio, muy recomendado.',1,1),(2,4.0,'2025-03-03','Buen ambiente, pero podría mejorar en limpieza.',2,2),(3,3.0,'2025-03-06','Instalaciones regulares, mantenimiento necesario.',3,3),(4,5.0,'2025-03-09','Muy buena atención y espacios cómodos.',4,4),(5,4.0,'2025-03-12','Buen lugar, pero algo costoso.',5,5),(6,2.0,'2025-03-15','No cumplió mis expectativas, estaba inactivo.',6,6),(7,2.0,'2025-03-18','Excelente gimnasio, todo impecable.',10,10),(8,3.0,'2025-03-21','Aceptable, pero debería mejorar horarios.',8,8),(9,4.0,'2025-03-24','Buen servicio familiar y atención al cliente.',9,9),(10,5.0,'2025-03-28','Un lugar excelente para entrenar.',10,10),(11,3.0,'2025-03-09',NULL,10,10);
/*!40000 ALTER TABLE `valoraciones` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-14 11:17:28
