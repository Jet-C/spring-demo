DROP TABLE IF EXISTS VEHICLES;

  CREATE TABLE VEHICLES(
  VIN VARCHAR(17) PRIMARY KEY,
  make VARCHAR(50) NOT NULL,
  model VARCHAR(50) NOT NULL,
  year INT NOT NULL,
  is_older BOOLEAN
  );
 
INSERT INTO vehicles (VIN, make, model, year, is_older) VALUES
  ('FR45212A24D4SED66', 'Ford', 'F-150', 2010, false),
  ('FR4EDED2150RFT5GE', 'Ford', 'Ranger', 1992, null),
  ('XDFR64AE9F3A5R78S', 'Chevrolet', 'Silverado 2500', 2017, false),
  ('XDFR6545DF3A5R896', 'Chevrolet', 'Silverado 1500', 1997, null),
  ('GMDE65A5ED66ER002', 'GMC', 'Sierra', 2012, false),
  ('PQERS2A36458E98CD', 'Nissan', 'Titan', 2013, false),
  ('194678S400005', 'Chevrolet', 'Corvette', 1977, true),
  ('48955460210', 'Ford', 'Mustang', 1974, true);