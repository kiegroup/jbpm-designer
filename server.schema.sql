-- 
-- Datenbank: `oryx`
-- 

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `sites`
-- 

CREATE TABLE `sites` (
  `ID` int(11) NOT NULL auto_increment COMMENT 'ID',
  `Name` varchar(255) NOT NULL COMMENT 'Process-Name',
  `Site` text NOT NULL COMMENT 'Process-Site',
  UNIQUE KEY `ID` (`ID`),
  UNIQUE KEY `Name` (`Name`)
);
