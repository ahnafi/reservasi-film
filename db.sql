
CREATE DATABASE nontonFilm;

CREATE TABLE FILM (
                      Film_ID INT PRIMARY KEY AUTO_INCREMENT,
                      Judul VARCHAR(255) NOT NULL,
                      Genre VARCHAR(100),
                      Durasi INT
);

CREATE TABLE STUDIO (
                        Studio_ID INT PRIMARY KEY AUTO_INCREMENT,
                        Nama_Studio VARCHAR(255) NOT NULL,
                        Kapasitas INT NOT NULL
);

CREATE TABLE SHOWTIME (
                          Showtime_ID INT PRIMARY KEY AUTO_INCREMENT,
                          Film_ID INT,
                          Studio_ID INT,
                          Jam_Tayang TIME NOT NULL,
                          FOREIGN KEY (Film_ID) REFERENCES FILM(Film_ID),
                          FOREIGN KEY (Studio_ID) REFERENCES STUDIO(Studio_ID)
);

CREATE TABLE Reservation (
                             Reservation_ID INT PRIMARY KEY AUTO_INCREMENT,
                             Showtime_ID INT,
                             Nomor_Kursi VARCHAR(10),
                             Status ENUM('Locked', 'Confirmed') NOT NULL,
                             FOREIGN KEY (Showtime_ID) REFERENCES SHOWTIME(Showtime_ID)
);

CREATE TABLE Payment (
                         Payment_ID INT PRIMARY KEY AUTO_INCREMENT,
                         Reservation_ID INT,
                         Amount DECIMAL NOT NULL,
                         Payment_Date DATE NOT NULL,
                         Status ENUM('Completed', 'Pending') NOT NULL,
                         FOREIGN KEY (Reservation_ID) REFERENCES Reservation(Reservation_ID)
);

CREATE TABLE Ticket (
                        Ticket_ID INT PRIMARY KEY AUTO_INCREMENT,
                        Reservation_ID INT,
                        Nomor_Kursi VARCHAR(10),
                        Purchase_Date DATE NOT NULL,
                        FOREIGN KEY (Reservation_ID) REFERENCES Reservation(Reservation_ID)
);