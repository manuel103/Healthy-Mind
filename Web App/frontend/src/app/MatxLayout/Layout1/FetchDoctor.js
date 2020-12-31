import React, { useState, useEffect } from "react";
import BootstrapTable from "react-bootstrap-table-next";
import { fireDb } from "../../services/firebase/firebaseConfig";

export let docName = '';

export default function FetchDoctor() {
//   [docName, setDocName] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [user, setUser] = useState("");

  const authListener = () => {
    fireDb.auth().onAuthStateChanged((user) => {
      if (user) {
        setUser(user);
      } else {
        setUser("");
      }
    });
  };

  useEffect(() => {
    authListener();
  }, []);

  useEffect(() => {
    async function loadDoctors() {
      const request = await fetch("http://localhost:9000/api/getDoctors", {});

      const allDoctors = await request.json();

      setDoctors(allDoctors.doctors);
    }

    //invoke the function
    loadDoctors();
  }, []);

  doctors.forEach(function (arrayItem) {
    if (arrayItem.email === user.email) {
      console.log(arrayItem.fullName);
    //   setDocName(arrayItem.fullName);
    docName = arrayItem.fullName;
    }
  });

  

//   const columns = [
//     {
//       dataField: "fullName",
//       text: "name",
//       style: { cursor: "pointer" },
//     },
//     {
//       dataField: "email",
//       text: "email",
//       style: { cursor: "pointer" },
//     },
//     {
//       dataField: "age",
//       text: "age",
//       style: { cursor: "pointer" },
//     },
//   ];

  return (
    <>
      {/* <BootstrapTable
        striped
        keyField="name"
        data={doctors}
        columns={columns}
      /> */}
    </>
  );
}
