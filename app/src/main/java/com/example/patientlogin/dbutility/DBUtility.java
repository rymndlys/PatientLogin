package com.example.patientlogin.dbutility;

public interface DBUtility {

    //connection class
    String jdbcDriverName = "com.mysql.jdbc.Driver";
    String jdbcUrl = "jdbc:mysql://192.168.1.2/isproj2_sympl";
    String dbUserName = "isproj2_sympl";
    String dbPassword = "mJ9r)Gv^Q>[=>%W#";

    //login
    String LOGIN_PATIENT = "select patient_id, contactno, password, name from patient where contactno = ? and password = ?";

    //queueing
    String SELECT_QUEUE="select queue.queue_id from queue " +
            "inner join department on department.department_id = queue.department_id " +
            "inner join doctor on doctor.doctor_id = queue.doctor_id " +
            "where department.name = ? and doctor.name = ? and queue.endtime is null";
    String QUEUE_PATIENT = "insert into instance (patient_id, queue_id, queuetype, status, priority) " +
            "values( ? , ? , ? , ?, ?)";

}

