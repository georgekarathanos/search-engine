CREATE DATABASE IF NOT EXISTS song_search;
USE song_search;

DROP TABLE IF EXISTS search;

CREATE TABLE search(
  slot int NOT NULL,
  sentence text DEFAULT NULL,
  top_song text DEFAULT NULL,
  suggestion text DEFAULT NULL,
  PRIMARY KEY (slot)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;