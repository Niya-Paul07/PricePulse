-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: pricepulse
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `price_alerts`
--

LOCK TABLES `price_alerts` WRITE;
/*!40000 ALTER TABLE `price_alerts` DISABLE KEYS */;
INSERT INTO `price_alerts` VALUES (1,4,20.00,'2026-10-20 05:41:34'),(2,4,25.00,'2026-10-20 06:04:40');
/*!40000 ALTER TABLE `price_alerts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `price_entries`
--

LOCK TABLES `price_entries` WRITE;
/*!40000 ALTER TABLE `price_entries` DISABLE KEYS */;
INSERT INTO `price_entries` VALUES (1,2,1,23.00,'2026-10-20'),(2,3,2,15.00,'2026-10-20'),(3,4,2,40.00,'2026-10-20'),(4,5,1,35.00,'2026-10-20'),(5,6,3,35.00,'2026-10-20'),(6,7,1,25.00,'2026-10-22');
/*!40000 ALTER TABLE `price_entries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Test Apple','Fruits','kg'),(2,'tomato','Groceries','Piece'),(3,'tomato','Groceries','Kg'),(4,'potato','Fruits & Vegetables','Kg'),(5,'potato','Fruits & Vegetables','Kg'),(6,'potato','Fruits & Vegetables','Kg'),(7,'milk','Dairy','Litre');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `shops`
--

LOCK TABLES `shops` WRITE;
/*!40000 ALTER TABLE `shops` DISABLE KEYS */;
INSERT INTO `shops` VALUES (1,'freedom','thrikkakara'),(2,'jamal stores','karimmakadu'),(3,'more','thrikkakra'),(4,'njIKDJI','kwkw');
/*!40000 ALTER TABLE `shops` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-22 13:00:08
