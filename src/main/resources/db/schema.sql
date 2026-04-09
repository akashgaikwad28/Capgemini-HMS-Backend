-- Capgemini HMS Database Schema for Supabase (PostgreSQL)

-- 1. Independent Core Tables
CREATE TABLE physician (
    employeeid INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    position VARCHAR(255) NOT NULL,
    ssn INTEGER NOT NULL
);

CREATE TABLE nurse (
    employeeid INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    position VARCHAR(255) NOT NULL,
    registered BOOLEAN NOT NULL,
    ssn INTEGER NOT NULL
);

CREATE TABLE procedures (
    code INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cost DECIMAL(10, 2) NOT NULL
);

CREATE TABLE medication (
    code INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE block (
    blockfloor INTEGER NOT NULL,
    blockcode INTEGER NOT NULL,
    PRIMARY KEY (blockfloor, blockcode)
);

-- 2. Tables with Primary Dependencies
CREATE TABLE department (
    departmentid INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    head INTEGER REFERENCES physician(employeeid)
);

CREATE TABLE patient (
    ssn INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(20) NOT NULL,
    insuranceid INTEGER NOT NULL,
    pcp INTEGER REFERENCES physician(employeeid)
);

CREATE TABLE room (
    roomnumber INTEGER PRIMARY KEY,
    roomtype VARCHAR(255) NOT NULL,
    blockfloor INTEGER NOT NULL,
    blockcode INTEGER NOT NULL,
    unavailable BOOLEAN NOT NULL,
    FOREIGN KEY (blockfloor, blockcode) REFERENCES block(blockfloor, blockcode)
);

-- 3. Association and Scheduling Tables
CREATE TABLE affiliated_with (
    physician INTEGER REFERENCES physician(employeeid),
    department INTEGER REFERENCES department(departmentid),
    primaryaffiliation BOOLEAN NOT NULL,
    PRIMARY KEY (physician, department)
);

CREATE TABLE trained_in (
    physician INTEGER REFERENCES physician(employeeid),
    treatment INTEGER REFERENCES procedures(code),
    certificationdate TIMESTAMP NOT NULL,
    certificationexpires TIMESTAMP NOT NULL,
    PRIMARY KEY (physician, treatment)
);

CREATE TABLE appointment (
    appointmentid INTEGER PRIMARY KEY,
    patient INTEGER REFERENCES patient(ssn),
    prepnurse INTEGER REFERENCES nurse(employeeid),
    physician INTEGER REFERENCES physician(employeeid),
    start TIMESTAMP NOT NULL,
    "end" TIMESTAMP NOT NULL,
    examinationroom TEXT NOT NULL
);

CREATE TABLE stay (
    stayid INTEGER PRIMARY KEY,
    patient INTEGER REFERENCES patient(ssn),
    room INTEGER REFERENCES room(roomnumber),
    staystart TIMESTAMP NOT NULL,
    stayend TIMESTAMP NOT NULL
);

CREATE TABLE on_call (
    nurse INTEGER REFERENCES nurse(employeeid),
    blockfloor INTEGER NOT NULL,
    blockcode INTEGER NOT NULL,
    oncallstart TIMESTAMP NOT NULL,
    oncallend TIMESTAMP NOT NULL,
    PRIMARY KEY (nurse, blockfloor, blockcode, oncallstart, oncallend),
    FOREIGN KEY (blockfloor, blockcode) REFERENCES block(blockfloor, blockcode)
);

-- 4. Prescriptions and Treatment Records
CREATE TABLE prescribes (
    physician INTEGER REFERENCES physician(employeeid),
    patient INTEGER REFERENCES patient(ssn),
    medication INTEGER REFERENCES medication(code),
    date TIMESTAMP NOT NULL,
    appointment INTEGER REFERENCES appointment(appointmentid),
    dose TEXT NOT NULL,
    PRIMARY KEY (physician, patient, medication, date)
);

CREATE TABLE undergoes (
    patient INTEGER REFERENCES patient(ssn),
    procedure INTEGER REFERENCES procedures(code),
    stay INTEGER REFERENCES stay(stayid),
    dateundergoes TIMESTAMP NOT NULL,
    physician INTEGER REFERENCES physician(employeeid),
    assistingnurse INTEGER REFERENCES nurse(employeeid),
    PRIMARY KEY (patient, procedure, stay, dateundergoes)
);
