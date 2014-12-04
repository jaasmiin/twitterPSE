/*
Erstellt unser aktuelles Datenbanklayout.
Man muss mit einem entsprechend maechtigen benutzer bei MySQL angemeldet sein. (root, Passwort siehe Mail von Paul)
*/

/* anlegen einer Datenbank Twitter */
CREATE DATABASE IF NOT EXISTS Twitter;
USE Twitter;


/* Accounts-Tabelle */
CREATE TABLE IF NOT EXISTS accounts (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	AccountId BIGINT UNSIGNED NOT NULL,
	AccountName VARCHAR(30) CHARACTER SET utf32 COLLATE utf32_unicode_520_ci NOT NULL,
	Verified BIT NOT NULL,
	Follower BIGINT UNSIGNED NOT NULL,
	Location INT UNSIGNED NOT NULL,
	UnlocalizedRetweets INT UNSIGNED NOT NULL
);


/* Location-Tabelle */
CREATE TABLE IF NOT EXISTS location (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Name VARCHAR(50) NOT NULL,
	Parent INT UNSIGNED
);


/* Category-Tabelle */
CREATE TABLE IF NOT EXISTS category (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Name VARCHAR(50) NOT NULL,
	Parent INT UNSIGNED
);


/* Retweets-Tabelle */
CREATE TABLE IF NOT EXISTS retweets (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Account INT UNSIGNED NOT NULL,
	Location INT UNSIGNED NOT NULL,
	Counter INT UNSIGNED NOT NULL,
	Day INT UNSIGNED NOT NULL
);


/* Category-Account-Tabelle */
CREATE TABLE IF NOT EXISTS categoryAccount (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Account INT UNSIGNED NOT NULL,
	Category INT UNSIGNED NOT NULL
);


/* Tweets pro Account, erstmal Daten mitnehmen, evtl. spaeter loeschen */
CREATE TABLE IF NOT EXISTS tweets (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Account INT UNSIGNED NOT NULL,
	Counter INT UNSIGNED NOT NULL,
	Day INT UNSIGNED NOT NULL
);


CREATE TABLE IF NOT EXISTS day (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Day DATE NOT NULL
);


/* Fremdschluessel */
ALTER TABLE accounts ADD CONSTRAINT FOREIGN KEY (Location) REFERENCES Llocation (Id);
ALTER TABLE location ADD CONSTRAINT FOREIGN KEY (Parent) REFERENCES location (Id);
ALTER TABLE category ADD CONSTRAINT FOREIGN KEY (Parent) REFERENCES category (Id);
ALTER TABLE retweets ADD CONSTRAINT FOREIGN KEY (Account) REFERENCES accounts (Id);
ALTER TABLE retweets ADD CONSTRAINT FOREIGN KEY (Location) REFERENCES location (Id);
ALTER TABLE tweets ADD CONSTRAINT FOREIGN KEY (Account) REFERENCES accounts (Id);
ALTER TABLE tweets ADD CONSTRAINT FOREIGN KEY (Day) REFERENCES day (Id);
ALTER TABLE retweets ADD CONSTRAINT FOREIGN KEY (Day) REFERENCES day (Id);
ALTER TABLE categoryAccount ADD CONSTRAINT FOREIGN KEY (Account) REFERENCES accounts (Id);
ALTER TABLE categoryAccount ADD CONSTRAINT FOREIGN KEY (Category) REFERENCES category (Id);
/* Unikate erzwingen */
ALTER TABLE accounts ADD CONSTRAINT uc_accountid UNIQUE (AccountId);
ALTER TABLE retweets ADD CONSTRAINT uc_retweet UNIQUE (Account, Day, Location);
ALTER TABLE tweets ADD CONSTRAINT uc_tweet UNIQUE (Account, Day);
ALTER TABLE day ADD CONSTRAINT uc_day UNIQUE (Day);
ALTER TABLE location ADD CONSTRAINT uc_location UNIQUE (Name);
ALTER TABLE retweets ADD CONSTRAINT uc_retweet UNIQUE (Account, Day, Location);
ALTER TABLE tweets ADD CONSTRAINT uc_tweet UNIQUE (Account, Day);
ALTER TABLE day ADD CONSTRAINT uc_day UNIQUE (Day);
