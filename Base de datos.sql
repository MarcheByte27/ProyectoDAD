-- MySQL dump 10.13  Distrib 8.0.23, for Win64 (x86_64)
--
-- Host: localhost    Database: dad
-- ------------------------------------------------------
-- Server version	8.0.23

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
-- Table structure for table `gps`
--

DROP TABLE IF EXISTS `gps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gps` (
  `IDGPS` int NOT NULL AUTO_INCREMENT,
  `Posicion` varchar(45) NOT NULL,
  `Marca_Temporal_GPS` datetime NOT NULL,
  `IDVeh` int NOT NULL,
  PRIMARY KEY (`IDGPS`),
  UNIQUE KEY `IDGPS_UNIQUE` (`IDGPS`),
  KEY `IDVeh` (`IDVeh`),
  CONSTRAINT `gps_ibfk_1` FOREIGN KEY (`IDVeh`) REFERENCES `vehiculo` (`IDVeh`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gps`
--

LOCK TABLES `gps` WRITE;
/*!40000 ALTER TABLE `gps` DISABLE KEYS */;
INSERT INTO `gps` VALUES (1,'41.34567244,34.234678','2021-04-18 13:21:17',1),(2,'42.34567244,37.000000','2020-04-18 10:21:17',2),(3,'44.34567244,20.234678','2022-05-13 13:21:17',2),(4,'42.34567244,37.000000','2020-04-18 10:21:17',3),(5,'43.9,89.8','2021-05-03 16:43:46',3),(6,'423343.9,891000.8','2021-05-04 11:20:42',3);
/*!40000 ALTER TABLE `gps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `temperatura`
--

DROP TABLE IF EXISTS `temperatura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `temperatura` (
  `IDTemp` int NOT NULL AUTO_INCREMENT,
  `Valor` float NOT NULL,
  `Marca_Temporal_Temp` datetime NOT NULL,
  `IDVeh` int NOT NULL,
  PRIMARY KEY (`IDTemp`),
  UNIQUE KEY `IDTemp_UNIQUE` (`IDTemp`),
  KEY `IDVeh` (`IDVeh`),
  CONSTRAINT `temperatura_ibfk_1` FOREIGN KEY (`IDVeh`) REFERENCES `vehiculo` (`IDVeh`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `temperatura`
--

LOCK TABLES `temperatura` WRITE;
/*!40000 ALTER TABLE `temperatura` DISABLE KEYS */;
INSERT INTO `temperatura` VALUES (1,35,'2021-02-18 13:17:17',2),(2,35,'2021-02-18 13:17:17',3),(3,30,'2021-03-18 13:17:17',2),(4,32,'2021-03-15 13:17:17',2),(5,45.6,'2021-05-03 16:27:46',3),(6,90.7,'2021-05-04 11:37:06',2);
/*!40000 ALTER TABLE `temperatura` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `IDUsuario` int NOT NULL AUTO_INCREMENT,
  `DNI` varchar(9) NOT NULL,
  `Telefono` int DEFAULT NULL,
  `Nombre` varchar(45) NOT NULL,
  `Direccion` varchar(45) NOT NULL,
  `Fecha_Nacimiento` date NOT NULL,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`IDUsuario`),
  UNIQUE KEY `dni_UNIQUE` (`DNI`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `IDUsuarios_UNIQUE` (`IDUsuario`),
  CONSTRAINT `usuario_chk_1` CHECK (regexp_like(`DNI`,_utf8mb4'[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][A-Z]')),
  CONSTRAINT `usuario_chk_2` CHECK ((length(`Telefono`) = 9))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'29538664A',665582214,'Rigoberto de Castilleja','Buenavista 99','1990-03-20','rigoCasti','rigoCasti'),(2,'29538664B',665582214,'USUARIO1','Buenavista 99','1990-03-20','user1','user1'),(3,'29538664C',665582214,'USUARIO2','Buenavista 99','1990-03-20','user2','user2'),(8,'29538664E',665582214,'Jesús','Buenavista 99','1990-05-27','prueba','prueba');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehiculo`
--

DROP TABLE IF EXISTS `vehiculo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehiculo` (
  `IDVeh` int NOT NULL AUTO_INCREMENT,
  `Matricula` varchar(7) NOT NULL,
  `Modelo` varchar(45) DEFAULT NULL,
  `IDUsuario` int NOT NULL,
  PRIMARY KEY (`IDVeh`),
  UNIQUE KEY `IDVeh_UNIQUE` (`IDVeh`),
  UNIQUE KEY `Matricula_Unique` (`Matricula`),
  KEY `IDUsuario` (`IDUsuario`),
  CONSTRAINT `vehiculo_ibfk_1` FOREIGN KEY (`IDUsuario`) REFERENCES `usuario` (`IDUsuario`),
  CONSTRAINT `vehiculo_chk_1` CHECK (regexp_like(`Matricula`,_utf8mb4'[0-9][0-9][0-9][0-9][A-Z][A-Z][A-Z]'))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehiculo`
--

LOCK TABLES `vehiculo` WRITE;
/*!40000 ALTER TABLE `vehiculo` DISABLE KEYS */;
INSERT INTO `vehiculo` VALUES (1,'1234ABC','Volkswagen',1),(2,'1235ABC','Seat',2),(3,'1236ABC','BMW',2);
/*!40000 ALTER TABLE `vehiculo` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-05-06 16:04:07
