# --- !Ups

CREATE TABLE payrecord(
    id SERIAL,
    paymentAmount 	real 		    NOT NULL,
    balance  		real 		    NOT NULL,
    paymentType 	varchar(255) 	NOT NULL,
    paymentGroup 	varchar(255) 	NOT NULL,
    paymentDate 	date            NOT NULL,
    PRIMARY KEY (id)
    );

CREATE TABLE filelog(
    id SERIAL,
    fileName varchar(255) NOT NULL,
    numberOfRecords bigint NOT NULL,
    status varchar(255) NOT NULL,
    uploadDate timestamp NOT NULL,
    PRIMARY KEY (id)
    );

# --- !Downs

DROP TABLE payrecord;
DROP TABLE filelog;