import React, { useEffect, useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";
import FormHelperText from "@material-ui/core/FormHelperText";
import FormControl from "@material-ui/core/FormControl";
import Select from "@material-ui/core/Select";
// import './PatientsList.css'

const useStyles = makeStyles((theme) => ({
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
  },
  selectEmpty: {
    marginTop: theme.spacing(2),
  },
}));


export default function PatientsList() {
  const classes = useStyles();
  const [name, setName] = React.useState("");

  const handleChange = (event) => {
    setName(event.target.value);
  };

  //create state to store our patient list
  const [patients, setPatients] = useState([]);

  useEffect(() => {
    async function loadPatients() {
      //fetch the patient list
      const request = await fetch("http://localhost:9000/api/getPatients", {
        //use the authorization https://www.youtube.com/watch?v=T3Px88x_PsA http://localhost:3000/#/app/main/dashboard
        // headers: {
        //   Authorization: "Bearer " + localStorage.getItem("@token"),
        // },
      });

      const allPatients = await request.json();
      //set the patient list on state
      setPatients(allPatients.patients);
    }

    //invoke the function
    loadPatients();
  }, []);

  let optionItems = patients.map((patient) => (
    <MenuItem className="menu-item" value={patient.name}>{patient.name}</MenuItem>
  ));

  return (
    <FormControl variant="outlined" className={classes.formControl}>
      <Select
        className="select-tag"
        labelId="demo-simple-select-outlined-label"
        id="demo-simple-select-outlined"
        value={name}
        onChange={handleChange}
        label="Name"
      >
        {/* <MenuItem value="">
          <em>None</em>
        </MenuItem> */}
        {optionItems}
      </Select>
    </FormControl>
  );
}
