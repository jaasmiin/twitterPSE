/*
Erstellt unser aktuelles Datenbanklayout.
Man muss mit einem entsprechend maechtigen benutzer bei MySQL angemeldet sein. (root, Passwort siehe Mail von Paul)
*/

/* anlegen einer Datenbank Twitter */
CREATE DATABASE IF NOT EXISTS twittertest;
USE twittertest;


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
/* Unikate erzwingen */
ALTER TABLE accounts ADD CONSTRAINT uc_twitteraccountid UNIQUE (TwitterAccountId);
ALTER TABLE retweets ADD CONSTRAINT uc_retweet UNIQUE (AccountId, DayId, LocationId);
ALTER TABLE tweets ADD CONSTRAINT uc_tweet UNIQUE (AccountId, DayId);
ALTER TABLE day ADD CONSTRAINT uc_day UNIQUE (Day);
ALTER TABLE location ADD CONSTRAINT uc_location UNIQUE (Code);
ALTER TABLE accountCategory ADD CONSTRAINT uc_accountcategory UNIQUE (AccountId, CategoryId);

/* add required entrys for testing */
INSERT INTO location (Name, Code, ParentId) VALUES ("defaultLocation","0",NULL);
INSERT INTO location (Name, Code, ParentId) VALUES ("test0","T0",NULL);
INSERT INTO location (Name, Code, ParentId) VALUES ("test1","T1",NULL);
INSERT INTO location (Name, Code, ParentId) VALUES ("test2","T2",NULL);
INSERT INTO location (Name, Code, ParentId) VALUES ("test3","T3",NULL);
INSERT INTO location (Name, Code, ParentId) VALUES ("test4","T4",NULL);
INSERT INTO location (Name, Code, ParentId) VALUES ("test5","T5",NULL);
INSERT INTO location (Name, Code, ParentId) VALUES ("testParent","TP",2);

INSERT INTO category (Name, ParentId) VALUES ("TestCategory", NULL);
INSERT INTO `category`(`Name`, `ParentId`) VALUES ("testC0", NULL);
INSERT INTO `category`(`Name`, `ParentId`) VALUES ("testC1", NULL);
INSERT INTO `category`(`Name`, `ParentId`) VALUES ("testC2", NULL);
INSERT INTO `category`(`Name`, `ParentId`) VALUES ("testC3", NULL);
INSERT INTO `category`(`Name`, `ParentId`) VALUES ("testC4", NULL);
INSERT INTO `category`(`Name`, `ParentId`) VALUES ("testC5", NULL);
INSERT INTO `category`(`Name`, `ParentId`) VALUES ("testCP", 2);

INSERT INTO `accounts`(`TwitterAccountId`, `AccountName`, `Verified`, `Follower`, `LocationId`, `URL`, `Categorized`) VALUES (0,"Tester0",1,0,8,"url",0);
INSERT INTO `accounts`(`TwitterAccountId`, `AccountName`, `Verified`, `Follower`, `LocationId`, `URL`, `Categorized`) VALUES (1,"Tester1",1,1,1,"url",0);
INSERT INTO `accounts`(`TwitterAccountId`, `AccountName`, `Verified`, `Follower`, `LocationId`, `URL`, `Categorized`) VALUES (2,"Tester2",1,2,1,"url",0);
INSERT INTO `accounts`(`TwitterAccountId`, `AccountName`, `Verified`, `Follower`, `LocationId`, `URL`, `Categorized`) VALUES (3,"Tester3",1,3,5,"url",1);
INSERT INTO `accounts`(`TwitterAccountId`, `AccountName`, `Verified`, `Follower`, `LocationId`, `URL`, `Categorized`) VALUES (4,"Tester4",1,4,5,"url",0);
INSERT INTO `accounts`(`TwitterAccountId`, `AccountName`, `Verified`, `Follower`, `LocationId`, `URL`, `Categorized`) VALUES (5,"Tester5",0,5,8,"url",0);

INSERT INTO `day`(`Day`) VALUES ("2000-01-01");
INSERT INTO `day`(`Day`) VALUES ("2000-01-02");
INSERT INTO `day`(`Day`) VALUES ("2000-01-03");
INSERT INTO `day`(`Day`) VALUES ("2000-01-04");