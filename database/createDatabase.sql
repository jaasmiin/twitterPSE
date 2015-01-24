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
	URL VARCHAR(100),
	Categorized BIT NOT NULL
);


/* Location-Tabelle */
CREATE TABLE IF NOT EXISTS location (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Name VARCHAR(50) NOT NULL,
	Code VARCHAR(3),
	ParentId INT UNSIGNED
);


/* Location-Freitext-Tabelle */
CREATE TABLE IF NOT EXISTS wordLocation (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	Word VARCHAR(250) NOT NULL,
	TimeZone VARCHAR(200) NOT NULL,
	Location VARCHAR(3) NOT NULL
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
	DayId INT UNSIGNED NOT NULL
);


/* Account-Category-Tabelle */
CREATE TABLE IF NOT EXISTS accountCategory (
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

/* table for DMOZ data */
CREATE TABLE IF NOT EXISTS page (
	Id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
	CategoryId INT UNSIGNED NOT NULL,
	Page VARCHAR(200) NOT NULL
);

/* Tabelle zum Speichern von Daten */
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
ALTER TABLE accountCategory ADD CONSTRAINT FOREIGN KEY (AccountId) REFERENCES accounts (Id);
ALTER TABLE accountCategory ADD CONSTRAINT FOREIGN KEY (CategoryId) REFERENCES category (Id);
ALTER TABLE page ADD CONSTRAINT FOREIGN KEY (CategoryId) REFERENCES category (Id);
/* Unikate erzwingen */
ALTER TABLE accounts ADD CONSTRAINT uc_twitteraccountid UNIQUE (TwitterAccountId);
ALTER TABLE retweets ADD CONSTRAINT uc_retweet UNIQUE (AccountId, DayId, LocationId);
ALTER TABLE tweets ADD CONSTRAINT uc_tweet UNIQUE (AccountId, DayId);
ALTER TABLE day ADD CONSTRAINT uc_day UNIQUE (Day);
ALTER TABLE location ADD CONSTRAINT uc_location UNIQUE (Code);
ALTER TABLE accountCategory ADD CONSTRAINT uc_accountcategory UNIQUE (AccountId, CategoryId);
ALTER TABLE page ADD CONSTRAINT uc_page UNIQUE (CategoryId, Page);

/* Indizes fuer effiziente Abfragen */
CREATE INDEX idxCategoryId ON page(CategoryId);
CREATE INDEX idxPage ON page(Page);
CREATE INDEX idxAccountCategory ON accountCategory(CategoryId);
CREATE INDEX idxTweets1 ON tweets(AccountId);
CREATE INDEX idxRetweets1 ON retweets(AccountId);
CREATE INDEX idxAccounts ON accounts(AccountName);
CREATE INDEX idxTweets2 ON tweets(DayId);
CREATE INDEX idxRetweets2 ON retweets(DayId);
CREATE INDEX idxRetweets3 ON retweets(LocationId);

/* add required entrys for testing */
INSERT INTO location (Name, Code, ParentId) VALUES ("defaultLocation","0",NULL);
