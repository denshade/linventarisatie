DROP TABLE IF EXISTS article_keyword;
DROP TABLE IF EXISTS contact_article;
DROP TABLE IF EXISTS keywordterm;
DROP TABLE IF EXISTS contact;
DROP TABLE IF EXISTS article;


CREATE TABLE IF NOT EXISTS `article` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` mediumtext NOT NULL,
  `type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `keywordterm` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(512) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `contact` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(45) DEFAULT NULL,
  `lastName` varchar(45) DEFAULT NULL,
  `ugent_id` bigint(40) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `article_keyword` (
  `article_id` int(11) NOT NULL,
  `keyword_id` int(11) NOT NULL,
  PRIMARY KEY (`article_id`,`keyword_id`),
  KEY `keyword_id_idx` (`keyword_id`),
  CONSTRAINT `article_keyword_article_id` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `article_keyword_id` FOREIGN KEY (`keyword_id`) REFERENCES `keywordterm` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `article_contact` (
  `contact_id` int(11) NOT NULL,
  `article_id` int(11) NOT NULL,
  PRIMARY KEY (`contact_id`,`article_id`),
  KEY `article_id_idx` (`article_id`),
  CONSTRAINT `article_id` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `contact_id` FOREIGN KEY (`contact_id`) REFERENCES `contact` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `contact_affiliation` (
  `contactid` int(11) NOT NULL,
  `affiliationid` int(11) NOT NULL,
  PRIMARY KEY (`contactid`,`affiliationid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `affiliation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `title_UNIQUE` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SELECT DISTINCT contact.* FROM inventarisatie.article
INNER JOIN article_contact ON article_contact.article_id = article.id
INNER JOIN contact ON contact.id = article_contact.contact_id
WHERE article.id in (
SELECT article.id FROM inventarisatie.article
INNER JOIN article_contact ON article_contact.article_id = article.id
INNER JOIN contact ON contact.id = article_contact.contact_id
WHERE type = 'journalArticle' and  contact.firstname = 'Luc' and contact.lastname='Dupré')
AND contact.lastname != 'Dupré';



SELECT article.* FROM inventarisatie.article
INNER JOIN article_keyword ON article_keyword.article_id= article.id
INNER JOIN keywordterm ON keywordterm.id = article_keyword.keyword_id
WHERE type = 'journalArticle' and  keywordterm.name = 'Iron Age';



