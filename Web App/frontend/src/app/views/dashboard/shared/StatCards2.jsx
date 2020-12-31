import React, { Component, useState, useEffect } from "react";
import { Grid, Card, Icon, Fab } from "@material-ui/core";
import { fireDb } from "../../../services/firebase/firebaseConfig";

const StatCards2 = () => {
  const [patients, setPatients] = useState([]);
  const [user, setUser] = useState("");
  let docPatients = []


  useEffect(() => {
    async function loadPatients() {
      //fetch the patient list
      const request = await fetch(
        "https://healthymind.herokuapp.com/api/getPatients",
        {}
      );

      const allPatients = await request.json();

      //set the patient list on state
      setPatients(allPatients.patients);

      // console.log(allPatients.patients.length)
    }

    //invoke the function
    loadPatients();
  }, []);

    const authListener = () => {
    fireDb.auth().onAuthStateChanged((user) => {
      // clearInputs();
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

    patients &&
    Object.keys(patients).forEach(function (key) {
      const data = patients[key];
      if (data.docId == user.email) {
        // console.log(data);
        docPatients.push(data)
      }
    });

  let total_patients = docPatients.length;
  let perc_increase = (((total_patients-(total_patients-1)) / (total_patients-1)) * 100).toFixed(
    2
  );
  
  return (
    <Grid container spacing={3} className="mb-24">
      <Grid item xs={12} md={6}>
        <Card elevation={3} className="p-16">
          <div className="flex flex-middle">
            <Fab
              size="medium"
              className="bg-light-green circle-44 box-shadow-none"
            >
              <Icon className="text-green">trending_up</Icon>
            </Fab>
            <h5 className="font-weight-500 text-green m-0 ml-12">
              Active Patients
            </h5>
          </div>
          <div className="pt-16 flex flex-middle">
            <h2 className="m-0 text-muted flex-grow-1">
              {total_patients === 0 ? <h6>No patients available</h6> : total_patients}
            </h2>
            <div className="ml-12 small-circle bg-green text-white">
              <Icon className="small-icon">expand_less</Icon>
            </div>
            <span className="font-size-13 text-green ml-4">
              {" "}
              (+{perc_increase}%)
            </span>
          </div>
        </Card>
      </Grid>
      <Grid item xs={12} md={6}></Grid>
    </Grid>
  );
};

export default StatCards2;
