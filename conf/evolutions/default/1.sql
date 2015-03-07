# --- !Ups

CREATE TABLE payrecord(
    id SERIAL,
    paymentAmount 	numeric 		NOT NULL,
    balance  		numeric 		NOT NULL,
    paymentType 	varchar(255) 	NOT NULL,
    paymentGroup 	varchar(255) 	NOT NULL,
    paymentDate 	date            NOT NULL,
    PRIMARY KEY (id),
    constraint u_constraint unique (balance, paymentDate)
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