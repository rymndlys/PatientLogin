package com.example.patientlogin.dbutility;

public interface DBUtility {

    //connection class
    String jdbcDriverName = "com.mysql.jdbc.Driver";
    String jdbcUrl = "jdbc:mysql://192.168.1.2/keruxdb2";
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

    //updated queueing
    String QUEUE_PATIENT = "insert into instance (patient_id, queue_id, queuetype, status, priority) " +
            "values( ? , ? , ? , ?, ?)";
    String SELECT_NEW_INSTANCE = "Select MAX(instance_id) from instance";
    String SELECT_COUNT_QUEUELIST = "Select COUNT(queue_id) from queuelist where queue_id=? and status='Active'";
    String INSERT_QUEUE_LIST = "insert into queuelist (queue_id, instance_id, queuenumber, status) values(?, ?, ?, 'Active')";
    String UPDATE_QUEUE_NUMBER = "update instance set queuenumber = ? where instance_id= ?";


    //IN THE VIEW PAGE TICKET
    String SELECT_QUEUENUMBER="SELECT queuenumber from instance where instance_id=?";

    //to retrieve info for edit profile
    String EDIT_PROFILE="select email, password, patienttype_id, name, contactno from patient" +
            "where patient_id = ?";

    //update string
    String UPDATE_PROFILE="update patient set email = ?, password = ?, firstname = ?, lastname = ?, contactno = ? where patient_id =  ?";
}

