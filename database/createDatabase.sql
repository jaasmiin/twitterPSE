/*
Erstellt unser aktuelles Datenbanklayout.
Man muss mit einem entsprechend maechtigen benutzer bei MySQL angemeldet sein. (root, Passwort siehe Mail von Paul)
*/

/* anlegen einer Datenbank Twitter */
CREATE DATABASE IF NOT EXISTS twitter;
USE twitter;


/* Accounts-Tabelle */
CREATE TABLE IF NOT EXISTS accounts (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	TwitterAccountId BIGINT UNSIGNED NOT NULL,
	AccountName VARCHAR(30) NOT NULL,
	Verified BIT NOT NULL,
	Follower BIGINT UNSIGNED NOT NULL,
	LocationId INT UNSIGNED NOT NULL,
	URL VARCHAR(100)
);


/* Location-Tabelle */
CREATE TABLE IF NOT EXISTS location (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Name VARCHAR(50) NOT NULL,
	Code VARCHAR(3),
	ParentId INT UNSIGNED
);


/* Category-Tabelle */
CREATE TABLE IF NOT EXISTS category (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Name VARCHAR(50) NOT NULL,
	ParentId INT UNSIGNED
);


/* Retweets-Tabelle */
CREATE TABLE IF NOT EXISTS retweets (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	AccountId INT UNSIGNED NOT NULL,
	LocationId INT UNSIGNED NOT NULL,
	Counter INT UNSIGNED NOT NULL,
	CounterNonLocalized INT UNSIGNED NOT NULL,
	DayId INT UNSIGNED NOT NULL
);


/* Category-Account-Tabelle */
CREATE TABLE IF NOT EXISTS categoryAccount (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	AccountId INT UNSIGNED NOT NULL,
	CategoryId INT UNSIGNED NOT NULL
);


/* Tweets pro Account, erstmal Daten mitnehmen, evtl. spaeter loeschen */
CREATE TABLE IF NOT EXISTS tweets (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	AccountId INT UNSIGNED NOT NULL,
	Counter INT UNSIGNED NOT NULL,
	DayId INT UNSIGNED NOT NULL
);


CREATE TABLE IF NOT EXISTS day (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Day DATE NOT NULL
);


/* Fremdschluessel */
ALTER TABLE accounts ADD CONSTRAINT FOREIGN KEY (LocationId) REFERENCES location (Id);
ALTER TABLE location ADD CONSTRAINT FOREIGN KEY (ParentId) REFERENCES location (Id);
ALTER TABLE category ADD CONSTRAINT FOREIGN KEY (ParentId) REFERENCES category (Id);
ALTER TABLE retweets ADD CONSTRAINT FOREIGN KEY (AccountId) REFERENCES accounts (Id);
ALTER TABLE retweets ADD CONSTRAINT FOREIGN KEY (LocationId) REFERENCES location (Id);
ALTER TABLE tweets ADD CONSTRAINT FOREIGN KEY (AccountId) REFERENCES accounts (Id);
ALTER TABLE tweets ADD CONSTRAINT FOREIGN KEY (DayId) REFERENCES day (Id);
ALTER TABLE retweets ADD CONSTRAINT FOREIGN KEY (DayId) REFERENCES day (Id);
ALTER TABLE categoryAccount ADD CONSTRAINT FOREIGN KEY (AccountId) REFERENCES accounts (Id);
ALTER TABLE categoryAccount ADD CONSTRAINT FOREIGN KEY (CategoryId) REFERENCES category (Id);
/* Unikate erzwingen */
ALTER TABLE accounts ADD CONSTRAINT uc_twitteraccountid UNIQUE (TwitterAccountId);
ALTER TABLE retweets ADD CONSTRAINT uc_retweet UNIQUE (AccountId, DayId, LocationId);
ALTER TABLE tweets ADD CONSTRAINT uc_tweet UNIQUE (AccountId, DayId);
ALTER TABLE day ADD CONSTRAINT uc_day UNIQUE (Day);
ALTER TABLE location ADD CONSTRAINT uc_location UNIQUE (Name);

/* add required entrys for testing */
/*
INSERT INTO location (Name, ParentId) VALUES ("TestLand", NULL);
INSERT INTO category (Name, ParentId) VALUES ("TestCategory", NULL);
*/

