IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'ConfeitariaDB')
BEGIN
  CREATE DATABASE ConfeitariaDB;
END
GO