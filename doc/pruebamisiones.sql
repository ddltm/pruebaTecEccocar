-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 13-12-2021 a las 21:33:42
-- Versión del servidor: 10.4.22-MariaDB
-- Versión de PHP: 8.0.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `pruebamisiones`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mision`
--

CREATE TABLE `mision` (
  `identificador` int(11) NOT NULL COMMENT 'Identificador de misión',
  `feciniciomision` datetime NOT NULL COMMENT 'Fecha de inicio de la misión',
  `fecfinmision` datetime NOT NULL COMMENT 'Fecha de fin de la misión',
  `tripulacion` int(11) NOT NULL COMMENT 'Número de miembros de la tripulación',
  `capitanes` varchar(100) NOT NULL COMMENT 'Listado de capitanes',
  `planetas` varchar(100) NOT NULL COMMENT 'Listado de planetas',
  `nave` int(11) NOT NULL COMMENT 'Identificador de Nave'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `mision`
--

INSERT INTO `mision` (`identificador`, `feciniciomision`, `fecfinmision`, `tripulacion`, `capitanes`, `planetas`, `nave`) VALUES
(1, '2021-01-01 10:00:00', '2021-02-01 12:00:00', 12345, '1,3,4,6,7', '2,5,6,7,8', 2),
(2, '2021-02-01 13:00:00', '2021-03-01 09:00:00', 54321, '1,4,6,7', '2,7,8', 3),
(4, '2021-01-01 13:00:00', '2021-01-01 17:00:00', 1000, '2,5', '3,4,5,6', 2),
(5, '2021-03-10 10:00:00', '2021-03-11 17:00:00', 65, '7,5', '3,4,5', 5),
(6, '2021-04-20 06:00:00', '2021-05-07 23:00:00', 4, '7,5,13', '3,4,5,6', 10),
(8, '2020-04-20 06:00:00', '2020-05-07 23:00:00', 4, '7,5,13', '3,4,5,6', 10),
(9, '2019-04-20 06:00:00', '2019-05-06 23:00:00', 6, '7,5,13', '3,4,5,6', 10);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `mision`
--
ALTER TABLE `mision`
  ADD PRIMARY KEY (`identificador`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `mision`
--
ALTER TABLE `mision`
  MODIFY `identificador` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identificador de misión', AUTO_INCREMENT=10;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
