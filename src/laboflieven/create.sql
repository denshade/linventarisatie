CREATE TABLE IF NOT EXISTS `article` (
  `idarticle` int(11) NOT NULL AUTO_INCREMENT,
  `title` mediumtext NOT NULL,
  `type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idarticle`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `keywordterm` (
  `idkeyword` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`idkeyword`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `ugent_contact` (
  `idugent_contact` int(11) NOT NULL,
  `firstName` varchar(45) DEFAULT NULL,
  `lastName` varchar(45) DEFAULT NULL,
  `ugent_id` bigint(40) DEFAULT NULL,
  PRIMARY KEY (`idugent_contact`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `article_keyword` (
  `article_id` int(11) NOT NULL,
  `keyword_id` int(11) NOT NULL,
  PRIMARY KEY (`article_id`,`keyword_id`),
  KEY `keyword_id_idx` (`keyword_id`),
  CONSTRAINT `article_keyword_article_id` FOREIGN KEY (`article_id`) REFERENCES `article` (`idarticle`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `article_keyword_id` FOREIGN KEY (`keyword_id`) REFERENCES `keywordterm` (`idkeyword`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `contact_article` (
  `contact_id` int(11) NOT NULL,
  `article_id` int(11) NOT NULL,
  PRIMARY KEY (`contact_id`,`article_id`),
  KEY `article_id_idx` (`article_id`),
  CONSTRAINT `article_id` FOREIGN KEY (`article_id`) REFERENCES `article` (`idarticle`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `contact_id` FOREIGN KEY (`contact_id`) REFERENCES `ugent_contact` (`idugent_contact`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
