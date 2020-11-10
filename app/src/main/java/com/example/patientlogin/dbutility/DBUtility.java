package com.example.patientlogin.dbutility;

public interface DBUtility {

    //connection class
//    String jdbcDriverName = "com.mysql.jdbc.Driver";
//    String jdbcUrl = "jdbc:mysql://192.168.1.2/keruxfinal";
//    String dbUserName = "KeruxAdmin";
//    String dbPassword = "admin";

    String jdbcDriverName = "com.mysql.jdbc.Driver";
    String jdbcUrl ="jdbc:mysql://192.168.1.6/keruxfinal";
    String dbUserName = "KeruxAdmin";
    String dbPassword = "admin";

    //login
    String LOGIN_PATIENT = "select patient_id, contactno, password, email, firstname, lastname from patient where contactno = ? and password = ?";

    //register patient
    String REGISTER_PATIENT = "insert into patient (email, password, patienttype_id, firstname, lastname, contactno, status) " +
            "values(?, ?, ?, ?, ?, ?, 'Active')";

    //check for existing account
    String CHECK_PATIENT = "select contactno, email from patient where contactno = ? or email = ?";

    //queueing
    String SELECT_QUEUE="select queue.queue_id from queue inner join department " +
            "on department.department_id = queue.department_id inner join doctor " +
            "on doctor.doctor_id = queue.doctor_id where department.name = ? and " +
            "CONCAT(doctor.firstname, ' ', doctor.lastname) = ? and queue.endtime is null";

    //updated queueing
    String QUEUE_PATIENT = "insert into instance (patient_id, queue_id, queuetype, status, priority) " +
            "values( ? , ? , ? , ?, ?)";
    String SELECT_NEW_INSTANCE = "Select MAX(instance_id) from instance";
    String SELECT_COUNT_QUEUELIST = "Select COUNT(queue_id) from queuelist where queue_id=? and status='Active'";
    String INSERT_QUEUE_LIST = "insert into queuelist (queue_id, instance_id, queuenumber, status) values(?, ?, ?, 'Active')";
    String UPDATE_QUEUE_NUMBER = "update instance set queuenumber = ? where instance_id= ?";
    String SELECT_CLINIC="select clinic_id, clinicname, clinichours, clinicdays, status from clinic where status='Active'";
    String VIEW_QUEUE = "";

    String VIEW_PATIENT_QUEUE = "select queuenumber from instance where patient_id = ?";


    //IN THE VIEW PAGE TICKET
    String SELECT_QUEUENUMBER="SELECT queuenumber from instance where instance_id=?";
    String SELECT_CURRENTLY_CALLING="select instance.queuenumber from instance inner join queuelist " +
            "on queuelist.instance_id = instance.instance_id inner join queue " +
            "on queue.queue_id = queuelist.queue_id where queue.queue_id = ? and " +
            "queuelist.status = 'Called'";

    //to retrieve info for edit profile
    String EDIT_PROFILE="select email, password, patienttype_id, name, contactno from patient" +
            "where patient_id = ?";

    //update profile string
    String UPDATE_PROFILE="update patient set email = ?, firstname = ?, lastname = ?, contactno = ? where patient_id =  ?";

    //sql statement for edit profile to not require the input of password when just editing basic patient information
    String UPDATE_PROFILE_PASS = "update patient set password = ?";

    //sql statement to compare the "old password" the patient has inputted in the text field from the one in the database
    String CONFIRM_PATIENT_PASS = "select password from patient where password = ?";
}

