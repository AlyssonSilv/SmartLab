-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema smartlab
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `smartlab` ;

-- -----------------------------------------------------
-- Schema smartlab
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `smartlab` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `smartlab` ;

-- -----------------------------------------------------
-- Table `smartlab`.`setor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartlab`.`setor` (
  `idSetor` INT NOT NULL AUTO_INCREMENT,
  `Nome_setor` VARCHAR(20) NULL DEFAULT NULL,
  `Capacidade` VARCHAR(4) NULL DEFAULT NULL,
  `Descricao` VARCHAR(70) NULL DEFAULT NULL,
  `Localizacao` VARCHAR(20) NULL DEFAULT NULL,
  PRIMARY KEY (`idSetor`))
ENGINE = InnoDB
AUTO_INCREMENT = 27
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `smartlab`.`ambientes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartlab`.`ambientes` (
  `idAmbiente` INT NOT NULL AUTO_INCREMENT,
  `idSetor` INT NULL DEFAULT NULL,
  `nomeAmbiente` VARCHAR(50) NULL DEFAULT NULL,
  `tipoAmbiente` VARCHAR(50) NULL DEFAULT NULL,
  `capacidade` INT NULL DEFAULT NULL,
  `disponibilidade` TINYINT(1) NULL DEFAULT '1',
  PRIMARY KEY (`idAmbiente`),
  INDEX `idSetor` (`idSetor` ASC) VISIBLE,
  CONSTRAINT `ambientes_ibfk_1`
    FOREIGN KEY (`idSetor`)
    REFERENCES `smartlab`.`setor` (`idSetor`))
ENGINE = InnoDB
AUTO_INCREMENT = 41
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `smartlab`.`usuarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartlab`.`usuarios` (
  `idUsuarios` INT NOT NULL AUTO_INCREMENT,
  `Nome` VARCHAR(100) NULL DEFAULT NULL,
  `Telefone` VARCHAR(15) NULL DEFAULT NULL,
  `Email` VARCHAR(100) NULL DEFAULT NULL,
  `Senha` VARCHAR(125) NULL DEFAULT NULL,
  `foto` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`idUsuarios`))
ENGINE = InnoDB
AUTO_INCREMENT = 14
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `smartlab`.`apikeys`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartlab`.`apikeys` (
  `idAPIKey` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NULL DEFAULT NULL,
  `api_key` VARCHAR(255) NULL DEFAULT NULL,
  `descricao` VARCHAR(255) NULL DEFAULT NULL,
  `dataCriacao` DATETIME NULL DEFAULT NULL,
  `status` TINYINT(1) NULL DEFAULT '1',
  PRIMARY KEY (`idAPIKey`),
  INDEX `idUsuario` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `apikeys_ibfk_1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `smartlab`.`usuarios` (`idUsuarios`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `smartlab`.`autenticacao`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartlab`.`autenticacao` (
  `idAutenticacao` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NULL DEFAULT NULL,
  `username` VARCHAR(50) NULL DEFAULT NULL,
  `password_hash` VARCHAR(255) NULL DEFAULT NULL,
  `ultimo_login` DATETIME NULL DEFAULT NULL,
  `perfil` ENUM('admin', 'usuario') NULL DEFAULT NULL,
  PRIMARY KEY (`idAutenticacao`),
  UNIQUE INDEX `username` (`username` ASC) VISIBLE,
  INDEX `idUsuario` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `autenticacao_ibfk_1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `smartlab`.`usuarios` (`idUsuarios`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `smartlab`.`configuracoes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartlab`.`configuracoes` (
  `idConfiguracao` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(50) NULL DEFAULT NULL,
  `valor` VARCHAR(255) NULL DEFAULT NULL,
  `descricao` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`idConfiguracao`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `smartlab`.`horarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartlab`.`horarios` (
  `idHorarios` INT NOT NULL AUTO_INCREMENT,
  `Turno` VARCHAR(5) NULL DEFAULT NULL,
  `Horario` VARCHAR(14) NULL DEFAULT NULL,
  `disponivel` TINYINT NOT NULL,
  PRIMARY KEY (`idHorarios`))
ENGINE = InnoDB
AUTO_INCREMENT = 19
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `smartlab`.`perifericos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartlab`.`perifericos` (
  `idPerifericos` INT NOT NULL AUTO_INCREMENT,
  `Tipo` VARCHAR(45) NULL DEFAULT NULL,
  `Quantidade` INT NULL DEFAULT NULL,
  `quantidade_inicial` INT NULL DEFAULT NULL,
  PRIMARY KEY (`idPerifericos`))
ENGINE = InnoDB
AUTO_INCREMENT = 14
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `smartlab`.`reserva`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartlab`.`reserva` (
  `idReserva` INT NOT NULL AUTO_INCREMENT,
  `fk_id_usuario` INT NULL DEFAULT NULL,
  `fk_id_horario` INT NULL DEFAULT NULL,
  `fk_id_perifericos` INT NULL DEFAULT NULL,
  `dataHora` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `ambientes_idAmbiente` INT NULL DEFAULT NULL,
  `status` ENUM('ativo', 'cancelado') NULL DEFAULT 'ativo',
  PRIMARY KEY (`idReserva`),
  INDEX `fk_id_usuario` (`fk_id_usuario` ASC) VISIBLE,
  INDEX `fk_id_horario` (`fk_id_horario` ASC) VISIBLE,
  INDEX `fk_id_perifericos` (`fk_id_perifericos` ASC) VISIBLE,
  INDEX `fk_reserva_ambientes1_idx` (`ambientes_idAmbiente` ASC) VISIBLE,
  CONSTRAINT `fk_reserva_ambientes1`
    FOREIGN KEY (`ambientes_idAmbiente`)
    REFERENCES `smartlab`.`ambientes` (`idAmbiente`),
  CONSTRAINT `reserva_ibfk_1`
    FOREIGN KEY (`fk_id_usuario`)
    REFERENCES `smartlab`.`usuarios` (`idUsuarios`),
  CONSTRAINT `reserva_ibfk_3`
    FOREIGN KEY (`fk_id_horario`)
    REFERENCES `smartlab`.`horarios` (`idHorarios`),
  CONSTRAINT `reserva_ibfk_4`
    FOREIGN KEY (`fk_id_perifericos`)
    REFERENCES `smartlab`.`perifericos` (`idPerifericos`))
ENGINE = InnoDB
AUTO_INCREMENT = 51
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `smartlab`.`feedback`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartlab`.`feedback` (
  `idFeedback` INT NOT NULL AUTO_INCREMENT,
  `idReserva` INT NULL DEFAULT NULL,
  `idUsuario` INT NULL DEFAULT NULL,
  `comentarios` TEXT NULL DEFAULT NULL,
  `nota` INT NULL DEFAULT NULL,
  `dataFeedback` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`idFeedback`),
  INDEX `idReserva` (`idReserva` ASC) VISIBLE,
  INDEX `idUsuario` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `feedback_ibfk_1`
    FOREIGN KEY (`idReserva`)
    REFERENCES `smartlab`.`reserva` (`idReserva`),
  CONSTRAINT `feedback_ibfk_2`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `smartlab`.`usuarios` (`idUsuarios`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `smartlab`.`historicoreservas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartlab`.`historicoreservas` (
  `idHistorico` INT NOT NULL AUTO_INCREMENT,
  `status` ENUM('ativa', 'cancelada', 'concluída') NULL DEFAULT NULL,
  `dataStatus` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`idHistorico`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `smartlab`.`logs`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartlab`.`logs` (
  `idLog` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NULL DEFAULT NULL,
  `acao` VARCHAR(255) NULL DEFAULT NULL,
  `dataHora` DATETIME NULL DEFAULT NULL,
  `detalhes` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`idLog`),
  INDEX `idUsuario` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `logs_ibfk_1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `smartlab`.`usuarios` (`idUsuarios`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `smartlab`.`notificacoes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartlab`.`notificacoes` (
  `idNotificacao` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NULL DEFAULT NULL,
  `mensagem` TEXT NULL DEFAULT NULL,
  `dataEnvio` DATETIME NULL DEFAULT NULL,
  `statusLido` TINYINT(1) NULL DEFAULT '0',
  PRIMARY KEY (`idNotificacao`),
  INDEX `idUsuario` (`idUsuario` ASC) VISIBLE,
  CONSTRAINT `notificacoes_ibfk_1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `smartlab`.`usuarios` (`idUsuarios`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

USE `smartlab` ;

-- -----------------------------------------------------
-- Placeholder table for view `smartlab`.`view_ambientes_capacidade`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartlab`.`view_ambientes_capacidade` (`idAmbiente` INT, `idSetor` INT, `nomeAmbiente` INT, `tipoAmbiente` INT, `capacidade` INT, `disponibilidade` INT);

-- -----------------------------------------------------
-- procedure DeleteReserva
-- -----------------------------------------------------

DELIMITER $$
USE `smartlab`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteReserva`(
    IN p_idReserva INT,
    IN p_fk_id_horario INT,
    IN p_ambientes_idAmbiente INT,
    IN p_fk_id_perifericos INT
)
BEGIN
    -- Atualizando a disponibilidade do ambiente
    UPDATE ambientes 
    SET disponibilidade = 1 
    WHERE idAmbiente = p_ambientes_idAmbiente;

    -- Atualizando a disponibilidade do horário
    UPDATE horarios 
    SET disponivel = 1 
    WHERE idHorarios = p_fk_id_horario;

    -- Restaurando a quantidade do periférico
    UPDATE perifericos 
    SET Quantidade = quantidade_inicial 
    WHERE idPerifericos = p_fk_id_perifericos;

    -- Deletando a reserva
    DELETE FROM reserva 
    WHERE idReserva = p_idReserva;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- function ListarReservasAtivas
-- -----------------------------------------------------

DELIMITER $$
USE `smartlab`$$
CREATE DEFINER=`root`@`localhost` FUNCTION `ListarReservasAtivas`(p_id_usuario INT) RETURNS json
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE resultado JSON;
    -- Gera uma lista de reservas ativas do usuário em formato JSON
    SELECT JSON_ARRAYAGG(
               JSON_OBJECT(
                   'idReserva', idReserva,
                   'fk_id_horario', fk_id_horario,
                   'fk_id_perifericos', fk_id_perifericos,
                   'dataHora', dataHora,
                   'ambientes_idAmbiente', ambientes_idAmbiente,
                   'status', status
               )
           )
    INTO resultado
    FROM reserva
    WHERE fk_id_usuario = p_id_usuario AND status = 1;

    RETURN resultado;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure sp_inserir_reserva
-- -----------------------------------------------------

DELIMITER $$
USE `smartlab`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_inserir_reserva`(
    IN p_id_usuario INT,
    IN p_id_setor INT,
    IN p_id_horario INT,
    IN p_id_perifericos INT,
    IN p_data_reserva DATETIME
)
BEGIN
    DECLARE new_reserva_id INT;

    -- Inserir nova reserva
    INSERT INTO Reserva (fk_id_usuario, fk_id_setor, fk_id_horario, fk_id_perifericos, dataHora)
    VALUES (p_id_usuario, p_id_setor, p_id_horario, p_id_perifericos, p_data_reserva);
    
    SET new_reserva_id = LAST_INSERT_ID();
    
    -- Registrar log da reserva
    INSERT INTO Logs (idUsuario, acao, dataHora, detalhes)
    VALUES (p_id_usuario, 'Reserva criada', NOW(), CONCAT('Reserva ID: ', new_reserva_id));
END$$

DELIMITER ;

-- -----------------------------------------------------
-- View `smartlab`.`view_ambientes_capacidade`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `smartlab`.`view_ambientes_capacidade`;
USE `smartlab`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `smartlab`.`view_ambientes_capacidade` AS select `smartlab`.`ambientes`.`idAmbiente` AS `idAmbiente`,`smartlab`.`ambientes`.`idSetor` AS `idSetor`,`smartlab`.`ambientes`.`nomeAmbiente` AS `nomeAmbiente`,`smartlab`.`ambientes`.`tipoAmbiente` AS `tipoAmbiente`,`smartlab`.`ambientes`.`capacidade` AS `capacidade`,`smartlab`.`ambientes`.`disponibilidade` AS `disponibilidade` from `smartlab`.`ambientes` order by `smartlab`.`ambientes`.`capacidade` desc;
USE `smartlab`;

DELIMITER $$
USE `smartlab`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `smartlab`.`antes_de_inserir_reserva`
BEFORE INSERT ON `smartlab`.`reserva`
FOR EACH ROW
BEGIN
    SET NEW.status = 'ativo';
END$$

USE `smartlab`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `smartlab`.`apos_reserva_insert`
AFTER INSERT ON `smartlab`.`reserva`
FOR EACH ROW
BEGIN
    -- Atualizar a disponibilidade no horário reservado
    UPDATE horarios
    SET disponivel = 0
    WHERE idHorarios = NEW.fk_id_horario;

    -- Atualizar a disponibilidade do ambiente reservado
    UPDATE ambientes
    SET disponibilidade = 0
    WHERE idAmbiente = NEW.ambientes_idAmbiente;
END$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
