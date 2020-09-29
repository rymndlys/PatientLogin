package com.example.patientlogin.dbutility;

public interface DBUtility {

    //connection class
    String jdbcDriverName = "com.mysql.jdbc.Driver";
    String jdbcUrl = "jdbc:mysql://10.70.0.16/keruxdb";
    String dbUserName = "KeruxAdmin";
    String dbPassword = "admin";

    //login
    String LOGIN_PATIENT = "select patient_id, contactno, password, email, firstname, lastname from patient where contactno = ? and password = ?";

    //register patient
    String REGISTER_PATIENT = "insert into patient (email, password, patienttype_id, name, contactno, status) " +
            "values(?, ?, ?, ?, ?, 'Active')";

    //check for existing account
    String CHECK_PATIENT = "select contactno, email from patient where contactno = ? or email = ?";

    //queueing
    String SELECT_QUEUE="select queue.queue_id from queue " +
            "inner join department on department.department_id = queue.department_id " +
            "inner join doctor on doctor.doctor_id = queue.doctor_id " +
            "where department.name = ? and doctor.name = ? and queue.endtime is null";
    String QUEUE_PATIENT = "insert into instance (patient_id, queue_id, queuetype, status, priority, QueueNumber) " +
            "values( ? , ? , ? , ?, ?, 1)";

    //to retrieve info for edit profile
    String EDIT_PROFILE="select email, password, patienttype_id, name, contactno from patient" +
            "where patient_id = ?";

    //update string
    String UPDATE_PROFILE="update patient " +
            "set email = ?, password = ?, firstname = ?, lastname = ?, contactno = ? " +
            "where patient_id =  ?";
}

